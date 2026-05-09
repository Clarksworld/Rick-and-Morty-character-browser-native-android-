package com.mobile.rickydemo.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.mobile.rickydemo.data.local.RickAndMortyDatabase
import com.mobile.rickydemo.data.local.entity.CharacterEntity
import com.mobile.rickydemo.data.local.entity.RemoteKeys
import com.mobile.rickydemo.data.mapper.toCharacterEntity
import com.mobile.rickydemo.data.remote.api.RickAndMortyApi
import retrofit2.HttpException
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class CharacterRemoteMediator(
    private val query: String,
    private val status: String,
    private val species: String,
    private val api: RickAndMortyApi,
    private val database: RickAndMortyDatabase
) : RemoteMediator<Int, CharacterEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, CharacterEntity>
    ): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: 1
            }
            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                val prevKey = remoteKeys?.prevKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                prevKey
            }
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                val nextKey = remoteKeys?.nextKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                nextKey
            }
        }

        return try {
            val response = api.getCharacters(
                page = page,
                name = query.ifEmpty { null },
                status = status.ifEmpty { null },
                species = species.ifEmpty { null }
            )

            val characters = response.results
            val endOfPaginationReached = characters.isEmpty() || response.info.next == null

            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    database.remoteKeysDao().clearRemoteKeys()
                    database.characterDao().clearAll()
                }
                val prevKey = if (page == 1) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1
                val keys = characters.map {
                    RemoteKeys(characterId = it.id, prevKey = prevKey, nextKey = nextKey)
                }
                database.remoteKeysDao().insertAll(keys)
                database.characterDao().insertAll(characters.map { it.toCharacterEntity(page) })
            }
            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (e: IOException) {
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            if (e.code() == 404) {
                // Rick and Morty API returns 404 when no characters match the search filter.
                // If this happens during a refresh, we must clear the database to show the empty state.
                if (loadType == LoadType.REFRESH) {
                    database.withTransaction {
                        database.remoteKeysDao().clearRemoteKeys()
                        database.characterDao().clearAll()
                    }
                }
                MediatorResult.Success(endOfPaginationReached = true)
            } else {
                MediatorResult.Error(e)
            }
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, CharacterEntity>): RemoteKeys? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { character ->
                database.remoteKeysDao().getRemoteKeysForCharacter(character.id)
            }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, CharacterEntity>): RemoteKeys? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { character ->
                database.remoteKeysDao().getRemoteKeysForCharacter(character.id)
            }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, CharacterEntity>): RemoteKeys? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { id ->
                database.remoteKeysDao().getRemoteKeysForCharacter(id)
            }
        }
    }
}
