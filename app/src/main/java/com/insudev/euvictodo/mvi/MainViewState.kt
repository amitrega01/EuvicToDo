package com.insudev.euvictodo.mvi

import com.insudev.euvictodo.models.Filters
import com.insudev.euvictodo.models.Sorting
import com.insudev.euvictodo.models.TodoModel

data class MainViewState(
    var isLoading: Boolean = true,
    var todoList: ArrayList<TodoModel> = ArrayList(),
    var filter: Filters = Filters.ALL,
    var searchPhrase: String = "",
    var sorting: Sorting = Sorting.ASCENDING,
    var message: String = "",
    var isLoadingFailed: Boolean = false,
    var listSize: Int = 0

)
