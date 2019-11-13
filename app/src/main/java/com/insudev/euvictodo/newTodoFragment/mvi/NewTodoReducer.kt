package com.insudev.euvictodo.newTodoFragment.mvi

class NewTodoReducer {
    fun reduce(state: NewTodoState, change: NewTodoStateChange): NewTodoState {
        val currentState = state.copy()
        when (change) {
            is NewTodoStateChange.ContentChange -> {

                when (change.result) {
                    is ContentChangeResult.Pending -> {
                        currentState.isLoading = true
                        currentState.isLoadingFailed = false
                    }
                    is ContentChangeResult.Completed -> {
                        currentState.isLoading = false
                        currentState.isLoadingFailed = false
                        currentState.newTodoContent = change.result.content
                    }
                    is ContentChangeResult.Error -> {
                        currentState.isLoading = false
                        currentState.isLoadingFailed = true
                        currentState.message = change.result.error
                    }
                }
            }


        }
        return currentState
    }
}