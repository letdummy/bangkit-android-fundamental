package com.sekalisubmit.githubmu.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.sekalisubmit.githubmu.R
import com.sekalisubmit.githubmu.data.response.GitHubUserFollowResponseItem

class GitHubFollowAdapter(
    private val onClick: (GitHubUserFollowResponseItem) -> Unit
) : ListAdapter<GitHubUserFollowResponseItem, GitHubFollowAdapter.MyViewHolder>(DIFF_CALLBACK) {

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<GitHubUserFollowResponseItem>() {
            override fun areItemsTheSame(oldItem: GitHubUserFollowResponseItem, newItem: GitHubUserFollowResponseItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: GitHubUserFollowResponseItem, newItem: GitHubUserFollowResponseItem): Boolean {
                return oldItem == newItem
            }
        }
    }


    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageList: ImageView = itemView.findViewById(R.id.list_image)
        val usernameList: TextView = itemView.findViewById(R.id.list_title)
        val list_followers: TextView = itemView.findViewById(R.id.list_followers)
        val list_public_repos: TextView = itemView.findViewById(R.id.list_public_repos)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
        return MyViewHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val user = getItem(position)
        Glide.with(holder.itemView.context)
            .load(user.avatarUrl)
            .apply(RequestOptions.circleCropTransform())
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(holder.imageList)
        holder.usernameList.text = user.login
        when (user.publicRepos){
            0 -> holder.list_public_repos.text = "No Public Repo"
            1 -> holder.list_public_repos.text = "${user.publicRepos} Public Repo"
            else -> holder.list_public_repos.text = "${user.publicRepos} Public Repos"
        }

        when (user.followers){
            0 -> holder.list_followers.text = "No Followers"
            1 -> holder.list_followers.text = "${user.followers} Follower"
            else -> holder.list_followers.text = "${user.followers} Followers"
        }

        holder.itemView.setOnClickListener {
            onClick(user)
        }
    }
}