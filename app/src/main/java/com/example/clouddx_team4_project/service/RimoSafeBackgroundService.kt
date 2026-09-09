package com.example.clouddx_team4_project.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.clouddx_team4_project.R
import com.example.clouddx_team4_project.network.LocationUpdateRequest
import com.example.clouddx_team4_project.network.RetrofitClient
import com.google.android.gms.location.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RimoSafeBackgroundService : Service() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private val CHANNEL_ID = "RimoSafeChannel"
    private val NOTIFICATION_ID = 1001

    override fun onCreate() {
        super.onCreate()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        createNotificationChannel()

        // 포그라운드 서비스 알림 생성 (상단바 고정)
        val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("RIMO 안심 서비스 실행 중")
            .setContentText("백그라운드에서 실시간 위치 및 안전 상태를 모니터링합니다.")
            .setSmallIcon(R.drawable.rimo_logo) // 수정된 로고 리소스 적용
            .setOngoing(true)
            .build()

        startForeground(NOTIFICATION_ID, notification)

        // 백그라운드 위치 추적 시작
        startBackgroundLocationUpdates()
    }

    private fun startBackgroundLocationUpdates() {
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY, 3000L // 3초 주기
        ).setMinUpdateIntervalMillis(2000L).build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                val location = locationResult.lastLocation ?: return

                // TokenManager를 통해 현재 로그인된 memberId 가져오기
                val memberId = RetrofitClient.tokenManager?.getMemberId() ?: return

                val request = LocationUpdateRequest(
                    memberId = memberId,
                    lat = location.latitude,
                    lng = location.longitude
                )

                // 서버 Redis로 실시간 위치 전송 API 호출
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        RetrofitClient.trackingApi.updateLocation(request)
                        Log.d("RIMO_BG_SERVICE", "백그라운드 위치 전송 성공: ${location.latitude}, ${location.longitude}")
                    } catch (e: Exception) {
                        Log.e("RIMO_BG_SERVICE", "백그라운드 위치 전송 실패", e)
                    }
                }
            }
        }

        try {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        } catch (e: SecurityException) {
            Log.e("RIMO_BG_SERVICE", "위치 권한 에러", e)
        }
    }

    private fun createNotificationChannel() {
        // ⭐️ 해결 1, 2: NotificationChannel은 안드로이드 8.0(API 26) 이상에서만 존재하므로 버전 체크 추가
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Rimo Safe Background Channel",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(serviceChannel)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        // ⭐️ 해결 3: removeUpdates() 오타 수정 (정확한 FusedLocationProviderClient 메서드명)
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}