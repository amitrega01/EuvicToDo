package com.insudev.euvictodo.newTodoFragment.mvi

import com.hannesdorfmann.mosby3.mvp.MvpView
import io.reactivex.Observable

interface NewTodoView : MvpView {
    val newTodoContent: Observable<String>
    val addNew: Observable<Unit>

    fun render(state: NewTodoState)
}
