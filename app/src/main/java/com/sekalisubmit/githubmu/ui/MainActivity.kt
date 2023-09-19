package com.sekalisubmit.githubmu.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.sekalisubmit.githubmu.R
import com.sekalisubmit.githubmu.data.response.GitHubOrgResponseItem
import com.sekalisubmit.githubmu.data.response.GitHubUserDetailResponse
import com.sekalisubmit.githubmu.data.response.GitHubUserSearchResponse
import com.sekalisubmit.githubmu.data.retrofit.ApiConfig
import com.sekalisubmit.githubmu.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: GitHubUserAdapter
    private lateinit var adapterHome: GitHubHomeAdapter

    data class UserDetailInfo(val publicRepos: Int, val followers: Int)

    companion object {
        private const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding) {
            searchView.setupWithSearchBar(searchBar)
            searchView
                .editText
                .setOnEditorActionListener { textView, actionId, event ->
                    searchBar.text = searchView.text
                    searchView.hide()
                    binding.rvHome.adapter = adapter
                    fetchGitHubUserSearch(searchView.text.toString())
                    true
                }
        }

        val layoutManager = LinearLayoutManager(this)
        binding.rvHome.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(this, layoutManager.orientation)
        binding.rvHome.addItemDecoration(itemDecoration)

        adapter = GitHubUserAdapter(
            onClick = { user ->
            val moveIntent = Intent(this@MainActivity, DetailUserActivity::class.java)
            moveIntent.putExtra(DetailUserActivity.EXTRA_USER, user.login)
            startActivity(moveIntent)
        })
        adapterHome = GitHubHomeAdapter(
            onClick = { user ->
                val moveIntent = Intent(this@MainActivity, DetailUserActivity::class.java)
                moveIntent.putExtra(DetailUserActivity.EXTRA_USER, user.login)
                startActivity(moveIntent)
            }
        )

        binding.rvHome.adapter = adapterHome

        fetchGitHubOrganizationData()

        val btnSetting: ImageButton = findViewById(R.id.btn_setting)
        btnSetting.setOnClickListener(){
            val moveIntent = Intent(this@MainActivity, SettingActivity::class.java)
            startActivity(moveIntent)
        }
    }

    private fun fetchGitHubOrganizationData() {
        showLoading(true)
        val apiService = ApiConfig.getApiService()
        val call = apiService.getOrgMembers()

        call.enqueue(object : Callback<List<GitHubOrgResponseItem>> {
            override fun onResponse(
                call: Call<List<GitHubOrgResponseItem>>,
                response: Response<List<GitHubOrgResponseItem>>
            ) {
                if (response.isSuccessful) {
                    val orgList = response.body() ?: emptyList()

                    for (user in orgList){
                        user?.login?.let { userId ->
                            fetchGithubUserDetail(userId) { userDetailInfo ->
                                val userItem = orgList.find { it?.login == userId }
                                userItem?.publicRepos = userDetailInfo.publicRepos
                                userItem?.followers = userDetailInfo.followers
                            }
                    }}
                    CoroutineScope(Dispatchers.Main).launch {
                        delay(700)
                        // idk why but add delay make fetch work properly
                        // if you try below 700 it will bug giving 0 public repos sometimes
                        adapterHome.submitList(orgList)
                        showLoading(false)
                        // set the loading to false after submitList so that user wouldn't notice the delay
                    }
                } else {
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }
            override fun onFailure(call: Call<List<GitHubOrgResponseItem>>, t: Throwable) {
                showLoading(false)
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }


    private fun fetchGitHubUserSearch(userLogin: String = "Adam") {
        showLoading(true)
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
                        adapter.submitList(usersToFetch)
                        showLoading(false)
                        // set the loading to false after submitList so that user wouldn't notice the delay
                    }
                } else {
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<GitHubUserSearchResponse>, t: Throwable) {
                showLoading(false)
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
                        Log.d(TAG, "Public Repositories Count for $userId: $publicReposCount")
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


    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }
}