package com.sekalisubmit.githubmu.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sekalisubmit.githubmu.data.local.room.Favs
import com.sekalisubmit.githubmu.data.remote.response.GitHubUserDetailResponse
import com.sekalisubmit.githubmu.data.remote.retrofit.ApiConfig
import com.sekalisubmit.githubmu.data.repository.FavsRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailViewModel(application: Application): ViewModel() {
    companion object {
        private const val TAG = "DetailViewModel"
    }

    private val _userDetail = MutableLiveData<GitHubUserDetailResponse?>()
    val userDetail: MutableLiveData<GitHubUserDetailResponse?> get() = _userDetail

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val cFavsRepository: FavsRepository = FavsRepository(application)

    fun setUserDetail(responseBody: GitHubUserDetailResponse?) {
        _userDetail.value = responseBody
    }

    fun isFavorited(username: String) : LiveData<Favs> = cFavsRepository.isFavorited(username)

    fun getFavUser() {
        cFavsRepository.getAllFavUsers()
        Log.d(TAG, "getFavUser: ${cFavsRepository.getAllFavUsers()}")
    }

    fun insert(favs: Favs) {
        cFavsRepository.insert(favs)
        Log.d(TAG, "insert: $favs")
        Log.d(TAG, "insert: ${favs.login}")
        Log.d(TAG, "insert: ${favs.avatarUrl}")
    }

    fun delete(favs: Favs) {
        cFavsRepository.delete(favs)
    }

    fun fetchGitHubUserDetail(userLogin: String) {
        val apiService = ApiConfig.getApiService()
        val call = apiService.getUserDetail(userLogin)
        showLoading(true)

        call.enqueue(object : Callback<GitHubUserDetailResponse> {
            override fun onResponse(
                call: Call<GitHubUserDetailResponse>,
                response: Response<GitHubUserDetailResponse>
            ) {
                if (response.isSuccessful) {
                    showLoading(false)
                    val responseBody = response.body()
                    setUserDetail(responseBody)
                } else {
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<GitHubUserDetailResponse>, t: Throwable) {
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }

    private fun showLoading(isLoading: Boolean) {
        _isLoading.value = isLoading
    }
}
