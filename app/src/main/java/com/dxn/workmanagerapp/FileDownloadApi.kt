package com.dxn.workmanagerapp

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.create
import retrofit2.http.GET

interface FileDownloadApi {

    @GET("1080/1920")
    suspend fun downloadImage() : Response<ResponseBody>


    companion object {
        val instance by lazy {
            Retrofit.Builder()
                .baseUrl("https://picsum.photos")
                .build()
                .create(FileDownloadApi::class.java)
        }
    }
}