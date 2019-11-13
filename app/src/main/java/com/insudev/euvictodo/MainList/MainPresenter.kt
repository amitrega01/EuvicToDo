package com.insudev.euvictodo.MainList

import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hannesdorfmann.mosby3.mvi.MviBasePresenter
import com.insudev.euvictodo.buisnesslogic.Filters
import com.insudev.euvictodo.models.TodoModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


class MainPresenter(sharedPrefs : SharedPreferences) : MviBasePresenter<MainView, MainViewState>() {


    private val retroService = TodoRetroService()

    var listType = object : TypeToken<ArrayList<TodoModel>>() {
    }.type

    private val reducer: MainViewReducer = MainViewReducer()
    val sharedPrefs = sharedPrefs
    override fun bindIntents() {

        val initIntent = intent { it.initIntent }.switchMap {
            val array = retroService.getAllTodos()
                return@switchMap array
                    .map {
                        TodoListChangeResult.Completed(
                            Gson().fromJson(
                                it,
                                listType
                            )
                        ) as TodoListChangeResult
                    }
                    .startWith(TodoListChangeResult.Pending())
                    .onErrorReturn { TodoListChangeResult.Error(it.localizedMessage) }
        }
            .map {
                Log.i("API", it.toString())
                return@map MainViewStateChange.TodoListChange(it) as MainViewStateChange
            }

        val scrollChange = intent { it.scrollChange }.map { Log.i("SCROLL", it.toString()) }

        val addTodo = intent { it.addTodo }.observeOn(Schedulers.io())
            .switchMap {
                retroService.addTodo(
                    it,
                    viewStateObservable.map { return@map it.filter }.blockingFirst(Filters.ALL)
                )
            }
            .observeOn(AndroidSchedulers.mainThread())
            .map { MainViewStateChange.TodoListChange(it) as MainViewStateChange }

        val changeFilter = intent { it.changeFilter }
            .switchMap {
                val filter = it
                val array = retroService.getFilteredTodos(it)
                return@switchMap array
                    .map {
                        FilterChangeResult.Completed(
                            filter,
                            Gson().fromJson(it, listType)
                        ) as FilterChangeResult
                    }
                    .startWith(FilterChangeResult.Pending())
                    .onErrorReturn { FilterChangeResult.Error(it.localizedMessage) }
            }
            .map { MainViewStateChange.FilterChange(it) as MainViewStateChange }

        val search = intent { it.search }.switchMap {
            val search = it
            val array =
                retroService.getFilteredTodos(
                    viewStateObservable.map { return@map it.filter }.blockingFirst(
                        Filters.ALL
                    )
                )
            return@switchMap array.map {
                SearchChangeResult.Completed(
                    search,
                    Gson().fromJson(it, listType)
                ) as SearchChangeResult
            }
                .startWith(SearchChangeResult.Pending())
                .onErrorReturn { SearchChangeResult.Error(it.localizedMessage) }
        }
            .map { MainViewStateChange.SearchChange(it) as MainViewStateChange }

        val updateTodo = intent { it.updateTodo }
            .switchMap {
                val array = retroService.setStatus(
                    it,
                    viewStateObservable.map { return@map it.filter }.blockingFirst(Filters.ALL)
                )
                return@switchMap array
                    .map {
                        TodoListChangeResult.Completed(
                            Gson().fromJson(
                                it,
                                listType
                            )
                        ) as TodoListChangeResult
                    }
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
                val array = retroService.delete()
                return@switchMap array
                    .map {
                        TodoListChangeResult.Completed(
                            Gson().fromJson(
                                it,
                                listType
                            )
                        ) as TodoListChangeResult
                    }
                    .startWith(TodoListChangeResult.Pending())
                    .onErrorReturn { TodoListChangeResult.Error(it.localizedMessage) }
            }.map {
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

