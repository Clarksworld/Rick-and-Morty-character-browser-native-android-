package com.mobile.rickydemo.data.mapper

import com.mobile.rickydemo.data.remote.dto.EpisodeDto
import com.mobile.rickydemo.domain.model.Episode

fun EpisodeDto.toEpisode(): Episode {
    return Episode(
        id = id,
        name = name,
        airDate = air_date,
        episode = episode
    )
}
