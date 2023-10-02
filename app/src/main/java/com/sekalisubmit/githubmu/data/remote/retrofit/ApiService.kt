package com.sekalisubmit.githubmu.data.remote.retrofit

import com.sekalisubmit.githubmu.data.remote.response.GitHubUserDetailResponse
import com.sekalisubmit.githubmu.data.remote.response.GitHubUserFollowResponseItem
import com.sekalisubmit.githubmu.data.remote.response.GitHubUserSearchResponse
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @GET("search/users")
    fun getUserSearch(
        @Query("q") query: String
    ): Call<GitHubUserSearchResponse>

    @GET("users/{userId}")
    fun getUserDetail(
        @Path("userId") userId: String
    ): Call<GitHubUserDetailResponse>

    @GET("users/{userId}/{target}")
    fun getUserFollowers(
        @Path("userId") userId: String,
        @Path("target") target: String
    ): Call<List<GitHubUserFollowResponseItem>>
}