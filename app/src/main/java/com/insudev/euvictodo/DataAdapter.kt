package com.insudev.euvictodo

import android.annotation.TargetApi
import android.content.Context
import android.graphics.Paint
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.insudev.euvictodo.models.EmptyModel
import com.insudev.euvictodo.models.ModelInterface
import com.insudev.euvictodo.models.TodoModel
import com.jakewharton.rxbinding3.view.clicks
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.empty_item.view.*
import kotlinx.android.synthetic.main.todo_view.view.*

abstract class BaseViewHolder<T>(itemView: View) : RecyclerView.ViewHolder(itemView) {
    abstract fun bind(item: T)


}


class DataAdapter(
    private val context: Context
) :
    RecyclerView.Adapter<DataAdapter.BaseViewHolder<*>>() {
    override fun getItemCount(): Int {
        return adapterDataList.size
    }

    private lateinit var instance: DataAdapter
    var adapterDataList: ArrayList<ModelInterface> = ArrayList()

    companion object {
        private const val TYPE_TODO = 0
        private const val TYPE_EMPTY = 1
    }


    inner class TodoViewHolder(itemView: View) : BaseViewHolder<TodoModel>(itemView) {

        @TargetApi(Build.VERSION_CODES.O)
        override fun bind(item: TodoModel) {

            itemView.content.text = item.content
            itemView.checkBox_status.isChecked = item.status
            itemView.text_date.text = java.time.format.DateTimeFormatter.ISO_INSTANT
                .format(java.time.Instant.ofEpochSecond(item.timeStamp))

            if (item.status) {
                itemView.content.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
            } else {
                itemView.content.paintFlags = Paint.ANTI_ALIAS_FLAG
            }

            itemView.checkBox_status.clicks().map {
                instance.notifyDataSetChanged()
                return@map item.id
            }.subscribe { (context as MainActivity).updateTodo.onNext(it) }
                .addTo((context as MainActivity).subscriptions)
        }
    }

    inner class EmptyViewHolder(itemView: View) : BaseViewHolder<EmptyModel>(itemView) {

        override fun bind(item: EmptyModel) {
            itemView.message.text = item.content
            itemView.card.clicks().map {

                instance.notifyDataSetChanged()
                return@map 3
            }.subscribe {
                adapterDataList.removeAt(itemCount - 1)

                (context as MainActivity).scrollChange.onNext(it)
            }
                .addTo((context as MainActivity).subscriptions)
        }
    }

    abstract class BaseViewHolder<T>(itemView: View) : RecyclerView.ViewHolder(itemView) {
        abstract fun bind(item: T)


    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        instance = this
        return when (viewType) {
            TYPE_TODO -> {
                val view = LayoutInflater.from(context)
                    .inflate(R.layout.todo_view, parent, false)
                TodoViewHolder(view)
            }
            TYPE_EMPTY -> {
                val view = LayoutInflater.from(context)
                    .inflate(
                        R.layout.empty_item
                        , parent, false
                    )
                EmptyViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        val element = adapterDataList[position]
        when (holder) {
            is TodoViewHolder -> holder.bind(element as TodoModel)
            is EmptyViewHolder -> holder.bind(element as EmptyModel)
            else -> throw IllegalArgumentException()
        }
    }

    override fun getItemViewType(position: Int): Int {
        val comparable = adapterDataList[position]
        return if (comparable is TodoModel) TYPE_TODO
        else TYPE_EMPTY
    }

}

