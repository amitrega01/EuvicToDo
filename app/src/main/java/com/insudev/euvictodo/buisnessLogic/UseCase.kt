package com.insudev.euvictodo.buisnessLogic

import android.util.Log
import com.insudev.euvictodo.models.Filters
import com.insudev.euvictodo.models.Sorting
import com.insudev.euvictodo.models.TodoModel
import com.insudev.euvictodo.mvi.TodoListChangeResult
import io.reactivex.Observable

class UseCase {

    val api = TodoRetroService()

    companion object {
        val instance = UseCase()
    }


    fun getFilteredTodos(
        filter: Filters,
        sorting: Sorting,
        skip: Int,
        take: Int
    ): Observable<TodoListChangeResult> {
        return api.getFilteredTodos(filter, sorting, skip, take).map {
            Log.i("SIZE FROM API", it.size.toString())

            if (it != null) {
                if (it.size == 0) {

                    TodoListChangeResult.Error("Nie ma wiecej notatek")
                } else
                    TodoListChangeResult.Completed(filter, it)
            } else TodoListChangeResult.Error("")
        }
            .startWith(TodoListChangeResult.Pending())
            .onErrorReturnItem(TodoListChangeResult.Error("Error"))


    }

    fun getEmpty(): Observable<TodoListChangeResult> {
        return Observable.fromArray(ArrayList<TodoModel>()).map {
            if (it != null) {

                TodoListChangeResult.Completed(null, it)
            } else TodoListChangeResult.Error("")
        }
            .startWith(TodoListChangeResult.Pending())
            .onErrorReturnItem(TodoListChangeResult.Error("Error"))


    }

}