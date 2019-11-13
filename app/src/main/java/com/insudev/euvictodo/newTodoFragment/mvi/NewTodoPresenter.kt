package com.insudev.euvictodo.newTodoFragment.mvi

import com.hannesdorfmann.mosby3.mvi.MviBasePresenter
import com.insudev.euvictodo.buisnessLogic.TodoRetroService


class NewTodoPresenter : MviBasePresenter<NewTodoView, NewTodoState>() {
    private val retroService = TodoRetroService()
    private val reducer: NewTodoReducer = NewTodoReducer()
    override fun bindIntents() {

        var newTodoContent = intent { it.newTodoContent }
            .map { ContentChangeResult.Completed(it) as ContentChangeResult }
            .startWith(ContentChangeResult.Pending())
            .onErrorReturn { ContentChangeResult.Error(it.localizedMessage) }
            .map { NewTodoStateChange.ContentChange(it) as NewTodoStateChange }

        var addNew = intent { it.addNew }

        var stream =
            newTodoContent.scan(NewTodoState()) { state: NewTodoState, change: NewTodoStateChange ->
                return@scan reducer.reduce(state, change)
            }


        subscribeViewState(stream) { view, viewState ->
            view.render(viewState)
        }


    }


}
