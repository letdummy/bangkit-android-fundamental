package com.sekalisubmit.githubmu.ui.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayoutMediator
import com.sekalisubmit.githubmu.R
import com.sekalisubmit.githubmu.data.local.room.Favs
import com.sekalisubmit.githubmu.data.remote.response.GitHubUserDetailResponse
import com.sekalisubmit.githubmu.databinding.ActivityDetailUserBinding
import com.sekalisubmit.githubmu.ui.adapter.PagerAdapter
import com.sekalisubmit.githubmu.ui.viewmodel.DetailViewModel
import com.sekalisubmit.githubmu.ui.viewmodel.ViewModelFactory

class DetailUserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailUserBinding
    private var isFavorited = false
    private val viewModel by viewModels<DetailViewModel> {
        ViewModelFactory.getInstance(application)
    }

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
                val login = userDetail.login ?: "letdummy"
                val avatarUrl = userDetail.avatarUrl ?: "https://avatars.githubusercontent.com/u/71609913?v=4"
                val publicRepos = userDetail.publicRepos ?: 0
                val followers = userDetail.followers ?: 0
                favHandler(login, avatarUrl, publicRepos, followers)
                updateUI(userDetail)
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.GONE
            }
        }

        binding.detailImage.setOnClickListener{
            viewModel.getFavUser()
        }

        viewModel.fetchGitHubUserDetail(intent.getStringExtra(EXTRA_USER).toString())
    }

    private fun favHandler(login: String, avatarUrl: String, publicRepos: Int, followers: Int) {
        viewModel.isFavorited(login).observe(this) { favs ->
            isFavorited = favs != null

            if (isFavorited) {
                binding.setFav.setImageResource(R.drawable.icon_fav_true)
            } else {
                binding.setFav.setImageResource(R.drawable.icon_fav_false)
            }
        }

        binding.setFav.setOnClickListener {
            val data = Favs(login = login, avatarUrl = avatarUrl, publicRepos = publicRepos, followers = followers)

            if (!isFavorited) {
                viewModel.insert(data)
                isFavorited = true
                binding.setFav.setImageResource(R.drawable.icon_fav_true)
                Log.d(TAG, "Insert: $data")
            } else {
                viewModel.delete(data)
                isFavorited = false
                binding.setFav.setImageResource(R.drawable.icon_fav_false)
                Log.d(TAG, "Delete: $data")
            }
        }
    }

    @SuppressLint("SetTextI18n")
    fun updateUI(userDetail: GitHubUserDetailResponse) {
        binding.detailUsername.text = userDetail.login
        if (userDetail.name == null) {
            binding.detailName.text = userDetail.login
        } else {
            binding.detailName.text = userDetail.name
        }
        if (userDetail.location == null) {
            binding.detailLocation.text = "No Location"
        } else {
            binding.detailLocation.text = userDetail.location
        }
        binding.detailFollowers.text = "${userDetail.followers} Followers"
        binding.detailFollowing.text = "${userDetail.following} Following"
        Glide.with(this)
            .load(userDetail.avatarUrl)
            .circleCrop()
            .into(binding.detailImage)
        binding.separate.text = "|"
        binding.detailIcon.setImageResource(R.drawable.icon_people)
        binding.detailIconLocation.setImageResource(R.drawable.icon_home)
    }
}