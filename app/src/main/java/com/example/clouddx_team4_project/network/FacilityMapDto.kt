package com.example.clouddx_team4_project.network

data class FacilityMapDto(

    val id: Long,

    val type: String,

    val name: String?,

    val address: String?,

    val lat: Double,

    val lng: Double
)