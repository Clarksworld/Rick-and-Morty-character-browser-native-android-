package com.mobile.rickydemo.domain.repository

import com.mobile.rickydemo.domain.model.Episode

interface EpisodeRepository {
    suspend fun getEpisodes(ids: List<Int>): List<Episode>
}
