package com.sekalisubmit.githubmu.data.remote.response

import com.google.gson.annotations.SerializedName

data class GitHubUserSearchResponse(

	@field:SerializedName("total_count")
	val totalCount: Int? = null,

	@field:SerializedName("items")
	val items: List<ItemsItem?>? = null
)

data class ItemsItem(

	@field:SerializedName("avatar_url")
	val avatarUrl: String? = null,

	@field:SerializedName("login")
	val login: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("url")
	val url: String? = null,

	@field:SerializedName("public_repos")
	var publicRepos: Int? = 0,

	@field:SerializedName("followers")
	var followers: Int? = 0
)
