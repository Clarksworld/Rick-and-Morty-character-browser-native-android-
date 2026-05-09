package com.mobile.rickydemo.ui.character_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.mobile.rickydemo.domain.model.Character
import com.mobile.rickydemo.domain.use_case.GetCharactersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class CharacterListViewModel @Inject constructor(
    private val getCharactersUseCase: GetCharactersUseCase
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _isSearching = MutableStateFlow(true)
    val isSearching = _isSearching.asStateFlow()

    private val _statusFilter = MutableStateFlow("")
    val statusFilter = _statusFilter.asStateFlow()

    private val _speciesFilter = MutableStateFlow("")
    val speciesFilter = _speciesFilter.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val characters: StateFlow<PagingData<Character>> = combine(
        _searchQuery,
        _statusFilter,
        _speciesFilter
    ) { query, status, species ->
        Triple(query, status, species)
    }.debounce(300)
        .distinctUntilChanged()
        .onEach { _isSearching.value = true }
        .flatMapLatest { (query, status, species) ->
            getCharactersUseCase(query, status, species)
        }
        .onEach { _isSearching.value = false }
        .cachedIn(viewModelScope)
        .stateIn(viewModelScope, SharingStarted.Lazily, PagingData.empty())

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        // Immediate feedback: if typing, we are searching
        _isSearching.value = true
    }

    fun onStatusFilterChange(status: String) {
        val newStatus = if (_statusFilter.value == status) "" else status
        _statusFilter.value = newStatus
        _isSearching.value = true
    }

    fun onSpeciesFilterChange(species: String) {
        val newSpecies = if (_speciesFilter.value == species) "" else species
        _speciesFilter.value = newSpecies
        _isSearching.value = true
    }
}
