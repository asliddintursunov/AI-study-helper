package com.aistudyhelper.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.aistudyhelper.models.StudyResult
import com.aistudyhelper.storage.ResultsStorage
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ResultsViewModel(application: Application) : AndroidViewModel(application) {
    private val resultsStorage = ResultsStorage(application)

    val results: StateFlow<List<StudyResult>> = resultsStorage.getResults().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    fun deleteResult(result: StudyResult) {
        viewModelScope.launch {
            resultsStorage.deleteResult(result)
        }
    }
}
