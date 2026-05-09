package com.mobile.rickydemo.data.remote.api

import com.mobile.rickydemo.data.remote.dto.CharacterResponse
import com.mobile.rickydemo.data.remote.dto.EpisodeDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RickAndMortyApi {

    @GET("character")
    suspend fun getCharacters(
        @Query("page") page: Int,
        @Query("name") name: String? = null,
        @Query("status") status: String? = null,
        @Query("species") species: String? = null
    ): CharacterResponse

    @GET("episode/{ids}")
    suspend fun getEpisodes(
        @Path("ids") ids: String
    ): List<EpisodeDto>

    @GET("episode/{id}")
    suspend fun getEpisode(
        @Path("id") id: Int
    ): EpisodeDto

    companion object {
        const val BASE_URL = "https://rickandmortyapi.com/api/"
    }
}
