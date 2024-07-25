package es.jvbabi.vplanplus.feature.main_home.feature_search.ui

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.jvbabi.vplanplus.domain.model.Lesson
import es.jvbabi.vplanplus.domain.model.Profile
import es.jvbabi.vplanplus.domain.model.ProfileType
import es.jvbabi.vplanplus.domain.model.RoomBooking
import es.jvbabi.vplanplus.feature.main_home.feature_search.domain.usecase.SearchUseCases
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZonedDateTime
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchUseCases: SearchUseCases
) : ViewModel() {
    val state = mutableStateOf(SearchState())

    private var searchJob: Job? = null

    init {
        viewModelScope.launch {
            combine(
                listOf(
                    searchUseCases.getCurrentProfileUseCase(),
                    searchUseCases.isSyncRunningUseCase(),
                    searchUseCases.getCurrentTimeUseCase()
                )
            ) { data ->
                val currentProfile = data[0] as Profile?
                val isSyncRunning = data[1] as Boolean
                val time = data[2] as ZonedDateTime

                state.value.copy(
                    currentProfile = currentProfile,
                    isSyncRunning = isSyncRunning,
                    time = time
                )
            }.collect {
                state.value = it
            }
        }
    }

    fun onQueryChange(query: String) {
        state.value = state.value.copy(query = query, results = emptyList(), isSearchRunning = true)
        if (query.isBlank()) {
            state.value = state.value.copy(results = emptyList(), isSearchRunning = false)
            return
        }
        doSearch()
    }

    private fun doSearch() {
        searchJob?.cancel()
        state.value = state.value.copy(isSearchRunning = true)

        searchJob = viewModelScope.launch {
            val results = searchUseCases.searchUseCase(state.value.query, state.value.selectedDate)
            state.value = state.value.copy(
                results = results,
                isSearchRunning = false
            )
        }
    }

    fun onSetDate(date: LocalDate?) {
        state.value = state.value.copy(selectedDate = date ?: LocalDate.now())
        doSearch()
    }
}

data class SearchState(
    val selectedDate: LocalDate = LocalDate.now(),
    val query: String = "",
    val currentProfile: Profile? = null,
    val isSyncRunning: Boolean = false,
    val results: List<SearchResult> = emptyList(),
    val isSearchRunning: Boolean = false,
    val time: ZonedDateTime = ZonedDateTime.now()
)

data class SearchResult(
    val name: String,
    val type: ProfileType,
    val school: String,
    val lessons: List<Lesson>?,
    val bookings: List<RoomBooking>
)