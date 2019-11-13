package com.insudev.euvictodo.MainList

import com.insudev.euvictodo.buisnesslogic.Filters
import com.insudev.euvictodo.models.TodoModel

sealed class FilterChangeResult {
    class Pending : FilterChangeResult()
    data class Completed(val filter: Filters, val todoList: ArrayList<TodoModel>) :
        FilterChangeResult()
    data class Error(val error: String) : FilterChangeResult()

}
