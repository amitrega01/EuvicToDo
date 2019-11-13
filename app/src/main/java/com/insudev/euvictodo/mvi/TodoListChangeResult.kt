package com.insudev.euvictodo.mvi

import com.insudev.euvictodo.models.TodoModel

sealed class TodoListChangeResult {
    class Pending : TodoListChangeResult()
    data class Completed(val change: Any?, val todoList: ArrayList<TodoModel>?) :
        TodoListChangeResult()
    data class Error(val error: String) : TodoListChangeResult()
}