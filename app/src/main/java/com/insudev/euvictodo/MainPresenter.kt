package com.insudev.euvictodo

import android.content.SharedPreferences
import com.hannesdorfmann.mosby3.mvi.MviBasePresenter
import com.insudev.euvictodo.buisnesslogic.Filters
import com.insudev.euvictodo.buisnesslogic.GetTodoUseCase
import com.insudev.euvictodo.buisnesslogic.MainViewState
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class MainPresenter(sharedPrefs : SharedPreferences) : MviBasePresenter<MainView, MainViewState>() {

    val sharedPrefs = sharedPrefs;
    override fun bindIntents() {
        val mainState: Observable<MainViewState> = intent(MainView::showAllTodos)
                .subscribeOn(Schedulers.io())
                .debounce(400, TimeUnit.MILLISECONDS)
                .switchMap { GetTodoUseCase.getAllTodos(sharedPrefs, it) }
//                .doOnNext { Timber.d("Received new state: " + it) }
                .observeOn(AndroidSchedulers.mainThread())

        val searchIntent  =intent { it.searchTodos }
            .subscribeOn(Schedulers.io())
            .debounce(400, TimeUnit.MILLISECONDS)
            .switchMap { GetTodoUseCase.searchTodos(sharedPrefs, it.phrase,it.sorting) }
            .observeOn(AndroidSchedulers.mainThread())

        val showFiltered = intent { it.showFiltered }
            .subscribeOn(Schedulers.io())
            .debounce(400,TimeUnit.MILLISECONDS)
            .switchMap { GetTodoUseCase.getFiltered(sharedPrefs, it.filter, it.sorting) }
            .observeOn(AndroidSchedulers.mainThread())




        val stream = Observable
            .merge(mainState,searchIntent, showFiltered )

        subscribeViewState(stream, MainView::render)
    }
}

