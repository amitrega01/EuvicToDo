package com.insudev.euvictodo.buisnesslogic

import com.insudev.euvictodo.models.TodoModel

sealed class MainViewState {
    object LoadingState : MainViewState()
    data class DataState(val todos: ArrayList<TodoModel>) : MainViewState()
    data class ErrorState(val error: Throwable) : MainViewState()
}

