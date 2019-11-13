package com.insudev.euvictodo

import com.google.gson.JsonArray
import com.insudev.euvictodo.buisnesslogic.Filters
import io.reactivex.Observable
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path


interface TodoService {

    @GET("/todos")
    fun getAllTodos(): Observable<JsonArray>

    @PUT("/todos/add/{content}/{filter}")
    fun addTodo(@Path("content") content: String, @Path("filter") filter: Filters): Observable<JsonArray>

    @GET("/todos/{filter}")
    fun getFilteredTodos(@Path("filter") filter: Filters): Observable<JsonArray>

    @PUT("/todos/status/{id}/{filter}")
    fun setStatus(@Path("id") id: Int, @Path("filter") filter: Filters): Observable<JsonArray>

    @DELETE("/todos")
    fun delete(): Observable<JsonArray>

}
