package com.insudev.euvictodo.MainList

import com.hannesdorfmann.mosby3.mvp.MvpView
import com.insudev.euvictodo.buisnesslogic.Filters
import com.insudev.euvictodo.models.Sorting
import io.reactivex.Observable

interface MainView : MvpView {
    val initIntent: Observable<Unit>
    val addTodo: Observable<String>
    val changeFilter: Observable<Filters>
    val search: Observable<String>
    val updateTodo: Observable<Int>
    val sortingChange: Observable<Sorting>
    fun render(state: MainViewState)
}

