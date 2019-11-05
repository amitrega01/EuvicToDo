package com.insudev.euvictodo.MainList

import android.content.SharedPreferences
import android.util.Log
import com.hannesdorfmann.mosby3.mvi.MviBasePresenter
import com.insudev.euvictodo.buisnesslogic.GetTodoUseCase
import io.reactivex.Observable

class MainPresenter(sharedPrefs : SharedPreferences) : MviBasePresenter<MainView, MainViewState>() {

    private val reducer: MainViewReducer = MainViewReducer()
    val sharedPrefs = sharedPrefs
    override fun bindIntents() {

        val initIntent = intent { it.initIntent }

            .switchMap {
                val array = GetTodoUseCase.getAllTodos(sharedPrefs)
                Log.i("ARRAY", array.toString())
                return@switchMap array
                    .map { TodoListChangeResult.Completed(it) as TodoListChangeResult }
                    .startWith(TodoListChangeResult.Pending())
                    .onErrorReturn { TodoListChangeResult.Error(it.localizedMessage) }
            }
            .map {
                Log.i("ARRAY", it.toString())
                return@map MainViewStateChange.TodoListChange(it) as MainViewStateChange
            }


        val addTodo = intent { it.addTodo }
            .switchMap {
                val array = GetTodoUseCase.addnewTodo(sharedPrefs, it)
                Log.i("ARRAY", array.toString())
                return@switchMap array
                    .map { TodoListChangeResult.Completed(it) as TodoListChangeResult }
                    .startWith(TodoListChangeResult.Pending())
                    .onErrorReturn { TodoListChangeResult.Error(it.localizedMessage) }
            }.map {
                return@map MainViewStateChange.TodoListChange(it) as MainViewStateChange
            }

        val changeFilter = intent { it.changeFilter }
            .map { FilterChangeResult.Completed(it) as FilterChangeResult }
            .startWith(FilterChangeResult.Pending())
            .onErrorReturn { FilterChangeResult.Error(it.localizedMessage) }
            .map { MainViewStateChange.FilterChange(it) as MainViewStateChange }

        val search = intent { it.search }
            .map { SearchChangeResult.Completed(it) as SearchChangeResult }
            .startWith(SearchChangeResult.Pending())
            .onErrorReturn { SearchChangeResult.Error(it.localizedMessage) }
            .map { MainViewStateChange.SearchChange(it) as MainViewStateChange }

        val updateTodo = intent { it.updateTodo }
            .switchMap {
                Log.i("UPDATE", it.toString())
                val array = GetTodoUseCase.updateTodo(sharedPrefs, it)
                return@switchMap array
                    .map { TodoListChangeResult.Completed(it) as TodoListChangeResult }
                    .startWith(TodoListChangeResult.Pending())
                    .onErrorReturn { TodoListChangeResult.Error(it.localizedMessage) }
            }.map {
                return@map MainViewStateChange.TodoListChange(it) as MainViewStateChange
            }
        val sortingChange = intent { it.sortingChange }
            .map { SortingChangedResult.Completed(it) as SortingChangedResult }
            .startWith(SortingChangedResult.Pending())
            .onErrorReturn { SortingChangedResult.Error(it.localizedMessage) }
            .map { MainViewStateChange.SortingChange(it) as MainViewStateChange }

        val clearFinished = intent { it.clearFinished }
            .switchMap {
                val array = GetTodoUseCase.clearFinished(sharedPrefs)
                Log.i("ARRAY", array.toString())
                return@switchMap array
                    .map { TodoListChangeResult.Completed(it) as TodoListChangeResult }
                    .startWith(TodoListChangeResult.Pending())
                    .onErrorReturn { TodoListChangeResult.Error(it.localizedMessage) }
            }
            .map {
                Log.i("ARRAY", it.toString())
                return@map MainViewStateChange.TodoListChange(it) as MainViewStateChange
            }


        val stream = Observable
            .merge(initIntent, addTodo, changeFilter, search)
            .mergeWith(updateTodo)
            .mergeWith(sortingChange)
            .mergeWith(clearFinished)
            .scan(MainViewState()) { state: MainViewState, change: MainViewStateChange ->
                return@scan reducer.reduce(state, change)
            }

        subscribeViewState(stream) { view, viewState ->
            view.render(viewState)
        }
    }
}

