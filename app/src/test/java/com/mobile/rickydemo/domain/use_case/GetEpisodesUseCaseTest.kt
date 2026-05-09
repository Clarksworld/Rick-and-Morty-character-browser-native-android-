package com.mobile.rickydemo.domain.use_case

import com.mobile.rickydemo.domain.model.Episode
import com.mobile.rickydemo.domain.repository.EpisodeRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetEpisodesUseCaseTest {

    private lateinit var getEpisodesUseCase: GetEpisodesUseCase
    private val repository: EpisodeRepository = mockk()

    @Before
    fun setUp() {
        getEpisodesUseCase = GetEpisodesUseCase(repository)
    }

    @Test
    fun `execute should return first 3 episodes when more than 3 urls are provided`() = runBlocking {
        // Given
        val episodeUrls = listOf(
            "https://rickandmortyapi.com/api/episode/1",
            "https://rickandmortyapi.com/api/episode/2",
            "https://rickandmortyapi.com/api/episode/3",
            "https://rickandmortyapi.com/api/episode/4"
        )
        val expectedEpisodes = listOf(
            Episode(1, "Pilot", "December 2, 2013", "S01E01"),
            Episode(2, "Lawnmower Dog", "December 9, 2013", "S01E02"),
            Episode(3, "Anatomy Park", "December 16, 2013", "S01E03")
        )
        coEvery { repository.getEpisodes(listOf(1, 2, 3)) } returns expectedEpisodes

        // When
        val result = getEpisodesUseCase.execute(episodeUrls)

        // Then
        assertEquals(3, result.size)
        assertEquals(expectedEpisodes, result)
    }

    @Test
    fun `execute should return empty list when no urls are provided`() = runBlocking {
        // When
        val result = getEpisodesUseCase.execute(emptyList())

        // Then
        assertEquals(0, result.size)
    }
}
