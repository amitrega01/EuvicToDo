package com.insudev.euvictodo.newTodoFragment.mvi

sealed class ContentChangeResult {

    class Pending : ContentChangeResult()
    data class Completed(val content: String) : ContentChangeResult()
    data class Error(val error: String) : ContentChangeResult()
}