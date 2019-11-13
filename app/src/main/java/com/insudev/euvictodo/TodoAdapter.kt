package com.insudev.euvictodo

import android.content.Context
import android.graphics.Paint
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.insudev.euvictodo.models.TodoModel
import com.jakewharton.rxbinding3.view.clicks
import io.reactivex.rxkotlin.addTo

class TodoAdapter(val context: Context) : RecyclerView.Adapter<MyViewHolder>() {
    var myDataset = ArrayList<TodoModel>()

    val sharedPrefs = context.getSharedPreferences("MAIN", Context.MODE_PRIVATE)


    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        // create a new view
        var view = LayoutInflater.from(parent.context)
            .inflate(R.layout.todo_view, parent, false)

        return MyViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val todo = myDataset.get(position)
        holder.content.text = todo.content
        holder.checkBox_status.isChecked = todo.status
        holder.text_date.text = java.time.format.DateTimeFormatter.ISO_INSTANT
            .format(java.time.Instant.ofEpochSecond(todo.timeStamp))

        if (todo.status) {
            holder.content.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
        } else {
            holder.content.paintFlags = Paint.ANTI_ALIAS_FLAG
        }

        holder.checkBox_status.clicks().map {
            this.notifyDataSetChanged()
            return@map todo.id
        }.subscribe { (context as MainActivity).updateTodo.onNext(it) }
            .addTo((context as MainActivity).subscriptions)
    }

    override fun getItemCount() = myDataset.size
}