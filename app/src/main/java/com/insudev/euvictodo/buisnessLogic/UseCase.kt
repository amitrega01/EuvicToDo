package com.insudev.euvictodo.buisnessLogic

import com.insudev.euvictodo.models.Filters
import com.insudev.euvictodo.models.ModelInterface
import com.insudev.euvictodo.models.Sorting
import com.insudev.euvictodo.models.TodoModel
import com.insudev.euvictodo.mvi.TodoListChangeResult
import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class UseCase {

    private val retrofit = Retrofit.Builder()
        .baseUrl("http://euvictodoapi.herokuapp.com/")
        .addCallAdapterFactory(RxJava2CallAdapterFactory.createAsync())
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val service = retrofit.create<TodoService>(TodoService::class.java)

    val api = TodoRetroService(service)

    companion object {
        val instance = UseCase()
    }

    fun addTodo(content: String, prevId: Int): Observable<TodoListChangeResult> {
        return Observable.just(
            TodoListChangeResult.Completed(
                TodoModel(
                    prevId, System.currentTimeMillis(), content,
                    false, arrayListOf("")
                ), null
            ) as TodoListChangeResult
        )
            .startWith(TodoListChangeResult.Pending())
            .onErrorReturnItem(TodoListChangeResult.Error("Błąd dodawnaia notatki"))
    }

    fun sync(toSync: ArrayList<TodoModel>): Observable<TodoListChangeResult> {
        return api.sync(toSync)
            .map {
                if (it) TodoListChangeResult.Completed("Zsynchronizowano", null)
                else TodoListChangeResult.Error("Błąd synchronizacji")
            }.startWith(TodoListChangeResult.Pending())
            .onErrorReturnItem(TodoListChangeResult.Error("Błąd synchronizacji"))
    }

    fun getFilteredTodos(
        filter: Filters,
        sorting: Sorting,
        skip: Int,
        take: Int
    ): Observable<TodoListChangeResult> {
        return api.getFilteredTodos(filter, sorting, skip, take).map {
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

    fun getNextId(): Observable<Int> {
        return api.getNextId()
    }

    fun updateStatus(
        id: Int,
        todoList: ArrayList<ModelInterface>,
        syncList: ArrayList<TodoModel>
    ): Observable<TodoListChangeResult> {
        val todos = ArrayList(todoList.filterIsInstance<TodoModel>())
        var toSync: TodoModel
        try {
            val index = todos.indexOfFirst { x -> x.id == id }
            toSync = todos.removeAt(index)
        } catch (e: ArrayIndexOutOfBoundsException) {

            val index = syncList.indexOfFirst { x -> x.id == id }
            toSync = syncList.removeAt(index)
        }
        toSync.status = !toSync.status
        return Observable.just(
            TodoListChangeResult.Completed(
                toSync,
                todos
            ) as TodoListChangeResult
        )
            .startWith(TodoListChangeResult.Pending())
            .onErrorReturnItem(TodoListChangeResult.Error("Error"))
    }

}