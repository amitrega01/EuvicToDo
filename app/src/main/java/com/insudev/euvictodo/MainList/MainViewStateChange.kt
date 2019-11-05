package com.insudev.euvictodo.MainList

sealed class MainViewStateChange {
    data class TodoListChange(val result: TodoListChangeResult) : MainViewStateChange()
    data class FilterChange(val filter: FilterChangeResult) : MainViewStateChange()
    data class SearchChange(val search: SearchChangeResult) : MainViewStateChange()
    data class SortingChange(val sorting: SortingChangedResult) : MainViewStateChange()
}