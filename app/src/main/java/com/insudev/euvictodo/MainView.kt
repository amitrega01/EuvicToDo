package com.insudev.euvictodo

import com.hannesdorfmann.mosby3.mvp.MvpView
import com.insudev.euvictodo.buisnesslogic.Filters
import com.insudev.euvictodo.buisnesslogic.MainViewState
import com.insudev.euvictodo.models.SortedFilter
import com.insudev.euvictodo.models.SortedSearch
import com.insudev.euvictodo.models.TodoModel
import io.reactivex.Observable

interface MainView : MvpView {
    /**
     * Emits button clicks
     */
    val showAllTodos: Observable<SORTING>
   // fun showAllTodos(): Observable<SORTING>
    val searchTodos : Observable<SortedSearch>
//    val filterUnfinished : Observable<Boolean>
    val showFiltered : Observable<SortedFilter>


    fun render(state: MainViewState)
}

