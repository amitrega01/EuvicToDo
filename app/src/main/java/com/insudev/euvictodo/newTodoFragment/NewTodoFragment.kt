package com.insudev.euvictodo.newTodoFragment

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hannesdorfmann.mosby3.mvi.MviFragment
import com.insudev.euvictodo.newTodoFragment.mvi.NewTodoPresenter
import com.insudev.euvictodo.newTodoFragment.mvi.NewTodoState
import com.insudev.euvictodo.newTodoFragment.mvi.NewTodoView
import com.jakewharton.rxbinding3.view.clicks
import com.jakewharton.rxbinding3.widget.textChanges
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.subjects.PublishSubject
import org.jetbrains.anko.*
import org.jetbrains.anko.support.v4.UI

class NewTodoFragment : MviFragment<NewTodoView, NewTodoPresenter>(), NewTodoView {
    override fun onDestroy() {
        super.onDestroy()
        subscriptions.clear()
    }


    override val newTodoContent = PublishSubject.create<String>()
    override val addNew = PublishSubject.create<Unit>()
    private val subscriptions = CompositeDisposable()

    override fun createPresenter(): NewTodoPresenter {
        return NewTodoPresenter()
    }

    override fun render(state: NewTodoState) {

    }


    companion object {

        fun newInstance(): NewTodoFragment {
            return NewTodoFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return UI {
            linearLayout {

                backgroundColor = Color.parseColor("#FFFFFF")
                elevation = 4f

                gravity = Gravity.CENTER_HORIZONTAL
                padding = 16
                editText {
                    textChanges().map { it.toString() }.subscribe { newTodoContent.onNext(it) }
                        .addTo(subscriptions)
                    hint = "Todo content"
                    width = 700
                }
                button("Add") {
                    backgroundColor = Color.parseColor("#000000")

                    textColor = Color.parseColor("#FFFFFF")
                    clicks().subscribe { addNew.onNext(it) }.addTo(subscriptions)
                }
            }.layoutParams = ViewGroup.LayoutParams(matchParent, wrapContent)
        }.view
    }


}





