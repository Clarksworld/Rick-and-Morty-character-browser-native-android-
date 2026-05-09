package com.mobile.rickydemo.data.repository

import com.mobile.rickydemo.data.mapper.toEpisode
import com.mobile.rickydemo.data.remote.api.RickAndMortyApi
import com.mobile.rickydemo.domain.model.Episode
import com.mobile.rickydemo.domain.repository.EpisodeRepository
import javax.inject.Inject

class EpisodeRepositoryImpl @Inject constructor(
    private val api: RickAndMortyApi
) : EpisodeRepository {

    override suspend fun getEpisodes(ids: List<Int>): List<Episode> {
        return try {
            val idsString = ids.joinToString(",")
            api.getEpisodes(idsString).map { it.toEpisode() }
        } catch (e: Exception) {
            emptyList()
        }
    }
}
