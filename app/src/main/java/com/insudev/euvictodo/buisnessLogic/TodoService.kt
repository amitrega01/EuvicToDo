package com.insudev.euvictodo.buisnessLogic

import com.google.gson.JsonArray
import com.insudev.euvictodo.models.Filters
import com.insudev.euvictodo.models.Sorting
import com.insudev.euvictodo.models.TodoModel
import io.reactivex.Observable
import retrofit2.http.*


interface TodoService {

    @GET("/todos")
    fun getAllTodos(): Observable<JsonArray>

    @PUT("/todos/add/{content}/{filter}")
    fun addTodo(@Path("content") content: String, @Path("filter") filter: Filters): Observable<ArrayList<TodoModel>>

    @GET("/todos/filter/{filter}/{sorting}/{skip}/{take}")
    fun getFilteredTodos(
        @Path("filter") filter: Filters,
        @Path("sorting") sorting: Sorting,
        @Path("skip") skip: Int,
        @Path("take") take: Int
    ): Observable<ArrayList<TodoModel>>

    @GET("/todos/s/{searchPhrase}/{filter}")
    fun searchTodos(
        @Path("searchPhrase") searchPhrase: String,
        @Path("filter") filter: Filters
    ): Observable<ArrayList<TodoModel>>


    @PUT("/todos/status/{id}/{filter}")
    fun setStatus(@Path("id") id: Int, @Path("filter") filter: Filters): Observable<JsonArray>

    @DELETE("/todos")
    fun delete(): Observable<JsonArray>


    @GET("/todos/nextId")
    fun getNextId(): Observable<Int>

    @POST("/todos/sync")
    fun sync(@Header("Content-Type") contenttype: String = "application/json", @Body toSync: ArrayList<TodoModel>): Observable<Boolean>
}
