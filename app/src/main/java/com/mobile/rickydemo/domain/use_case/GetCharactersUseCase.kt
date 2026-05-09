package com.mobile.rickydemo.domain.use_case

import androidx.paging.PagingData
import com.mobile.rickydemo.domain.model.Character
import com.mobile.rickydemo.domain.repository.CharacterRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCharactersUseCase @Inject constructor(
    private val repository: CharacterRepository
) {
    operator fun invoke(query: String, status: String, species: String): Flow<PagingData<Character>> {
        return repository.getCharacters(query, status, species)
    }
}
