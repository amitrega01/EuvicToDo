package com.insudev.euvictodo.MainList


sealed class SearchChangeResult {
    class Pending : SearchChangeResult()
    data class Completed(val searchPhrase: String) : SearchChangeResult()
    data class Error(val error: String) : SearchChangeResult()

}
