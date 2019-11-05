package com.insudev.euvictodo.data

import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.insudev.euvictodo.models.TodoModel
import io.reactivex.Observable
import java.util.*
import kotlin.collections.ArrayList

object Repository {

    fun loadTodos(sharedPrefs: SharedPreferences): Observable<ArrayList<TodoModel>> =
        Observable.fromArray(getAll(sharedPrefs))


    var listType = object : TypeToken<ArrayList<TodoModel>>() {

    }.type

    private fun getAll(sharedPrefs: SharedPreferences): ArrayList<TodoModel> {
        val json = sharedPrefs.getString("JSON", "{}")
        var array = ArrayList<TodoModel>()
        if (json != "{}")
            array =
                Gson().fromJson(json, listType)
        Log.i("ARRAY", array.toString())
        return ArrayList(array)
    }

    fun updateStatus(sharedPrefs: SharedPreferences, id: Int): Observable<ArrayList<TodoModel>> {
        val json = sharedPrefs.getString("JSON", "{}")
        var array = ArrayList<TodoModel>()
        if (json != "{}")
            array = Gson().fromJson(json, listType)
        array.forEach { x ->
            if (x.id == id) x.status = !x.status
        }
        val editor = sharedPrefs.edit()
        val newjson = Gson().toJson(array)
        editor.putString("JSON", newjson).commit()
        return Observable.fromArray(array)
    }


    fun addNewTodo(
        sharedPrefs: SharedPreferences,
        todoContent: String
    ): Observable<ArrayList<TodoModel>> {
        val json = sharedPrefs.getString("JSON", "{}")
        var array = ArrayList<TodoModel>()
        if (json != "{}")
            array =
                Gson().fromJson(json, listType)
        array.add(
            TodoModel(
                array.last().id + 1,
                Date().toString(),
                todoContent,
                false,
                arrayListOf("elo")
            )
        )
        val editor = sharedPrefs.edit()

        val newjson = Gson().toJson(array)
        editor.putString("JSON", newjson).commit()
        return Observable.fromArray(array)
    }
}

