package com.sekalisubmit.githubmu.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayoutMediator
import com.sekalisubmit.githubmu.R
import com.sekalisubmit.githubmu.data.response.GitHubUserDetailResponse
import com.sekalisubmit.githubmu.data.retrofit.ApiConfig
import com.sekalisubmit.githubmu.databinding.ActivityDetailUserBinding
import com.sekalisubmit.githubmu.ui.adapter.PagerAdapter
import com.sekalisubmit.githubmu.ui.viewmodel.DetailViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailUserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailUserBinding
    private lateinit var viewModel: DetailViewModel

    companion object {
        const val EXTRA_USER = "extra_user"
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
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[DetailViewModel::class.java]

        val sectionsPagerAdapter = PagerAdapter(this)
        sectionsPagerAdapter.username = intent.getStringExtra(EXTRA_USER).toString()
        val viewPager = binding.viewPager
        viewPager.adapter = sectionsPagerAdapter
        val tabs = binding.tabs
        TabLayoutMediator(tabs, viewPager) { tab, position ->
            tab.text = resources.getString(TAB_TITLES[position])
        }.attach()

        viewModel.userDetail.observe(this) { userDetail ->
            userDetail?.let {
                updateUI(userDetail)
            }
        }

        fetchGitHubUserDetail(intent.getStringExtra(EXTRA_USER).toString())
    }

    private fun fetchGitHubUserDetail(userLogin: String) {
        val apiService = ApiConfig.getApiService()
        val call = apiService.getUserDetail(userLogin)

        call.enqueue(object : Callback<GitHubUserDetailResponse> {
            override fun onResponse(
                call: Call<GitHubUserDetailResponse>,
                response: Response<GitHubUserDetailResponse>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    viewModel.setUserDetail(responseBody)
                    responseBody?.let {
                        updateUI(it)
                    }
                } else {
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<GitHubUserDetailResponse>, t: Throwable) {
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }

    @SuppressLint("SetTextI18n")
    private fun updateUI(userDetail: GitHubUserDetailResponse) {
        binding.detailLogin.text = userDetail.login
        binding.detailFollowers.text = "${userDetail.followers} Followers"
        binding.detailFollowing.text = "${userDetail.following} Following"
        Glide.with(this@DetailUserActivity)
            .load(userDetail.avatarUrl)
            .into(binding.detailImage)
        binding.separate.text = "|"
        binding.detailIcon.setImageResource(R.drawable.icon_people)
    }
}