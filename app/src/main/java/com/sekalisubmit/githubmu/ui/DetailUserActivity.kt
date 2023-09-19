package com.sekalisubmit.githubmu.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayoutMediator
import com.sekalisubmit.githubmu.R
import com.sekalisubmit.githubmu.data.response.GitHubUserDetailResponse
import com.sekalisubmit.githubmu.data.retrofit.ApiConfig
import com.sekalisubmit.githubmu.databinding.ActivityDetailUserBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailUserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailUserBinding
    companion object {
        var EXTRA_USER = "extra_user"
        private const val TAG = "DetailUserActivity"
        @StringRes
        private val TAB_TITLES = intArrayOf(
            R.string.follower_tab,
            R.string.following_tab
        )
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailUserBinding.inflate(layoutInflater)
        fetchGitHubUserDetail(intent.getStringExtra(EXTRA_USER).toString())
        setContentView(binding.root)

        val sectionsPagerAdapter = PagerAdapter(this)
        sectionsPagerAdapter.username = intent.getStringExtra(EXTRA_USER).toString()
        val viewPager = binding.viewPager
        viewPager.adapter = sectionsPagerAdapter
        val tabs = binding.tabs
        TabLayoutMediator(tabs, viewPager) { tab, position ->
            tab.text = resources.getString(TAB_TITLES[position])
        }.attach()

        supportActionBar?.elevation = 0f
    }

    private fun fetchGitHubUserDetail(userLogin: String){
        showLoading(true)
        val apiService = ApiConfig.getApiService()
        val call = apiService.getUserDetail(userLogin)

        call.enqueue(object : Callback<GitHubUserDetailResponse> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(
                call: Call<GitHubUserDetailResponse>,
                response: Response<GitHubUserDetailResponse>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        binding.detailLogin.text = responseBody.login
                        binding.detailFollowers.text = "${responseBody.followers} Followers"
                        binding.detailFollowing.text = "${responseBody.following} Following"
                        Glide.with(this@DetailUserActivity)
                            .load(responseBody.avatarUrl)
                            .into(binding.detailImage)
                        binding.separate.text = "|"
                        binding.detailIcon.setImageResource(R.drawable.icon_people)
                    }

                    CoroutineScope(Dispatchers.Main).launch {
                        delay(700)
                        showLoading(false)
                        // set the loading to false after submitList so that user wouldn't notice the delay
                    }
                } else {
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<GitHubUserDetailResponse>, t: Throwable) {
                showLoading(false)
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })

    }

    private fun updateView(){

    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }
}