package com.insudev.euvictodo.MainList

import com.insudev.euvictodo.models.TodoModel


sealed class SearchChangeResult {
    class Pending : SearchChangeResult()
    data class Completed(val searchPhrase: String, val todoList: ArrayList<TodoModel>) :
        SearchChangeResult()
    data class Error(val error: String) : SearchChangeResult()

}
