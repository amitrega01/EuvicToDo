package com.insudev.euvictodo.dialogNewTodo

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Window
import com.insudev.euvictodo.R
import kotlinx.android.synthetic.main.new_todo_dialog.*

class NewTodoDialog(context: Context) : Dialog(context) {

    init {
        setCancelable(true)
        setTitle("Add new TODO")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.new_todo_dialog)
//        callback = addButton.clicks().map { contentText.text.toString() }

        addButton.setOnClickListener {
            this.close()
        }


    }

    private fun close() {
        this.dismiss()
    }
}