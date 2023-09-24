package com.sekalisubmit.githubmu.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.sekalisubmit.githubmu.R
import com.sekalisubmit.githubmu.data.response.GitHubUserDetailResponse
import com.sekalisubmit.githubmu.data.response.GitHubUserFollowResponseItem
import com.sekalisubmit.githubmu.data.retrofit.ApiConfig
import com.sekalisubmit.githubmu.databinding.FragmentFollowBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FollowFragment : Fragment() {

    private lateinit var binding: FragmentFollowBinding
    private lateinit var adapter: GitHubFollowAdapter

    data class UserDetailInfo(val publicRepos: Int, val followers: Int)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_follow, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentFollowBinding.bind(view)

        val layoutManager = LinearLayoutManager(activity)
        binding.rvFollow.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(activity, layoutManager.orientation)
        binding.rvFollow.addItemDecoration(itemDecoration)

        val position = arguments?.getInt(ARG_POSITION)
        val username = arguments?.getString(ARG_USERNAME)

        when (position) {
            0 -> {
                adapter = GitHubFollowAdapter{ user ->
                    val action = DetailUserFragmentDirections.actionDetailUserFragmentSelf()
                    action.userId = user.login.toString()
                    findNavController().navigate(action)
                }
                binding.rvFollow.adapter = adapter
                fetchGitHubUserFollow(username.toString(), "followers")
            }
            else -> {
                adapter = GitHubFollowAdapter{ user ->
                    val action = DetailUserFragmentDirections.actionDetailUserFragmentSelf()
                    action.userId = user.login.toString()
                    findNavController().navigate(action)
                }
                binding.rvFollow.adapter = adapter
                fetchGitHubUserFollow(username.toString(), "following")
            }
        }
    }

    companion object {
        const val ARG_POSITION: String = "position"
        const val ARG_USERNAME: String = "username"
        private const val TAG = "FollowFragment"
    }

    private fun fetchGitHubUserFollow(userId: String, target: String){
        showLoading(true)
        val apiService = ApiConfig.getApiService()
        val call = apiService.getUserFollowers(userId, target)

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