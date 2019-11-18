package com.insudev.euvictodo

import android.app.AlertDialog

import android.os.Bundle
import android.util.Log
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hannesdorfmann.mosby3.mvi.MviActivity
import com.insudev.euvictodo.dialogNewTodo.NewTodoDialog
import com.insudev.euvictodo.models.EmptyModel
import com.insudev.euvictodo.models.Filters
import com.insudev.euvictodo.models.Sorting
import com.insudev.euvictodo.models.TodoModel
import com.insudev.euvictodo.mvi.MainPresenter
import com.insudev.euvictodo.mvi.MainView
import com.insudev.euvictodo.mvi.MainViewState
import com.jakewharton.rxbinding3.view.clicks
import com.jakewharton.rxbinding3.widget.checkedChanges
import com.jakewharton.rxbinding3.widget.textChanges
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.new_todo_dialog.*


class MainActivity : MviActivity<MainView, MainPresenter>(),
    MainView {

    private lateinit var dialog: NewTodoDialog

    private lateinit var recyclerView: RecyclerView
    lateinit var viewAdapter: DataAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager

    val subscriptions = CompositeDisposable()

    override val initIntent: Observable<Unit> = Observable.just(Unit)
    override val addTodo = PublishSubject.create<String>()
    override val changeFilter = PublishSubject.create<Filters>()
    override val search = PublishSubject.create<String>()
    override val updateTodo = PublishSubject.create<Int>()
    override val sortingChange = PublishSubject.create<Sorting>()
    override val clearFinished = PublishSubject.create<Unit>()
    override val scrollChange = PublishSubject.create<Int>()
    override lateinit var syncList: Observable<Unit>

    private lateinit var slideUp: Animation

    private lateinit var slideDown: Animation

    override fun render(state: MainViewState) {
        loadingIndicator.visible = state.isLoading

        if (state.isLoadingFailed) {

            Toast.makeText(this, state.message, Toast.LENGTH_LONG)
            Log.i("ERR", state.message)
            AlertDialog.Builder(this).setTitle("Error").setMessage(state.message).show()

        } else {

            Log.i("STATE", state.toString())
            viewAdapter.adapterDataList = ArrayList((state.toSync + state.todoList).distinct())
            clear_button.visible = when (state.filter) {
                Filters.FINISHED -> true
                else -> false
            }
            sorting_button.text = when (state.sorting) {
                Sorting.DESCENDING -> "DESC"
                Sorting.ASCENDING -> "ASC"
            }

            if (state.toSync.size >= 1) {
                fab_sync.visible = true

                fab_sync.startAnimation(slideUp)
            }


            viewAdapter.notifyDataSetChanged()

        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up)
        slideDown = AnimationUtils.loadAnimation(this, R.anim.slide_down)

        dialog = NewTodoDialog(this)
        loadingIndicator.visible = true


        fab_newTodo.setOnClickListener {
            dialog.show()

            dialog.addButton.clicks().map {
                viewAdapter.notifyDataSetChanged()
                dialog.hide()
                val content = dialog.contentText.text.toString()
                dialog.contentText.text.clear()
                return@map content
            }.subscribe {
                addTodo.onNext(it)
            }.addTo(subscriptions)
        }

        viewManager = LinearLayoutManager(this)
        viewAdapter = DataAdapter(this)
        recyclerView = findViewById<RecyclerView>(R.id.recycler).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }

        group.checkedChanges().map {
            Log.i("MAPPING", it.toString())
            viewAdapter.notifyDataSetChanged()
            when (it) {
                radio_all.id -> return@map Filters.ALL
                radio_todo.id -> return@map Filters.UNFINISHED
                else -> return@map Filters.FINISHED
            }

        }.subscribe { changeFilter.onNext(it) }.addTo(subscriptions)

        searchText.textChanges().map {
            viewAdapter.notifyDataSetChanged()
            return@map it.toString()
        }
            .subscribe { search.onNext(it) }.addTo(subscriptions)


        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val visibleItemCount = viewManager.childCount
                val totalItemCount = viewManager.itemCount
                val firstVisible =
                    (viewManager as LinearLayoutManager).findFirstVisibleItemPosition()
                if ((visibleItemCount + firstVisible) >= totalItemCount) {
                    if (viewAdapter.adapterDataList.last() is TodoModel) {

                        viewAdapter.adapterDataList.add(EmptyModel("Pobierz paczke", "get"))
                        viewAdapter.notifyDataSetChanged()
                    }
                }
            }
        })

        sorting_button.clicks().map {
            viewAdapter.notifyDataSetChanged()
            when (sorting_button.text.toString()) {
                "DESC" -> return@map Sorting.ASCENDING
                "ASC" -> return@map Sorting.DESCENDING
                else -> return@map Sorting.ASCENDING
            }
        }.subscribe { sortingChange.onNext(it) }.addTo(subscriptions)

        clear_button.clicks().map {
            viewAdapter.notifyDataSetChanged()
        }.subscribe { clearFinished.onNext(it) }.addTo(subscriptions)



        syncList = fab_sync.clicks()

    }

    override fun createPresenter(): MainPresenter {
        return MainPresenter()

    }

    fun not() {
        viewAdapter.notifyDataSetChanged()
    }

    override fun onPause() {
        super.onPause()
        Log.i("LIFECYCLE", "pause")
        syncList = Observable.just(Unit)
    }

    override fun onDestroy() {
        super.onDestroy()
        subscriptions.clear()
    }

}
