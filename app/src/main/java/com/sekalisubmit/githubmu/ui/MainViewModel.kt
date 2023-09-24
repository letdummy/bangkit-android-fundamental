package com.sekalisubmit.githubmu.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sekalisubmit.githubmu.data.response.GitHubUserDetailResponse
import com.sekalisubmit.githubmu.data.response.GitHubUserSearchResponse
import com.sekalisubmit.githubmu.data.response.ItemsItem
import com.sekalisubmit.githubmu.data.retrofit.ApiConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel: ViewModel() {
//    private val _userListOrg = MutableLiveData<List<GitHubOrgResponseItem>>()
//    val userListOrg: LiveData<List<GitHubOrgResponseItem>> get() = _userListOrg

    private val _userListSearch = MutableLiveData<List<ItemsItem?>>()
    val userListSearch: MutableLiveData<List<ItemsItem?>> get() = _userListSearch

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    init {
        fetchGitHubUserSearch("adam")
    }

    companion object {
        private const val TAG = "MainViewModel"
    }

    data class UserDetailInfo(val publicRepos: Int, val followers: Int)

    fun fetchGitHubUserSearch(userLogin: String) {
        _loading.value = true
        val apiService = ApiConfig.getApiService()
        val call = apiService.getUserSearch(userLogin)

        call.enqueue(object : Callback<GitHubUserSearchResponse> {
            override fun onResponse(call: Call<GitHubUserSearchResponse>, response: Response<GitHubUserSearchResponse>) {
                if (response.isSuccessful) {
                    val searchResponse = response.body()
                    val userList = searchResponse?.items ?: emptyList()

                    val usersToFetch = userList.take(15)

                    for (user in usersToFetch) {
                        user?.login?.let { userId ->
                            fetchGithubUserDetail(userId) { userDetailInfo ->
                                val userItem = userList.find { it?.login == userId }
                                userItem?.publicRepos = userDetailInfo.publicRepos
                                userItem?.followers = userDetailInfo.followers
                            }
                        }
                    }

                    CoroutineScope(Dispatchers.Main).launch {
                        delay(700)
                        // idk why but add delay make fetch work properly
                        // if you try below 700 it will bug giving 0 public repos sometimes
                        _userListSearch.value = usersToFetch
                        _loading.value = false
                        // set the loading to false after submitList so that user wouldn't notice the delay
                    }
                } else {
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<GitHubUserSearchResponse>, t: Throwable) {
                _loading.value = false
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }

    private fun fetchGithubUserDetail(userId: String, callback: (UserDetailInfo) -> Unit) {
        val apiService = ApiConfig.getApiService()
        val call = apiService.getUserDetail(userId)

        call.enqueue(object : Callback<GitHubUserDetailResponse> {
            override fun onResponse(call: Call<GitHubUserDetailResponse>, response: Response<GitHubUserDetailResponse>) {
                if (response.isSuccessful) {
                    val userDetailResponse = response.body()
                    if (userDetailResponse != null) {
                        val publicReposCount = userDetailResponse.publicRepos
                        if (publicReposCount != null) {
                            val publicRepos = userDetailResponse.publicRepos
                            val followers = userDetailResponse.followers

                            Log.d(TAG, "Public Repositories Count for $userId: $publicRepos")
                            Log.d(TAG, "Followers Count for $userId: $followers")

                            if (followers != null) {
                                val userDetailInfo = UserDetailInfo(publicRepos, followers)
                                callback(userDetailInfo)
                            }
                        }
                    } else {
                        Log.e(TAG, "Response body is null")
                    }
                } else {
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<GitHubUserDetailResponse>, t: Throwable) {
                Log.e(TAG, "onFailure in fetchGithubUserDetail for user $userId: ${t.message}")
            }
        })
    }

//    private fun fetchGitHubOrganizationData() {
//        _loading.value = true
//        val apiService = ApiConfig.getApiService()
//        val call = apiService.getOrgMembers()
//
//        call.enqueue(object : Callback<List<GitHubOrgResponseItem>> {
//            override fun onResponse(
//                call: Call<List<GitHubOrgResponseItem>>,
//                response: Response<List<GitHubOrgResponseItem>>
//            ) {
//                if (response.isSuccessful) {
//                    val orgList = response.body() ?: emptyList()
//
//                    for (user in orgList){
//                        user?.login?.let { userId ->
//                            fetchGithubUserDetail(userId) { userDetailInfo ->
//                                val userItem = orgList.find { it?.login == userId }
//                                userItem?.publicRepos = userDetailInfo.publicRepos
//                                userItem?.followers = userDetailInfo.followers
//                            }
//                        }}
//                    CoroutineScope(Dispatchers.Main).launch {
//                        delay(700)
//                        // idk why but add delay make fetch work properly
//                        // if you try below 700 it will bug giving 0 public repos sometimes
//                        _userListOrg.value = orgList
//                        _loading.value = false
//                        // set the loading to false after submitList so that user wouldn't notice the delay
//                    }
//                } else {
//                    Log.e(TAG, "onFailure: ${response.message()}")
//                }
//            }
//            override fun onFailure(call: Call<List<GitHubOrgResponseItem>>, t: Throwable) {
//                _loading.value = false
//                Log.e(TAG, "onFailure: ${t.message}")
//            }
//        })
//    }
}