package com.mobile.rickydemo.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CharacterResponse(
    val info: InfoDto,
    val results: List<CharacterDto>
)

data class InfoDto(
    val count: Int,
    val pages: Int,
    val next: String?,
    val prev: String?
)

data class CharacterDto(
    val id: Int,
    val name: String,
    val status: String,
    val species: String,
    val type: String,
    val gender: String,
    val origin: LocationDto,
    val location: LocationDto,
    val image: String,
    val episode: List<String>,
    val url: String,
    val created: String
)

data class LocationDto(
    val name: String,
    val url: String
)
