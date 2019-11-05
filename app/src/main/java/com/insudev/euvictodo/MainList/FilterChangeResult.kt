package com.insudev.euvictodo.MainList

import com.insudev.euvictodo.buisnesslogic.Filters

sealed class FilterChangeResult {
    class Pending : FilterChangeResult()
    data class Completed(val filter: Filters) : FilterChangeResult()
    data class Error(val error: String) : FilterChangeResult()

}
