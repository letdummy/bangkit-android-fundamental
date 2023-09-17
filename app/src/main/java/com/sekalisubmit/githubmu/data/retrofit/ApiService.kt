package com.sekalisubmit.githubmu.data.retrofit

import com.sekalisubmit.githubmu.data.response.GitHubOrgResponse
import com.sekalisubmit.githubmu.data.response.GitHubUserDetailResponse
import com.sekalisubmit.githubmu.data.response.GitHubUserSearchResponse
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

    @GET("orgs/dicodingacademy/members")
    fun getOrgMembers(): Call<GitHubOrgResponse>
}