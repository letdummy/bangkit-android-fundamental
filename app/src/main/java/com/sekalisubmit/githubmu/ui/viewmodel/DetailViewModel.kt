package com.sekalisubmit.githubmu.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sekalisubmit.githubmu.data.response.GitHubUserDetailResponse

class DetailViewModel: ViewModel() {
    fun setUserDetail(responseBody: GitHubUserDetailResponse?) {
        _userDetail.value = responseBody
    }

    private val _userDetail = MutableLiveData<GitHubUserDetailResponse?>()
    val userDetail: MutableLiveData<GitHubUserDetailResponse?> get() = _userDetail

}