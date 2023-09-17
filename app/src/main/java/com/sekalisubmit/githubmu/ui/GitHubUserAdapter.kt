package com.sekalisubmit.githubmu.ui

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
import com.sekalisubmit.githubmu.data.response.ItemsItem

class GitHubUserAdapter(
    private val onClick: (ItemsItem) -> Unit
) : ListAdapter<ItemsItem, GitHubUserAdapter.MyViewHolder>(DIFF_CALLBACK) {
    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ItemsItem>() {
            override fun areItemsTheSame(oldItem: ItemsItem, newItem: ItemsItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: ItemsItem, newItem: ItemsItem): Boolean {
                return oldItem == newItem
            }
        }
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageList: ImageView = itemView.findViewById(R.id.list_image)
        val usernameList: TextView = itemView.findViewById(R.id.list_title)
        val infoList: TextView = itemView.findViewById(R.id.list_info)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
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
            0 -> holder.infoList.text = "No Repository"
            1 -> holder.infoList.text = "${user.publicRepos} Repository"
            else -> holder.infoList.text = "${user.publicRepos} Repositories"
        }

        holder.itemView.setOnClickListener {
            onClick(user)
        }
    }
}