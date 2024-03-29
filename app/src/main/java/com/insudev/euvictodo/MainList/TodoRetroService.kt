package com.insudev.euvictodo.MainList

import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.reflect.TypeToken
import com.insudev.euvictodo.TodoService
import com.insudev.euvictodo.buisnesslogic.Filters
import com.insudev.euvictodo.models.TodoModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class TodoRetroService {
    //TODO uprzatnac projekt, paginacja, obluga bledow onErrorreturnNext, USeCase,
    private val retrofit = Retrofit.Builder()
        .baseUrl("http://euvictodoapi.herokuapp.com/")
        .addCallAdapterFactory(RxJava2CallAdapterFactory.createAsync())
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val service = retrofit.create<TodoService>(TodoService::class.java)

    var listType = object : TypeToken<ArrayList<TodoModel>>() {}.type

    fun getAllTodos(): Observable<JsonArray> {
        Log.i("API2", "Subscribing")
        val temp = service.getAllTodos()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
        return temp
    }

    fun addTodo(content: String, filter: Filters): Observable<TodoListChangeResult> {
        val temp = service.addTodo(content, filter)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map {
                if (it != null) {
                    TodoListChangeResult.Completed(Gson().fromJson(it, listType))
                } else {
                    TodoListChangeResult.Error(it.toString())
                }
            }
            .startWith(TodoListChangeResult.Pending())
            .onErrorReturn {

                TodoListChangeResult.Error(it.localizedMessage)

            }


        return temp
    }

    fun getFilteredTodos(filter: Filters): Observable<JsonArray> {
        val temp = service.getFilteredTodos(filter)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

        return temp
    }

    fun setStatus(id: Int, filter: Filters): Observable<JsonArray> {
        val temp = service.setStatus(id, filter)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

        return temp
    }

    fun delete(): Observable<JsonArray> {
        val temp = service.delete()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

        return temp
    }


}