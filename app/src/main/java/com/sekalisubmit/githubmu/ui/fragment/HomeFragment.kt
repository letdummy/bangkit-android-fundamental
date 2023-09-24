package com.sekalisubmit.githubmu.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.sekalisubmit.githubmu.databinding.FragmentHomeBinding
import com.sekalisubmit.githubmu.ui.DetailUserActivity
import com.sekalisubmit.githubmu.ui.adapter.GitHubUserAdapter
import com.sekalisubmit.githubmu.ui.viewmodel.MainViewModel

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapterSearch: GitHubUserAdapter

    private lateinit var viewModel: MainViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        val view = binding.root

        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.GONE
            }
        }

        with(binding) {
            searchView.setupWithSearchBar(searchBar)
            searchView
                .editText
                .setOnEditorActionListener { _, _, _ ->
                    searchBar.text = searchView.text
                    searchView.hide()
                    viewModel.fetchGitHubUserSearch(searchView.text.toString())
                    viewModel.userListSearch.observe(viewLifecycleOwner) { userList ->
                        adapterSearch.submitList(userList)
                    }
                    binding.rvHome.adapter = adapterSearch
                    false
                }
        }

        val layoutManager = LinearLayoutManager(context)
        binding.rvHome.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(context, layoutManager.orientation)
        binding.rvHome.addItemDecoration(itemDecoration)

        adapterSearch = GitHubUserAdapter { user ->
            val moveIntent = Intent(context, DetailUserActivity::class.java)
            moveIntent.putExtra(DetailUserActivity.EXTRA_USER, user.login)
            startActivity(moveIntent)
        }

        viewModel.userListSearch.observe(viewLifecycleOwner) { userList ->
            adapterSearch.submitList(userList)
        }

        binding.rvHome.adapter = adapterSearch
        return view
    }
}