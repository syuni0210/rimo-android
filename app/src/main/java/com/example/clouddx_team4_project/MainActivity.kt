package com.example.clouddx_team4_project

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.clouddx_team4_project.ui.navigation.AppNavigation
import com.kakao.vectormap.MapView


class MainActivity : ComponentActivity() {

    companion object {
        var mapView: MapView? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AppNavigation()
        }
    }


    override fun onResume() {
        super.onResume()

        mapView?.resume()
    }


    override fun onPause() {

        mapView?.pause()

        super.onPause()
    }
}