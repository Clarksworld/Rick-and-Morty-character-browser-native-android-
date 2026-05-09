package com.mobile.rickydemo.data.mapper

import com.mobile.rickydemo.data.local.entity.CharacterEntity
import com.mobile.rickydemo.data.remote.dto.CharacterDto
import com.mobile.rickydemo.domain.model.Character

fun CharacterDto.toCharacterEntity(page: Int): CharacterEntity {
    return CharacterEntity(
        id = id,
        name = name,
        status = status,
        species = species,
        type = type,
        gender = gender,
        originName = origin.name,
        locationName = location.name,
        image = image,
        episodeUrls = episode,
        page = page
    )
}

fun CharacterEntity.toCharacter(): Character {
    return Character(
        id = id,
        name = name,
        status = status,
        species = species,
        type = type,
        gender = gender,
        origin = originName,
        location = locationName,
        image = image,
        episodeUrls = episodeUrls
    )
}
