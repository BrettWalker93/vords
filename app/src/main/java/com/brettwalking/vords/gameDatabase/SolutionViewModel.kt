package com.brettwalking.vords.gameDatabase

import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.launch



class SolutionViewModel(private val repository: SolutionRepository) : ViewModel() {

    // Using LiveData and caching what allWords returns has several benefits:
    // - We can put an observer on the data (instead of polling for changes) and only update the
    //   the UI when the data actually changes.
    // - Repository is completely separated from the UI through the ViewModel.
    val allSolutions: LiveData<List<Solution>> = repository.allSolutions.asLiveData()

    val currentList: MutableLiveData<MutableList<Solution>> by lazy {
        MutableLiveData<MutableList<Solution>>()
    }

    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */
    fun insert(solution: Solution) = viewModelScope.launch {
        repository.insert(solution)
    }

    fun updateList(size: Int) = viewModelScope.launch {
        Log.i("loc", "view model, updatelist")
        val tempList: MutableList<Solution> = mutableListOf()
        allSolutions.value?.let {
            for (l in it) {
                if (size == l.wordSize) {
                    Log.i("adding", l.solution)
                    tempList.add(l)
                }
            }
        }
        currentList.postValue(tempList)
        Log.i("all list", allSolutions.value.toString())
        Log.i("current list", currentList.value.toString())
    }
}

class SolutionViewModelFactory(private val repository: SolutionRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SolutionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SolutionViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
