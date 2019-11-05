package com.insudev.euvictodo.MainList

import android.util.Log

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
                        Log.i("FILTER", change.filter.filter.toString())
                    }
                    is FilterChangeResult.Error -> {
                        currentState.isLoading = false
                        currentState.isLoadingFailed = true
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
                    }
                    is SearchChangeResult.Error -> {
                        currentState.isLoading = false
                        currentState.isLoadingFailed = true
                    }
                }
            }
        }
//
//        when  (currentState.filter) {
//            Filters.ALL -> currentState.todoList = currentState.todoList
//            Filters.FINISHED -> currentState.todoList = ArrayList(currentState.todoList.filter { x-> x.status })
//            Filters.UNFINISHED -> currentState.todoList = ArrayList(currentState.todoList.filter { x-> !x.status })
//        }
        return currentState
    }

}