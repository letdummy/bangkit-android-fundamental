package com.sekalisubmit.githubmu.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.sekalisubmit.githubmu.R
import com.sekalisubmit.githubmu.data.remote.response.GitHubUserDetailResponse
import com.sekalisubmit.githubmu.data.remote.response.GitHubUserFollowResponseItem
import com.sekalisubmit.githubmu.data.remote.retrofit.ApiConfig
import com.sekalisubmit.githubmu.databinding.FragmentFollowBinding
import com.sekalisubmit.githubmu.ui.DetailUserActivity
import com.sekalisubmit.githubmu.ui.adapter.GitHubFollowAdapter
import com.sekalisubmit.githubmu.ui.viewmodel.FollowViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FollowFragment : Fragment() {
    companion object {
        const val ARG_POSITION: String = "position"
        const val ARG_USERNAME: String = "username"
        private const val TAG = "FollowFragment"
    }

    private lateinit var binding: FragmentFollowBinding
    private lateinit var adapter: GitHubFollowAdapter
    private lateinit var viewModel: FollowViewModel

    data class UserDetailInfo(val publicRepos: Int, val followers: Int)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_follow, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentFollowBinding.bind(view)

        viewModel = ViewModelProvider(this)[FollowViewModel::class.java]

        val layoutManager = LinearLayoutManager(activity)
        binding.rvFollow.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(activity, layoutManager.orientation)
        binding.rvFollow.addItemDecoration(itemDecoration)

        adapter = GitHubFollowAdapter(onClick = { user ->
            val intent = Intent(activity, DetailUserActivity::class.java)
            intent.putExtra(DetailUserActivity.EXTRA_USER, user.login)
            startActivity(intent)
        })
        binding.rvFollow.adapter = adapter

        val position = arguments?.getInt(ARG_POSITION)
        val username = arguments?.getString(ARG_USERNAME)

        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading){
                when (position) {
                    0 -> {
                        fetchGitHubUserFollow(username.toString(), "followers")
                    }
                    else -> {
                        fetchGitHubUserFollow(username.toString(), "following")
                    }
                }
            }
        }

        viewModel.userListFollow.observe(viewLifecycleOwner) { userList ->
            adapter.submitList(userList)
            viewModel.setLoading(false)
        }

    }

    private fun fetchGitHubUserFollow(userId: String, target: String){
        val apiService = ApiConfig.getApiService()
        val call = apiService.getUserFollowers(userId, target)
        showLoading(true)
        call.enqueue(object : Callback<List<GitHubUserFollowResponseItem>>{
            override fun onResponse(
                call: Call<List<GitHubUserFollowResponseItem>>,
                response: Response<List<GitHubUserFollowResponseItem>>
            ) {
                if (response.isSuccessful){
                    val followResponse = response.body()
                    val userList = followResponse ?: emptyList()

                    val usersToFetch = userList.take(20)

                    for (user in usersToFetch) {
                        user?.login?.let { userId ->
                            fetchGithubUserDetail(userId) { userDetailInfo ->
                                val userItem = userList.find { it?.login == userId }
                                userItem?.publicRepos = userDetailInfo.publicRepos
                                userItem?.followers = userDetailInfo.followers
                            }
                        }
                    }

                    // okay, so basically if I put under 1000 ms delay,
                    // chance to get bug 0 value is really high
                    lifecycleScope.launch {
                        delay(1000)
                        adapter.submitList(usersToFetch)
                        showLoading(false)
                        viewModel.setUserListFollow(response.body())
                    }

                } else {
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<GitHubUserFollowResponseItem>>, t: Throwable) {
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
                                val userDetailInfo =
                                    UserDetailInfo(publicRepos, followers)
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