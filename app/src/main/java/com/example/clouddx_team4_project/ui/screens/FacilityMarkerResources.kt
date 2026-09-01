package com.example.clouddx_team4_project.ui.screens

import com.example.clouddx_team4_project.R

fun getFacilityMarkerResource(
    type: String
): Int {

    return when (type) {

        "CCTV" ->
            R.drawable.marker_cctv


        else ->
            R.drawable.marker_current_location
    }
}