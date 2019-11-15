package com.insudev.euvictodo.models

data class EmptyModel(
    var content: String = "",
    var action: Any? = null,
    override var timeStamp: Long = Long.MAX_VALUE
) : ModelInterface