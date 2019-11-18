package com.insudev.euvictodo.buisnessLogic

import com.insudev.euvictodo.models.Filters
import com.insudev.euvictodo.models.Sorting
import com.insudev.euvictodo.models.TodoModel
import io.reactivex.Observable
import retrofit2.http.*


interface TodoService {


    @GET("/todos/filter/{filter}/{sorting}/{skip}/{take}")
    fun getFilteredTodos(
        @Path("filter") filter: Filters,
        @Path("sorting") sorting: Sorting,
        @Path("skip") skip: Int,
        @Path("take") take: Int
    ): Observable<ArrayList<TodoModel>>


    @GET("/todos/nextId")
    fun getNextId(): Observable<Int>

    @POST("/todos/sync")
    fun sync(@Header("Content-Type") contenttype: String = "application/json", @Body toSync: ArrayList<TodoModel>): Observable<Boolean>
}
