package com.mobile.rickydemo.domain.repository

import androidx.paging.PagingData
import com.mobile.rickydemo.domain.model.Character
import kotlinx.coroutines.flow.Flow

interface CharacterRepository {
    fun getCharacters(query: String, status: String, species: String): Flow<PagingData<Character>>
    suspend fun getCharacterById(id: Int): Character?
}
