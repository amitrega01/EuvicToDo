package com.insudev.euvictodo.buisnessLogic


import com.insudev.euvictodo.models.Filters
import com.insudev.euvictodo.models.Sorting
import com.insudev.euvictodo.models.TodoModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class TodoRetroService(private val service: TodoService) {
    //TODO uprzatnac projekt, paginacja, obluga bledow onErrorreturnNext, USeCase,


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


    fun getNextId(): Observable<Int> {
        return service.getNextId()

    }

    fun sync(toSync: ArrayList<TodoModel>): Observable<Boolean> {
        return service.sync("application/json", toSync)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }


}