package com.insudev.euvictodo.models

import kotlinx.serialization.Serializable
import kotlin.collections.ArrayList
@Serializable
data class TodoModel (
     var id: Int,
     var timeStamp : String,
     var content: String,
     var status: Boolean,
     var tags: ArrayList<String>
)