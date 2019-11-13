package com.insudev.euvictodo.newTodoFragment.mvi

data class NewTodoState(
    var isLoading: Boolean = true,
    var newTodoContent: String = "",
    var message: String = "",
    var isLoadingFailed: Boolean = false
)