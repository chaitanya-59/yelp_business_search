package com.assignment.chaitanya.flows.webservice

import com.assignment.chaitanya.flows.pojo.BusinessesList
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface Api {
    @GET("v3/businesses/search")
    fun loadBusiness(@Query("term") term: String, @Query("latitude") latitude: String, @Query("longitude") longitude: String): Call<BusinessesList>
}