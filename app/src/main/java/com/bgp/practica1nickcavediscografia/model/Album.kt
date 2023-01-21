package com.bgp.practica1nickcavediscografia.model

import com.google.gson.annotations.SerializedName

data class Album(

    @SerializedName("id")
    var id:String? = null,

    @SerializedName("album")
    var album:String? = null,

    @SerializedName("cover")
    var cover:String? = null,

    @SerializedName("year")
    var year:String? = null

)
