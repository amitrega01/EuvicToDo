package com.insudev.euvictodo

import android.view.View
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.insudev.euvictodo.models.TodoModel

class MyViewHolder( item: View) : RecyclerView.ViewHolder(item) {
     val content : TextView = item.findViewById(R.id.content)

     val checkBox_status : CheckBox = item.findViewById(R.id.checkBox_status)

     val text_date : TextView = item.findViewById(R.id.text_date)
}
