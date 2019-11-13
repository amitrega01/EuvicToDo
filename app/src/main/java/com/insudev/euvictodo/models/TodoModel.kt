package com.insudev.euvictodo.models

import kotlinx.serialization.Serializable

@Serializable
data class TodoModel(

     var id: Int,
     var timeStamp: Long,
     var content: String,
     var status: Boolean,
     var tags: ArrayList<String>
)