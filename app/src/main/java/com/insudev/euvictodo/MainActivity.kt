package com.insudev.euvictodo

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
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

    lateinit var dialog: NewTodoDialog

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

    override fun render(state: MainViewState) {
        loadingIndicator.visible = state.isLoading

        if (state.isLoadingFailed) {
            Toast.makeText(this, state.message, Toast.LENGTH_LONG)
            Log.i("ERR", state.message)
            AlertDialog.Builder(this).setTitle("Error").setMessage(state.message).show()
        } else {
            Log.i("STATE", state.toString())
            viewAdapter.adapterDataList = state.todoList
            clear_button.visible = when (state.filter) {
                Filters.FINISHED -> true
                else -> false
            }
            sorting_button.text = when (state.sorting) {
                Sorting.DESCENDING -> "DESC"
                Sorting.ASCENDING -> "ASC"
            }
            viewAdapter.notifyDataSetChanged()
        }


    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
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
            if (it == radio_all.id)
                return@map Filters.ALL
            else if (it == radio_todo.id)
                return@map Filters.UNFINISHED
            else
                return@map Filters.FINISHED

        }.subscribe { changeFilter.onNext(it) }.addTo(subscriptions)


        searchText.textChanges().map {
            viewAdapter.notifyDataSetChanged()
            return@map it.toString()
        }
            .subscribe { search.onNext(it) }.addTo(subscriptions)
//        recyclerView.scrollStateChanges().map {
//            if (recyclerView.canScrollVertically(1)) return@map 0
//            else{
//                Log.i("ADAPTER", "Adding")
//                viewAdapter.notifyDataSetChanged()
//                return@map 3
//            }}.subscribe {
//            Log.i("SCROLL", it.toString())
//            if (it == 3 && viewAdapter.adapterDataList.last() is TodoModel)
//                viewAdapter.adapterDataList.add(EmptyModel("Pobierz paczke", "get"))
//            viewAdapter.notifyDataSetChanged()
////            scrollChange.onNext(it)
//
//        }.addTo(subscriptions)

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

    }

    override fun createPresenter(): MainPresenter {
        val sharedPreferences = getSharedPreferences("MAIN", Context.MODE_PRIVATE)
        return MainPresenter(sharedPreferences)

    }

    fun not() {
        viewAdapter.notifyDataSetChanged()
    }

    override fun onDestroy() {
        super.onDestroy()
        subscriptions.clear()
    }

}
