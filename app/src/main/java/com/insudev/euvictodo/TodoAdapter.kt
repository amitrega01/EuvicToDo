package com.insudev.euvictodo

import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.insudev.euvictodo.models.TodoModel
import com.jakewharton.rxbinding3.view.clicks
import io.reactivex.rxkotlin.addTo
import java.util.*
import kotlin.collections.ArrayList

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
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        val todo = myDataset.get(position)
        holder.content.text = todo.content
        holder.checkBox_status.isChecked = todo.status
        holder.text_date.text = Date(todo.timeStamp).toLocaleString()
        if (todo.status) {
            holder.content.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
        } else {
            holder.content.paintFlags = Paint.ANTI_ALIAS_FLAG
        }
        holder.checkBox_status.clicks().map {
            this.notifyDataSetChanged()
            return@map todo.id
        }
            .subscribe {
                (context as MainActivity).updateTodo.onNext(it)
            }
            .addTo((context as MainActivity).subscriptions)

    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = myDataset.size
}