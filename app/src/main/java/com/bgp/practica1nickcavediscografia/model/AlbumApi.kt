package com.bgp.practica1nickcavediscografia.model

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url

interface AlbumApi {

    @GET
    fun getAlbums( @Url url: String?):Call<ArrayList<Album>>


    @GET("discography/{id}")
    fun getAlbumDetail(
        @Path("id") id:String?
    ):Call<AlbumDetail>



}