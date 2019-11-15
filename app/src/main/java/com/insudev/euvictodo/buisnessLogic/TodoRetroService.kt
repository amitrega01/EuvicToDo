package com.insudev.euvictodo.buisnessLogic

import android.util.Log
import com.google.gson.JsonArray
import com.google.gson.reflect.TypeToken
import com.insudev.euvictodo.models.Filters
import com.insudev.euvictodo.models.Sorting
import com.insudev.euvictodo.models.TodoModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class TodoRetroService(val service: TodoService) {
    //TODO uprzatnac projekt, paginacja, obluga bledow onErrorreturnNext, USeCase,


    var listType = object : TypeToken<ArrayList<TodoModel>>() {}.type

    fun getAllTodos(): Observable<JsonArray> {
        Log.i("API2", "Subscribing")

        val temp = service.getAllTodos()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
        return temp
    }

    fun addTodo(
        content: String,
        filter: Filters,
        sorting: Sorting
    ): Observable<ArrayList<TodoModel>> {
        return service.addTodo(content, filter)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun getFilteredTodos(
        filter: Filters,
        sorting: Sorting,
        skip: Int,
        take: Int
    ): Observable<ArrayList<TodoModel>> {
        return service.getFilteredTodos(filter, sorting, skip, take)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }


    fun searchTodos(
        searchPhrase: String,
        filter: Filters
    ): Observable<ArrayList<TodoModel>> {
        return service.searchTodos(searchPhrase, filter)

            .subscribeOn(Schedulers.io())

            .observeOn(AndroidSchedulers.mainThread())
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

    fun getNextId(): Observable<Int> {
        return service.getNextId()

    }


}