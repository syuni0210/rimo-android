package com.example.clouddx_team4_project.data

import com.google.gson.annotations.SerializedName
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query


interface KakaoLocalApi {

    @GET("v2/local/search/keyword.json")
    suspend fun searchKeyword(
        @Header("Authorization")
        authorization: String,

        @Query("query")
        query: String
    ): KakaoPlaceResponse
}


object KakaoLocalClient {

    val api: KakaoLocalApi by lazy {

        Retrofit.Builder()
            .baseUrl("https://dapi.kakao.com/")
            .addConverterFactory(
                GsonConverterFactory.create()
            )
            .build()
            .create(KakaoLocalApi::class.java)
    }
}


data class KakaoPlaceResponse(

    @SerializedName("documents")
    val documents: List<KakaoPlace>
)


data class KakaoPlace(

    @SerializedName("id")
    val id: String,

    @SerializedName("place_name")
    val placeName: String,

    @SerializedName("address_name")
    val addressName: String,

    @SerializedName("road_address_name")
    val roadAddressName: String,

    // x = 경도
    @SerializedName("x")
    val longitude: String,

    // y = 위도
    @SerializedName("y")
    val latitude: String
)