package com.mobile.rickydemo.ui.character_detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobile.rickydemo.domain.model.Character
import com.mobile.rickydemo.domain.model.Episode
import com.mobile.rickydemo.domain.use_case.GetCharacterUseCase
import com.mobile.rickydemo.domain.use_case.GetEpisodesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CharacterDetailViewModel @Inject constructor(
    private val getCharacterUseCase: GetCharacterUseCase,
    private val getEpisodesUseCase: GetEpisodesUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow<CharacterDetailState>(CharacterDetailState.Loading)
    val state: StateFlow<CharacterDetailState> = _state.asStateFlow()

    init {
        savedStateHandle.get<Int>("characterId")?.let { id ->
            loadCharacter(id)
        }
    }

    fun loadCharacter(id: Int) {
        viewModelScope.launch {
            _state.value = CharacterDetailState.Loading
            val character = getCharacterUseCase.execute(id)
            if (character != null) {
                val episodes = getEpisodesUseCase.execute(character.episodeUrls)
                _state.value = CharacterDetailState.Success(character, episodes)
            } else {
                _state.value = CharacterDetailState.Error("Character not found")
            }
        }
    }
}

sealed class CharacterDetailState {
    object Loading : CharacterDetailState()
    data class Success(val character: Character, val episodes: List<Episode>) : CharacterDetailState()
    data class Error(val message: String) : CharacterDetailState()
}
