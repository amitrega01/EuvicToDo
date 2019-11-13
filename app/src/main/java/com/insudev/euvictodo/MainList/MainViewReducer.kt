package com.insudev.euvictodo.MainList

import android.util.Log
import com.insudev.euvictodo.models.Sorting

class MainViewReducer {

    fun reduce(state: MainViewState, change: MainViewStateChange): MainViewState {
        val currentState = state.copy()
        Log.i("PREVIOUS", currentState.toString())
        Log.i("CHANGES", currentState.toString())
        when (change) {
            is MainViewStateChange.TodoListChange -> {

                when (change.result) {
                    is TodoListChangeResult.Pending -> {
                        currentState.isLoading = true
                        currentState.isLoadingFailed = false
                    }
                    is TodoListChangeResult.Completed -> {
                        currentState.isLoading = false
                        currentState.isLoadingFailed = false
                        currentState.todoList = change.result.todoList
                    }
                    is TodoListChangeResult.Error -> {
                        currentState.isLoading = false
                        currentState.isLoadingFailed = true
                        currentState.message = change.result.error
                    }
                }
            }
            is MainViewStateChange.FilterChange -> {
                when (change.filter) {
                    is FilterChangeResult.Pending -> {
                        currentState.isLoading = true
                        currentState.isLoadingFailed = false
                    }
                    is FilterChangeResult.Completed -> {
                        currentState.isLoading = false
                        currentState.isLoadingFailed = false
                        currentState.filter = change.filter.filter
                        currentState.todoList = change.filter.todoList
                    }
                    is FilterChangeResult.Error -> {
                        currentState.isLoading = false
                        currentState.isLoadingFailed = true
                        currentState.message = change.filter.error
                    }
                }
            }
            is MainViewStateChange.SearchChange -> {
                when (change.search) {
                    is SearchChangeResult.Pending -> {
                        currentState.isLoading = true
                        currentState.isLoadingFailed = false
                    }
                    is SearchChangeResult.Completed -> {
                        currentState.isLoading = false
                        currentState.isLoadingFailed = false
                        currentState.searchPhrase = change.search.searchPhrase
                        currentState.todoList = change.search.todoList
                    }
                    is SearchChangeResult.Error -> {
                        currentState.isLoading = false
                        currentState.isLoadingFailed = true
                        currentState.message = change.search.error
                    }
                }
            }
            is MainViewStateChange.SortingChange -> {
                when (change.sorting) {
                    is SortingChangedResult.Pending -> {
                        currentState.isLoading = true
                        currentState.isLoadingFailed = false
                    }
                    is SortingChangedResult.Completed -> {
                        currentState.isLoading = false
                        currentState.isLoadingFailed = false
                        currentState.sorting = change.sorting.newSorting
                    }
                    is SortingChangedResult.Error -> {
                        currentState.isLoading = false
                        currentState.isLoadingFailed = true
                        currentState.message = change.sorting.error
                    }
                }
            }
        }

//
        currentState.todoList =
            ArrayList(currentState.todoList.filter { x -> x.content.contains(currentState.searchPhrase) })

        currentState.todoList = when (currentState.sorting) {
            Sorting.ASCENDING -> ArrayList(currentState.todoList.sortedWith(compareByDescending { it.timeStamp }))
            Sorting.DESCENDING -> ArrayList(currentState.todoList.sortedWith(compareBy { it.timeStamp }))
        }

        return currentState
    }

}