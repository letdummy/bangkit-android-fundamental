package com.sekalisubmit.githubmu.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayoutMediator
import com.sekalisubmit.githubmu.R
import com.sekalisubmit.githubmu.data.response.GitHubUserDetailResponse
import com.sekalisubmit.githubmu.data.retrofit.ApiConfig
import com.sekalisubmit.githubmu.databinding.FragmentDetailUserBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailUserFragment : Fragment() {

    private var _binding: FragmentDetailUserBinding? = null
    private val binding get() = _binding!!

    companion object {
        private const val TAG = "com.sekalisubmit.githubmu.ui.DetailUserFragment"
        @StringRes
        private val TAB_TITLES = intArrayOf(
            R.string.follower_tab,
            R.string.following_tab
        )
        var ANTI_DEFAULT = "anti"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetailUserBinding.inflate(inflater, container, false)

        val userId = arguments?.getString("userId")

        // Fetch and display user details
        if (userId != null) {
            if (userId != "default") {
                fetchGitHubUserDetail(userId)
            } else {
                fetchGitHubUserDetail(ANTI_DEFAULT)
            }
        }

        val sectionsPagerAdapter = PagerAdapter(context as DetailUserActivity)
        if (userId != null) {
            if (userId != "default"){
                sectionsPagerAdapter.username = userId
            } else {
                sectionsPagerAdapter.username = ANTI_DEFAULT
            }
        }
        val viewPager = binding.viewPager
        viewPager.adapter = sectionsPagerAdapter

        val tabs = binding.tabs
        TabLayoutMediator(tabs, viewPager) { tab, position ->
            tab.text = resources.getString(TAB_TITLES[position])
        }.attach()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun fetchGitHubUserDetail(userLogin: String) {
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
                        Glide.with(requireContext())
                            .load(responseBody.avatarUrl)
                            .into(binding.detailImage)
                        binding.separate.text = "|"
                        binding.detailIcon.setImageResource(R.drawable.icon_people)
                    }

                    CoroutineScope(Dispatchers.Main).launch {
                        delay(700)
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
}
