package com.insudev.euvictodo.models

import com.insudev.euvictodo.SORTING
import com.insudev.euvictodo.buisnesslogic.Filters

class SortedFilter {

    val sorting : SORTING
    val filter : Filters

    constructor( filter: Filters, sorting: SORTING) {
        this.sorting = sorting
        this.filter = filter
    }
}