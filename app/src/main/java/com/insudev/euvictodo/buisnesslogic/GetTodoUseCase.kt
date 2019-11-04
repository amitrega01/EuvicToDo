package com.insudev.euvictodo.buisnesslogic

import android.content.SharedPreferences
import android.util.Log
import com.google.type.Date
import com.insudev.euvictodo.SORTING
import com.insudev.euvictodo.data.Repository
import com.insudev.euvictodo.models.TodoModel
import io.reactivex.Observable
import java.time.LocalDate
import java.util.*
import kotlin.collections.ArrayList

enum class Filters {
    FINISHED,
    UNFINISHED,
    ALL
}

object GetTodoUseCase {

    fun getFiltered(sharedPrefs: SharedPreferences,filter : Filters, sorting: SORTING) : Observable<MainViewState> {
        when(filter) {
            Filters.ALL-> return getAllTodos(sharedPrefs,sorting)
            Filters.UNFINISHED -> return  getUnfinished(sharedPrefs,sorting)
            Filters.FINISHED ->    return  getFinished(sharedPrefs,sorting)
        }
    }

     fun getAllTodos(sharedPrefs : SharedPreferences, sorting: SORTING): Observable<MainViewState> {
         Log.i("SORTING", sorting.toString())
         return Repository.loadTodos(sharedPrefs,sorting)
                 .map<MainViewState> { MainViewState.DataState(it) }
                 .startWith(MainViewState.LoadingState)
                 .onErrorReturn { MainViewState.ErrorState(it) }



    }
    private fun getUnfinished(sharedPrefs : SharedPreferences, sorting: SORTING): Observable<MainViewState> {
        Log.i("POS", "getUnifinished")
        return Repository.loadTodos(sharedPrefs, sorting)
            .map<MainViewState> { MainViewState.DataState(it.filter { x->x.status == false } as ArrayList<TodoModel>)}
            .startWith(MainViewState.LoadingState)
            .onErrorReturn { MainViewState.ErrorState(it) }
    }



    private fun getFinished(sharedPrefs: SharedPreferences, sorting: SORTING): Observable<MainViewState> {
        Log.i("POS", "getFinished")
        return Repository.loadTodos(sharedPrefs, sorting)
            .map<MainViewState> { MainViewState.DataState(it.filter { x->x.status == true } as ArrayList<TodoModel>)}
            .startWith(MainViewState.LoadingState)
            .onErrorReturn { MainViewState.ErrorState(it) }
    }

    fun searchTodos(sharedPrefs : SharedPreferences, phrase: String, sorting: SORTING): Observable<MainViewState>{
        if (phrase == "")  return getAllTodos(sharedPrefs,sorting);
        return Repository.loadTodos(sharedPrefs, sorting)
            .map<MainViewState> {MainViewState.DataState(it.filter { x-> x.content.contains(phrase) }as ArrayList<TodoModel> )}
            .startWith(MainViewState.LoadingState)
            .onErrorReturn { MainViewState.ErrorState(it) }

    }
}
