package com.insudev.euvictodo.mvi

import android.content.SharedPreferences
import com.google.gson.reflect.TypeToken
import com.hannesdorfmann.mosby3.mvi.MviBasePresenter
import com.insudev.euvictodo.buisnessLogic.UseCase
import com.insudev.euvictodo.models.TodoModel
import io.reactivex.Observable


class MainPresenter(sharedPrefs: SharedPreferences) : MviBasePresenter<MainView, MainViewState>() {


    private lateinit var state: MainViewState

    private val useCase = UseCase.instance

    var listType = object : TypeToken<ArrayList<TodoModel>>() {
    }.type

    private val reducer: MainViewReducer = MainViewReducer()
    val sharedPrefs = sharedPrefs
    override fun bindIntents() {
        //TODO To use case skip i take
        val initIntent = intent { it.initIntent }.switchMap {
            useCase.getFilteredTodos(state.filter, state.sorting, 0, 11)
        }.map { MainViewStateChange.TodoListChange(it) as MainViewStateChange }

        val scrollChange = intent { it.scrollChange }.switchMap {
            if (it == 3)
                useCase.getFilteredTodos(state.filter, state.sorting, state.listSize, 11)
            else useCase.getEmpty()
        }.map { MainViewStateChange.ScrollChange(it) as MainViewStateChange }


        //TODO To use case
        val addTodo = intent { it.addTodo }
            .switchMap { useCase.addTodo(it, useCase.getNextId().blockingFirst()) }
            .map { MainViewStateChange.TodoAdded(it) as MainViewStateChange }


        val filterChange = intent { it.changeFilter }
            .switchMap { useCase.getFilteredTodos(it, state.sorting, 0, 11) }
            .map { MainViewStateChange.FilterChange(it) as MainViewStateChange }


        val search = intent { it.search }
            .map {
                TodoListChangeResult.Completed(
                    it,
                    ArrayList(state.todoList.filter { x -> x is TodoModel }) as ArrayList<TodoModel>
                ) as TodoListChangeResult
            }
            .startWith(TodoListChangeResult.Pending())
            .onErrorReturn { TodoListChangeResult.Error("Błąd wyszukiwania") }
            .map { MainViewStateChange.SearchChange(it) as MainViewStateChange }

        val syncList = intent { it.syncList }
            .switchMap { useCase.sync(state.toSync) }
            .map { MainViewStateChange.Synced(it) as MainViewStateChange }

        //TODO To use case
//        val updateTodo = intent { it.updateTodo }
//            .switchMap {
//                val array = retroService.setStatus(
//                    it,
//                    viewStateObservable.map { return@map it.filter }.blockingFirst(Filters.ALL)
//                )
//                return@switchMap array
//                    .map {
//                        TodoListChangeResult.Completed(
//                            null,
//                            Gson().fromJson(
//                                it,
//                                listType
//                            )
//                        ) as TodoListChangeResult
//                    }
//                    .startWith(TodoListChangeResult.Pending())
//                    .onErrorReturn { TodoListChangeResult.Error(it.localizedMessage) }
//            }.map {
//                return@map MainViewStateChange.TodoListChange(it) as MainViewStateChange
//            }

        val sortingChange = intent { it.sortingChange }
            .map { TodoListChangeResult.Completed(it, null) as TodoListChangeResult }
            .startWith(TodoListChangeResult.Pending())
            .onErrorReturn { TodoListChangeResult.Error(it.localizedMessage) }
            .map { MainViewStateChange.SortingChange(it) as MainViewStateChange }


        //TODO To use case
//        val clearFinished = intent { it.clearFinished }
//            .switchMap {
//                val array = retroService.delete()
//                return@switchMap array
//                    .map {
//                        TodoListChangeResult.Completed(
//                            null, Gson().fromJson(
//                                it,
//                                listType
//                            )
//                        ) as TodoListChangeResult
//                    }
//                    .startWith(TodoListChangeResult.Pending())
//                    .onErrorReturn { TodoListChangeResult.Error(it.localizedMessage) }
//            }.map {
//                return@map MainViewStateChange.TodoListChange(it) as MainViewStateChange
//            }


        val stream = Observable
            .merge(initIntent, addTodo, filterChange, search)
            .mergeWith(sortingChange)
            .mergeWith(scrollChange)
            .mergeWith(syncList)
            .scan(MainViewState()) { state: MainViewState, change: MainViewStateChange ->
                return@scan reducer.reduce(state, change)
            }

        subscribeViewState(stream) { view, viewState ->
            state = viewState
            view.render(viewState)

        }
    }
}

