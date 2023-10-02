package com.sekalisubmit.githubmu.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sekalisubmit.githubmu.data.remote.response.GitHubUserFollowResponseItem

class FollowViewModel: ViewModel() {
    data class UserDetailInfo(val publicRepos: Int, val followers: Int)

    private val _userListFollow = MutableLiveData<List<GitHubUserFollowResponseItem>?>()
    val userListFollow: MutableLiveData<List<GitHubUserFollowResponseItem>?> get() = _userListFollow

    fun setUserListFollow(responseBody: List<GitHubUserFollowResponseItem>?) {
        _userListFollow.value = responseBody
    }

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    fun setLoading(isLoading: Boolean) {
        _loading.value = isLoading
    }

    init {
        setLoading(true)
    }
}