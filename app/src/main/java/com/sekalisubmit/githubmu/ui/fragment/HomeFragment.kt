package com.sekalisubmit.githubmu.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
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
    private val navController by lazy { findNavController() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root

        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        setupRecyclerView()
        setupSearchView()

        binding.btnFav.setOnClickListener {
            navController.navigate(HomeFragmentDirections.actionHomeFragmentToFavoriteFragment())
        }

        return view
    }

    private fun setupRecyclerView() {
        adapterSearch = GitHubUserAdapter { user ->
            val moveIntent = Intent(requireContext(), DetailUserActivity::class.java)
            moveIntent.putExtra(DetailUserActivity.EXTRA_USER, user.login)
            startActivity(moveIntent)
        }

        val layoutManager = LinearLayoutManager(requireContext())
        binding.rvHome.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(requireContext(), layoutManager.orientation)
        binding.rvHome.addItemDecoration(itemDecoration)

        binding.rvHome.adapter = adapterSearch
    }

    private fun setupSearchView() {
        binding.searchView.setupWithSearchBar(binding.searchBar)
        binding.searchView.editText.setOnEditorActionListener { _, _, _ ->
            binding.searchBar.text = binding.searchView.text
            binding.searchView.hide()
            viewModel.fetchGitHubUserSearch(binding.searchView.text.toString())
            return@setOnEditorActionListener false
        }

        viewModel.userListSearch.observe(viewLifecycleOwner, Observer { userList ->
            adapterSearch.submitList(userList)
        })

        viewModel.loading.observe(viewLifecycleOwner, Observer { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}