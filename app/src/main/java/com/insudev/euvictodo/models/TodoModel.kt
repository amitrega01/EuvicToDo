package com.insudev.euvictodo.models

import kotlinx.serialization.Serializable

@Serializable
data class TodoModel(

     var id: Int,
     override var timeStamp: Long,
     var content: String,
     var status: Boolean,
     var tags: ArrayList<String>
) : ModelInterface