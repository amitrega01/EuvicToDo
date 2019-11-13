package com.insudev.euvictodo.dialogNewTodo

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.insudev.euvictodo.models.TodoModel
import org.jetbrains.anko.button
import org.jetbrains.anko.editText
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.verticalLayout
import java.util.*
import kotlin.collections.ArrayList


class NewTodoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        verticalLayout{

            val editContent = editText{
                hint = "Title"
                textSize = 24f
            }
            button{
                onClick {
                    var listType = object : TypeToken<ArrayList<TodoModel>>() {

                    }.type
                    val sharedPrefs = getSharedPreferences("MAIN", Context.MODE_PRIVATE)
                    val editor = sharedPrefs.edit()
                    val json = sharedPrefs.getString("JSON", "{}")
                    var array = ArrayList<TodoModel>()
                    if (json != "{}")

                        array =
                            Gson().fromJson(json, listType)



                    Log.i("DODAWANIE", Gson().toJson(array))
                    array.add(
                        TodoModel(
                            array.size,
                            Date().time,
                            editContent.text.toString(),
                            false,
                            arrayListOf("")
                        )
                    )
                    Log.i("DODAWANIE", Gson().toJson(array))
                    editor.putString("JSON",Gson().toJson(array)).commit()
                    finish()
                }
                title = "Add"
            }

        }
    }
}
