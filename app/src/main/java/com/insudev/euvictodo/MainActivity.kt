package com.insudev.euvictodo

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hannesdorfmann.mosby3.mvi.MviActivity
import com.insudev.euvictodo.MainList.MainPresenter
import com.insudev.euvictodo.MainList.MainView
import com.insudev.euvictodo.MainList.MainViewState
import com.insudev.euvictodo.buisnesslogic.Filters
import com.insudev.euvictodo.dialogNewTodo.NewTodoDialog
import com.insudev.euvictodo.models.Sorting
import com.jakewharton.rxbinding3.recyclerview.dataChanges
import com.jakewharton.rxbinding3.view.clicks
import com.jakewharton.rxbinding3.widget.checkedChanges
import com.jakewharton.rxbinding3.widget.textChanges
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.new_todo_dialog.*


//
//enum class SORTING {
//    ASCENDING,
//    DESCENDING
//}

class MainActivity : MviActivity<MainView, MainPresenter>(),
    MainView {

    lateinit var dialog: NewTodoDialog
    override val initIntent: Observable<Unit> = Observable.just(Unit)


    private lateinit var recyclerView: RecyclerView
    lateinit var viewAdapter: TodoAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager
    val subscriptions = CompositeDisposable()

    override val addTodo = PublishSubject.create<String>()
    override val changeFilter = PublishSubject.create<Filters>()
    override val search = PublishSubject.create<String>()
    override val updateTodo = PublishSubject.create<Int>()
    override val sortingChange = PublishSubject.create<Sorting>()
    override val clearFinished = PublishSubject.create<Unit>()

    override  fun render(state: MainViewState) {
        if (state.isLoadingFailed) {
            Toast.makeText(this, state.message, Toast.LENGTH_LONG)
        } else {
            loadingIndicator.visible = state.isLoading
            clear_button.visible = false
            var temp = when (state.filter) {
                Filters.ALL -> ArrayList(state.todoList
                    .filter { x ->
                        x.content.toLowerCase().contains(state.searchPhrase.toLowerCase())
                    })
                Filters.FINISHED -> {
                    clear_button.visible = true
                    ArrayList(state.todoList
                        .filter { x -> x.status }
                        .filter { x ->
                            x.content.toLowerCase().contains(state.searchPhrase.toLowerCase())
                        })
                }
                Filters.UNFINISHED -> ArrayList(state.todoList
                    .filter { x -> !x.status }
                    .filter { x ->
                        x.content.toLowerCase().contains(state.searchPhrase.toLowerCase())
                    })

            }
            temp = when (state.sorting) {
                Sorting.ASCENDING -> ArrayList(temp.sortedWith(compareByDescending { it.timeStamp }))
                Sorting.DESCENDING -> ArrayList(temp.sortedWith(compareBy { it.timeStamp }))
            }

            viewAdapter.myDataset = temp
            viewAdapter.dataChanges()

            sorting_button.text = when (state.sorting) {
                Sorting.DESCENDING -> "DESC"
                Sorting.ASCENDING -> "ASC"
            }

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
        viewAdapter = TodoAdapter(this)

        recyclerView = findViewById<RecyclerView>(R.id.recycler).apply {
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            setHasFixedSize(true)

            // use a linear layout manager
            layoutManager = viewManager

            // specify an viewAdapter (see also next example)
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
        }.subscribe { search.onNext(it) }.addTo(subscriptions)



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

        }
            .subscribe {
                clearFinished.onNext(it)
            }.addTo(subscriptions)

    }


    override fun createPresenter(): MainPresenter {
        val sharedPreferences = getSharedPreferences("MAIN", Context.MODE_PRIVATE)
        return MainPresenter(sharedPreferences)

    }
// override fun showAllTodos(): Observable<ArrayList<TodoModel>> = RxRecyclerViewAdapter.dataChanges(viewAdapter).map { return@map ArrayList<TodoModel>() }

//    override  val showFiltered : Observable<SortedFilter>
//        get() = RxRadioGroup.checkedChanges(group).map {
//            Log.i("MAPPING", it.toString())
//            if(it == radio_all.id)
//          return@map SortedFilter(Filters.ALL, sorting)
//            else if (it == radio_todo.id)
//                return@map SortedFilter(Filters.UNFINISHED, sorting)
//            else
//                return@map SortedFilter(Filters.FINISHED, sorting)
//
//        }
//
//
//
//
//    override val searchTodos : Observable<SortedSearch>
//        get() = searchText.textChanges().map { SortedSearch(searchText.text.toString(), sorting) }
//
//
//    private fun renderLoadingState() {
//        loadingIndicator.visible = true
//    }
//
//    private fun renderDataState(dataState: MainViewState.DataState) {
//        loadingIndicator.visible = false
//
//        sorting_button.clicks()
//

//        Log.i("JSON!!!", Gson().toJson(dataState.todos   ))
//        viewAdapter.myDataset.clear()
//        viewAdapter.myDataset = dataState.todos
//        viewAdapter.notifyDataSetChanged()
//
//    }
//
//    private fun renderErrorState(errorState: MainViewState.ErrorState) {
//        loadingIndicator.visible = false
//        Toast.makeText(this, "error ${errorState.error}", Toast.LENGTH_LONG).show()
//    }
fun not() {
    viewAdapter.notifyDataSetChanged()
}
    override fun onDestroy() {
        super.onDestroy()
        subscriptions.clear()
    }


}