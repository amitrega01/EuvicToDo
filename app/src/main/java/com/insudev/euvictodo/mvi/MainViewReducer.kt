package com.insudev.euvictodo.mvi

import android.util.Log
import com.insudev.euvictodo.models.Filters
import com.insudev.euvictodo.models.ModelInterface
import com.insudev.euvictodo.models.Sorting
import com.insudev.euvictodo.models.TodoModel

class MainViewReducer {

    fun reduce(state: MainViewState, change: MainViewStateChange): MainViewState {
        val currentState = state.copy()
        currentState.todoList = ArrayList(currentState.todoList.filter { x -> x is TodoModel })
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
                        currentState.todoList =
                            change.result.todoList!! as ArrayList<ModelInterface>
                        currentState.listSize = change.result.todoList.size
                    }
                    is TodoListChangeResult.Error -> {
                        currentState.isLoading = false
                        currentState.isLoadingFailed = true
                        currentState.message = change.result.error
                    }
                }
            }
            is MainViewStateChange.FilterChange -> {
                when (change.result) {
                    is TodoListChangeResult.Pending -> {
                        currentState.isLoading = true
                        currentState.isLoadingFailed = false
                    }
                    is TodoListChangeResult.Completed -> {
                        currentState.isLoading = false
                        currentState.isLoadingFailed = false
                        currentState.filter = change.result.change!! as Filters
                        currentState.todoList =
                            change.result.todoList!! as ArrayList<ModelInterface>
                        currentState.listSize = change.result.todoList.size
                    }
                    is TodoListChangeResult.Error -> {
                        currentState.isLoading = false
                        currentState.isLoadingFailed = true
                        currentState.message = change.result.error
                    }
                }
            }
            is MainViewStateChange.SearchChange -> {
                when (change.result) {
                    is TodoListChangeResult.Pending -> {
                        currentState.isLoading = true
                        currentState.isLoadingFailed = false
                    }
                    is TodoListChangeResult.Completed -> {
                        currentState.isLoading = false
                        currentState.isLoadingFailed = false
                        currentState.searchPhrase = change.result.change!! as String
                        currentState.todoList =
                            change.result.todoList!! as ArrayList<ModelInterface>
                        currentState.listSize = change.result.todoList.size
                    }
                    is TodoListChangeResult.Error -> {
                        currentState.isLoading = false
                        currentState.isLoadingFailed = true
                        currentState.message = change.result.error
                    }
                }
            }
            is MainViewStateChange.SortingChange -> {
                when (change.result) {
                    is TodoListChangeResult.Pending -> {
                        currentState.isLoading = true
                        currentState.isLoadingFailed = false
                    }
                    is TodoListChangeResult.Completed -> {
                        currentState.isLoading = false
                        currentState.isLoadingFailed = false
                        currentState.sorting = change.result.change!! as Sorting
                    }
                    is TodoListChangeResult.Error -> {
                        currentState.isLoading = false
                        currentState.isLoadingFailed = true
                        currentState.message = change.result.error
                    }
                }
            }
            is MainViewStateChange.ScrollChange -> {
                when (change.result) {
                    is TodoListChangeResult.Pending -> {
                        currentState.isLoading = true
                        currentState.isLoadingFailed = false
                    }
                    is TodoListChangeResult.Completed -> {
                        currentState.isLoading = false
                        currentState.isLoadingFailed = false
                        currentState.todoList =
                            ArrayList(currentState.todoList + change.result.todoList!!)
                        currentState.listSize =
                            ArrayList(currentState.todoList + change.result.todoList).size
                    }
                    is TodoListChangeResult.Error -> {
                        currentState.isLoading = false
                        currentState.isLoadingFailed = true
                        currentState.message = change.result.error
                    }
                }
            }
            is MainViewStateChange.TodoAdded -> {
                when (change.result) {
                    is TodoListChangeResult.Pending -> {
                        currentState.isLoading = true
                        currentState.isLoadingFailed = false
                    }
                    is TodoListChangeResult.Completed -> {
                        currentState.isLoading = false
                        currentState.isLoadingFailed = false
                        currentState.toSync =
                            ArrayList(currentState.toSync + change.result.change as TodoModel)
                        Log.i("TO SYNC", currentState.toSync.toString())
                    }
                    is TodoListChangeResult.Error -> {
                        currentState.isLoading = false
                        currentState.isLoadingFailed = true
                        currentState.message = change.result.error
                    }
                }
            }
        }

//
        Log.i("LIST SIZE", currentState.listSize.toString())
//        currentState.todoList =
//            ArrayList(currentState.todoList.filter { x ->
//                (x as TodoModel).content.contains(
//                    currentState.searchPhrase
//                )
//            })

//        currentState.todoList = when (currentState.sorting) {
//            Sorting.ASCENDING -> ArrayList(currentState.todoList.sortedWith(compareByDescending { it.timeStamp }))
//            Sorting.DESCENDING -> ArrayList(currentState.todoList.sortedWith(compareBy { it.timeStamp }))
//        }

        return currentState
    }

}