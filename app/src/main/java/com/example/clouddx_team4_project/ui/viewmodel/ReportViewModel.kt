package com.example.clouddx_team4_project.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clouddx_team4_project.network.ReportRecordDto
import com.example.clouddx_team4_project.network.RetrofitClient
import com.example.clouddx_team4_project.network.RoutePreferenceDto
import com.example.clouddx_team4_project.network.SummaryDto
import com.example.clouddx_team4_project.network.TopFriendDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ReportViewModel : ViewModel() {

    private val api =
        RetrofitClient.reportApi


    // ========================================
    // 요약
    // ========================================

    private val _summary =
        MutableStateFlow<SummaryDto?>(null)

    val summary: StateFlow<SummaryDto?> =
        _summary.asStateFlow()


    // ========================================
    // 경로 선호도
    // ========================================

    private val _routePreferences =
        MutableStateFlow<List<RoutePreferenceDto>>(
            emptyList()
        )

    val routePreferences:
            StateFlow<List<RoutePreferenceDto>> =
        _routePreferences.asStateFlow()


    // ========================================
    // 귀가 기록
    // ========================================

    private val _records =
        MutableStateFlow<List<ReportRecordDto>>(
            emptyList()
        )

    val records:
            StateFlow<List<ReportRecordDto>> =
        _records.asStateFlow()


    // ========================================
    // 가장 많이 사용한 친구
    // ========================================

    private val _topFriend =
        MutableStateFlow<TopFriendDto?>(null)

    val topFriend:
            StateFlow<TopFriendDto?> =
        _topFriend.asStateFlow()


    // ========================================
    // 로딩 상태
    // ========================================

    private val _isLoading =
        MutableStateFlow(false)

    val isLoading =
        _isLoading.asStateFlow()


    // ========================================
    // 에러 메시지
    // ========================================

    private val _errorMessage =
        MutableStateFlow<String?>(null)

    val errorMessage =
        _errorMessage.asStateFlow()


    // ========================================
    // 리포트 조회
    // ========================================

    fun loadReport() {

        viewModelScope.launch {

            _isLoading.value =
                true

            _errorMessage.value =
                null

            try {

                // ========================================
                // 현재 로그인 사용자 ID
                // ========================================

                val currentMemberId =
                    RetrofitClient
                        .tokenManager
                        ?.getMemberId()

                if (currentMemberId == null) {

                    _errorMessage.value =
                        "사용자 식별 정보를 찾을 수 없습니다."

                    Log.e(
                        "REPORT_API",
                        "memberId가 없습니다."
                    )

                    return@launch
                }


                // ========================================
                // 사용 리포트 요약
                // ========================================

                _summary.value =
                    api.getSummary(
                        currentMemberId
                    )


                // ========================================
                // 경로 선호도
                // ========================================

                _routePreferences.value =
                    api.getRoutePreference(
                        currentMemberId
                    )


                // ========================================
                // 귀가 기록
                // ========================================

                _records.value =
                    api.getRecords(
                        currentMemberId
                    )


                // ========================================
                // 가장 많이 사용한 친구
                // ========================================

                _topFriend.value =
                    api.getTopFriend(
                        currentMemberId
                    )


                // ========================================
                // 디버깅 로그
                // ========================================

                Log.d(
                    "REPORT_API",
                    "memberId = $currentMemberId"
                )

                Log.d(
                    "REPORT_API",
                    "summary = ${_summary.value}"
                )

                Log.d(
                    "REPORT_API",
                    "routePreferences = ${_routePreferences.value}"
                )

                Log.d(
                    "REPORT_API",
                    "records = ${_records.value}"
                )

                Log.d(
                    "REPORT_API",
                    "topFriend = ${_topFriend.value}"
                )


            } catch (e: Exception) {

                Log.e(
                    "REPORT_API",
                    "리포트 API 호출 실패",
                    e
                )

                _errorMessage.value =
                    e.message
                        ?: "데이터 조회 실패"

            } finally {

                _isLoading.value =
                    false
            }
        }
    }
}