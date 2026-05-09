package com.mobile.rickydemo.domain.use_case

import com.mobile.rickydemo.domain.model.Episode
import com.mobile.rickydemo.domain.repository.EpisodeRepository
import javax.inject.Inject

class GetEpisodesUseCase @Inject constructor(
    private val repository: EpisodeRepository
) {
    suspend fun execute(episodeUrls: List<String>): List<Episode> {
        val ids = episodeUrls.mapNotNull { url ->
            url.split("/").lastOrNull()?.toIntOrNull()
        }.take(3) // Only first 3 episodes as per requirement
        
        return if (ids.isNotEmpty()) {
            repository.getEpisodes(ids)
        } else {
            emptyList()
        }
    }
}
