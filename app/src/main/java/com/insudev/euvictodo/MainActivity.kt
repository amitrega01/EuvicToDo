package com.insudev.euvictodo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.hannesdorfmann.mosby3.mvi.MviActivity
import com.insudev.euvictodo.buisnesslogic.Filters
import com.insudev.euvictodo.buisnesslogic.MainViewState
import com.insudev.euvictodo.models.SortedFilter
import com.insudev.euvictodo.models.SortedSearch
import com.insudev.euvictodo.models.TodoModel
import com.insudev.euvictodo.newTodo.NewTodoActivity
import com.jakewharton.rxbinding2.support.v7.widget.RxRecyclerViewAdapter
import com.jakewharton.rxbinding2.view.attaches
import com.jakewharton.rxbinding2.widget.RxRadioGroup
import com.jakewharton.rxbinding2.widget.checkedChanges
import com.jakewharton.rxbinding2.widget.textChanges
import com.jakewharton.rxbinding3.view.clicks
import com.jakewharton.rxbinding3.widget.checkedChanges
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.collections.ArrayList



enum class SORTING {
    ASCENDING,
    DESCENDING
}

class MainActivity : MviActivity<MainView, MainPresenter>(), MainView {
    override val showAllTodos = PublishSubject.create<SORTING>()
    private var sorting = SORTING.ASCENDING;


    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: TodoAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager
    private val subscriptions = CompositeDisposable()


    override  fun render(state: MainViewState) {
        when(state) {
            is MainViewState.LoadingState -> renderLoadingState()
            is MainViewState.DataState -> renderDataState(state)
            is MainViewState.ErrorState -> renderErrorState(state)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        fab_newTodo.setOnClickListener {
            val intent = Intent(baseContext,NewTodoActivity::class.java)
            startActivity(intent)
        }


        loadingIndicator.visible = true

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

        recyclerView.attaches().subscribe {
            showAllTodos.onNext(SORTING.ASCENDING)
        }.addTo(subscriptions)

    }

     fun statusChange() {
        viewAdapter.notifyDataSetChanged()

    }

    override fun createPresenter() : MainPresenter{

        val sharedPreferences = getSharedPreferences("MAIN", Context.MODE_PRIVATE);
        return MainPresenter(sharedPreferences)

    }
// override fun showAllTodos(): Observable<ArrayList<TodoModel>> = RxRecyclerViewAdapter.dataChanges(viewAdapter).map { return@map ArrayList<TodoModel>() }

    override  val showFiltered : Observable<SortedFilter>
        get() = RxRadioGroup.checkedChanges(group).map {
            Log.i("MAPPING", it.toString())
            if(it == radio_all.id)
          return@map SortedFilter(Filters.ALL, sorting)
            else if (it == radio_todo.id)
                return@map SortedFilter(Filters.UNFINISHED, sorting)
            else
                return@map SortedFilter(Filters.FINISHED, sorting)

        }




    override val searchTodos : Observable<SortedSearch>
        get() = searchText.textChanges().map { SortedSearch(searchText.text.toString(), sorting) }


    private fun renderLoadingState() {
        loadingIndicator.visible = true
    }

    private fun renderDataState(dataState: MainViewState.DataState) {
        loadingIndicator.visible = false

        sorting_button.clicks()

        sorting_button.setOnClickListener {

            Log.d("SORTING", sorting.toString())
            when (sorting)  {
                SORTING.DESCENDING -> {
                    sorting_button.text = "ASC"
                    sorting= SORTING.ASCENDING
                }
                SORTING.ASCENDING-> {
                    sorting_button.text = "DESC"
                    sorting =  SORTING.DESCENDING
                }
            }
        }
        Log.i("JSON!!!", Gson().toJson(dataState.todos   ))
        viewAdapter.myDataset.clear()
        viewAdapter.myDataset = dataState.todos
        viewAdapter.notifyDataSetChanged()

    }

    private fun renderErrorState(errorState: MainViewState.ErrorState) {
        loadingIndicator.visible = false
        Toast.makeText(this, "error ${errorState.error}", Toast.LENGTH_LONG).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        subscriptions.clear()
    }



}