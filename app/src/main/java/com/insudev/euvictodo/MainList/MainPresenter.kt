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
            }.map {
                return@map MainViewStateChange.TodoListChange(it) as MainViewStateChange
            }

        val changeFilter = intent { it.changeFilter }
            .map { FilterChangeResult.Completed(it) as FilterChangeResult }
            .startWith(FilterChangeResult.Pending())
            .map { MainViewStateChange.FilterChange(it) as MainViewStateChange }

        val search = intent { it.search }
            .map { SearchChangeResult.Completed(it) as SearchChangeResult }
            .startWith(SearchChangeResult.Pending())
            .map { MainViewStateChange.SearchChange(it) as MainViewStateChange }

//        val mainState: Observable<MainViewState> = intent(MainView::showAllTodos)
//                .subscribeOn(Schedulers.io())
//                .debounce(400, TimeUnit.MILLISECONDS)
//                .switchMap { GetTodoUseCase.getAllTodos(sharedPrefs, it) }
////                .doOnNext { Timber.d("Received new state: " + it) }
//                .observeOn(AndroidSchedulers.mainThread())
//
//        val searchIntent  =intent { it.searchTodos }
//            .subscribeOn(Schedulers.io())
//            .debounce(400, TimeUnit.MILLISECONDS)
//            .switchMap { GetTodoUseCase.searchTodos(sharedPrefs, it.phrase,it.sorting) }
//            .observeOn(AndroidSchedulers.mainThread())
//
//        val showFiltered = intent { it.showFiltered }
//            .subscribeOn(Schedulers.io())
//            .debounce(400,TimeUnit.MILLISECONDS)
//            .switchMap { GetTodoUseCase.getFiltered(sharedPrefs, it.filter, it.sorting) }
//            .observeOn(AndroidSchedulers.mainThread())
//
//

//
        val stream = Observable
            .merge(initIntent, addTodo, changeFilter, search)
            .scan(MainViewState()) { state: MainViewState, change: MainViewStateChange ->
                return@scan reducer.reduce(state, change)
            }

        subscribeViewState(stream) { view, viewState ->
            view.render(viewState)
        }
    }
}

