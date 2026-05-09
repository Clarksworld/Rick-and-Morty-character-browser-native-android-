package com.mobile.rickydemo.domain.use_case

import com.mobile.rickydemo.domain.model.Character
import com.mobile.rickydemo.domain.repository.CharacterRepository
import javax.inject.Inject

class GetCharacterUseCase @Inject constructor(
    private val repository: CharacterRepository
) {
    suspend fun execute(id: Int): Character? {
        return repository.getCharacterById(id)
    }
}
