package com.insudev.euvictodo.models


import com.insudev.euvictodo.SORTING

class SortedSearch {

    val phrase : String
    val sorting : SORTING

    constructor(phrase: String, sorting: SORTING) {
        this.phrase = phrase
        this.sorting = sorting
    }
}