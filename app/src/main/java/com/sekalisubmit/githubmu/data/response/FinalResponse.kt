package com.sekalisubmit.githubmu.data.response

import com.google.gson.annotations.SerializedName

data class Response(

    @field:SerializedName("avatar_url")
    val avatarUrl: String? = null,

    @field:SerializedName("following_url")
    val followingUrl: String? = null,

    @field:SerializedName("login")
    val login: String? = null,

    @field:SerializedName("followers_url")
    val followersUrl: String? = null,

    @field:SerializedName("url")
    val url: String? = null,

    @field:SerializedName("public_repos")
    val publicRepos: Int? = null
)
