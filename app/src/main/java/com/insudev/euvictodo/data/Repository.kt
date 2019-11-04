package com.insudev.euvictodo.data

import android.content.SharedPreferences
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.insudev.euvictodo.SORTING
import com.insudev.euvictodo.models.TodoModel
import io.reactivex.Observable
import org.jetbrains.anko.doAsync
import java.util.Random
object Repository {

    fun loadTodos(sharedPrefs: SharedPreferences, sorting: SORTING): Observable<ArrayList<TodoModel>> = Observable.fromArray(getAll(sharedPrefs, sorting))
    var listType = object : TypeToken<ArrayList<TodoModel>>() {

    }.type
    private fun getAll(sharedPrefs: SharedPreferences, sorting: SORTING): ArrayList<TodoModel> {
        val json = sharedPrefs.getString("JSON","{}");
        var array = ArrayList<TodoModel>()
        if (json != "{}")
        Log.i("SORTING", sorting.toString())
            array =
                Gson().fromJson(json, listType)
        return when (sorting){
            SORTING.ASCENDING -> ArrayList(array.sortedWith(compareByDescending { it.timeStamp }))
            SORTING.DESCENDING -> ArrayList(array.sortedWith(compareBy{ it.timeStamp }))
        }
    }
}

