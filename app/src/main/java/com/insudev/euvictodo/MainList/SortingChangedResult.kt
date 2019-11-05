package com.insudev.euvictodo.MainList

import com.insudev.euvictodo.models.Sorting

sealed class SortingChangedResult {

    class Pending : SortingChangedResult()
    data class Completed(val newSorting: Sorting) : SortingChangedResult()
    data class Error(val error: String) : SortingChangedResult()
}