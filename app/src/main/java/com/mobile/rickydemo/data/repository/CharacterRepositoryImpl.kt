package com.mobile.rickydemo.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.mobile.rickydemo.data.local.RickAndMortyDatabase
import com.mobile.rickydemo.data.mapper.toCharacter
import com.mobile.rickydemo.data.remote.api.RickAndMortyApi
import com.mobile.rickydemo.domain.model.Character
import com.mobile.rickydemo.domain.repository.CharacterRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CharacterRepositoryImpl @Inject constructor(
    private val api: RickAndMortyApi,
    private val database: RickAndMortyDatabase
) : CharacterRepository {

    @OptIn(ExperimentalPagingApi::class)
    override fun getCharacters(
        query: String,
        status: String,
        species: String
    ): Flow<PagingData<Character>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                prefetchDistance = 2,
                enablePlaceholders = false
            ),
            remoteMediator = CharacterRemoteMediator(
                query = query,
                status = status,
                species = species,
                api = api,
                database = database
            ),
            pagingSourceFactory = {
                database.characterDao().getCharacters(query, status, species)
            }
        ).flow.map { pagingData ->
            pagingData.map { it.toCharacter() }
        }
    }

    override suspend fun getCharacterById(id: Int): Character? {
        return database.characterDao().getCharacterById(id)?.toCharacter()
    }
}
