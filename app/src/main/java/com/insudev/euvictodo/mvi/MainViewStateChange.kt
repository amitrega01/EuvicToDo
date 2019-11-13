package com.insudev.euvictodo.mvi

sealed class MainViewStateChange {
    data class TodoListChange(val result: TodoListChangeResult) : MainViewStateChange()
    data class FilterChange(val result: TodoListChangeResult) : MainViewStateChange()
    data class SearchChange(val result: TodoListChangeResult) : MainViewStateChange()
    data class SortingChange(val result: TodoListChangeResult) : MainViewStateChange()
    data class ScrollChange(val result: TodoListChangeResult) : MainViewStateChange()

}