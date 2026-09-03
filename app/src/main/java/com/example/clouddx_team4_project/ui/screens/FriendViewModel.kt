package com.example.clouddx_team4_project.ui.screens

import android.adservices.adid.AdId
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clouddx_team4_project.network.FriendResponse
import com.example.clouddx_team4_project.network.RetrofitClient
import com.example.clouddx_team4_project.network.UserResponse
import kotlinx.coroutines.launch

class FriendViewModel : ViewModel() {

    private val api = RetrofitClient.friendApi

    var friends by mutableStateOf<List<UserResponse>>(emptyList())
        private set

    var receivedRequests by mutableStateOf<List<FriendResponse>>(emptyList())
        private set

    var sentRequests by mutableStateOf<List<FriendResponse>>(emptyList())
        private set

    var message by mutableStateOf<String?>(null)
        private set

    fun loadAll(currentMemberId: Long) {

        viewModelScope.launch {

            try {

                val friendResponse =
                    api.getFriendList(currentMemberId)

                if (friendResponse.isSuccessful) {
                    friends =
                        friendResponse.body().orEmpty()
                }


                val receivedResponse =
                    api.getReceivedPendingRequests(
                        currentMemberId
                    )

                if (receivedResponse.isSuccessful) {
                    receivedRequests =
                        receivedResponse.body().orEmpty()
                }


                val sentResponse =
                    api.getSentPendingRequests(
                        currentMemberId
                    )

                if (sentResponse.isSuccessful) {
                    sentRequests =
                        sentResponse.body().orEmpty()
                }

            } catch (e: Exception) {

                message =
                    "서버 연결 실패: ${e.message}"
            }
        }
    }


    fun sendFriendRequest(
        currentMemberId: Long,
        name: String,
        loginId: String
    ) {

        viewModelScope.launch {

            try {

                // 아이디로 회원 검색
                val searchResponse =
                    api.searchUserById(loginId)

                val user =
                    searchResponse.body()

                if (!searchResponse.isSuccessful ||
                    user == null
                ) {

                    message =
                        "해당 아이디의 회원을 찾을 수 없습니다."

                    return@launch
                }


                // 입력한 이름과 DB 이름 확인
                if (user.memberName != name) {

                    message =
                        "이름과 아이디가 일치하지 않습니다."

                    return@launch
                }


                val requestResponse =
                    api.sendFriendRequest(
                        requesterId =
                            currentMemberId,

                        receiverId =
                            user.mmbrId
                    )


                if (requestResponse.isSuccessful) {

                    message =
                        "친구 요청을 보냈습니다."

                    loadAll(currentMemberId)

                } else {
                    val errorBody = requestResponse.errorBody()?.string()
                    android.util.Log.e("FriendRequest", "실패 사유 : $errorBody")

                    message =
                        "친구 요청에 실패했습니다."
                }

            } catch (e: Exception) {

                message =
                    "서버 연결 실패: ${e.message}"
            }
        }
    }


    fun acceptRequest(currentMemberId: Long, friendId: Long) {

        viewModelScope.launch {

            try {

                val response =
                    api.acceptFriendRequest(
                        friendId =
                            friendId,

                        memberId =
                            currentMemberId
                    )


                if (response.isSuccessful) {

                    message =
                        "친구 요청을 수락했습니다."

                    loadAll(currentMemberId)

                } else {

                    message =
                        "친구 요청 수락에 실패했습니다."
                }

            } catch (e: Exception) {

                message =
                    "서버 연결 실패: ${e.message}"
            }
        }
    }



    fun rejectRequest(friendId: Long, currentMemberId: Long) {

        viewModelScope.launch {

            try {

                val response =
                    api.rejectFriendRequest(
                        friendId =
                            friendId,

                        memberId =
                            currentMemberId
                    )


                if (response.isSuccessful) {

                    message =
                        "친구 요청을 거절했습니다."

                    loadAll(currentMemberId)

                } else {

                    message =
                        "친구 요청 거절에 실패했습니다."
                }

            } catch (e: Exception) {

                message =
                    "서버 연결 실패: ${e.message}"
            }
        }
    }

    // =========================
    // 내가 보낸 친구 요청 취소
    // =========================

    fun cancelSentRequest(friendId: Long, currentMemberId: Long) {

        viewModelScope.launch {

            try {

                val response =
                    api.cancelFriendRequest(
                        friendId = friendId,
                        memberId = currentMemberId
                    )

                if (response.isSuccessful) {

                    message =
                        "친구 요청을 취소했습니다."

                    loadAll(currentMemberId)

                } else {

                    message =
                        "친구 요청 취소에 실패했습니다."
                }

            } catch (e: Exception) {

                message =
                    "서버 연결 실패: ${e.message}"
            }
        }
    }


    fun deleteFriend(friendMemberId: Long, currentMemberId: Long) {

        viewModelScope.launch {

            try {

                val response =
                    api.deleteFriend(
                        memberId =
                            currentMemberId,

                        friendMemberId =
                            friendMemberId
                    )


                if (response.isSuccessful) {

                    message =
                        "친구를 삭제했습니다."


                    loadAll(currentMemberId)

                } else {

                    message =
                        "친구 삭제에 실패했습니다."
                }

            } catch (e: Exception) {

                message =
                    "서버 연결 실패: ${e.message}"
            }
        }
    }


    fun clearMessage() {
        message = null
    }

    // FriendViewModel.kt 내부에 추가
    fun toggleLocationSharing(currentMemberId: Long, friendMemberId: Long, isSharing: Boolean) {
        viewModelScope.launch {
            try {
                // 백엔드 API 호출
                val response = api.toggleLocationSharing(
                    friendMemberId = friendMemberId,
                    memberId = currentMemberId,
                    isSharing = isSharing
                )

                if (response.isSuccessful) {
                    message = if (isSharing) "위치 공유를 시작합니다." else "위치 공유를 중단합니다."
                    // 실제 서비스에서는 여기서 서버의 최신 상태를 다시 불러오거나(loadAll),
                    // 로컬 상태를 성공 시점에 동기화하는 로직이 들어갑니다.
                } else {
                    message = "위치 공유 상태 변경에 실패했습니다."
                }
            } catch (e: Exception) {
                message = "서버 연결 실패: ${e.message}"
            }
        }
    }
    // ========================================
    // 지도 화면 이동을 위한 내비게이션 트리거 상태
    // ========================================
    var navigateToMapData by mutableStateOf<Triple<String, Double, Double>?>(null)
        private set

    // ========================================
    // 친구 위치 조회 함수 (위치 버튼 클릭 시 호출)
    // ========================================
    fun fetchFriendLocation(requesterId: Long, friendMemberId: Long, friendName: String) {
        viewModelScope.launch {
            try {
                // TODO: 실제 tracking-api가 준비되면 아래 주석을 풀고 연동하세요.

                val response = RetrofitClient.trackingApi.getFriendLocation(
                friendId = friendMemberId,
                requesterId = requesterId
                )

                if (response.isSuccessful && response.body() != null) {
                    val result = response.body()!!

                    if (result.success) {
                    navigateToMapData = Triple(friendName, result.lat, result.lng)
                    } else {
                    message = result.message
                    }
                } else {
                    message = "서버 응답 오류가 발생했습니다."
                }
            } catch (e: Exception) {
                message = "위치 조회 실패: ${e.message}"
            }
        }
    }

    // ========================================
    // 네비게이션 이동 완료 후 상태 초기화
    // ========================================
    fun clearNavigation() {
        navigateToMapData = null
    }
}