package com.sekalisubmit.githubmu.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.sekalisubmit.githubmu.R
import com.sekalisubmit.githubmu.databinding.FragmentHomeBinding

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
            showLoading(isLoading)
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
            // same with Follow Fragment line 53 - 73
            // please don't forget, this is how we navigate to DetailUserActivity
            // the action.idActivity is safeArgs (for quick material review)
            val action = HomeFragmentDirections.actionHomeFragmentToDetailUserActivity()
            action.idActivity = user.login.toString()
            findNavController().navigate(action)
        }

        // I want to implement when user open app for the first time
        // the app will show the list of dicodingacademy organization members
        // I tried so hard to implement this, but I can't
        // it feels like I over engineered this
        // sorry, maybe I will try this feature in Submission 2

//        adapterHome = GitHubHomeAdapter { user ->
//            val action = HomeFragmentDirections.actionHomeFragmentToDetailUserActivity()
//            action.idActivity = user.login.toString()
//            findNavController().navigate(action)
//        }

        viewModel.userListSearch.observe(viewLifecycleOwner) { userList ->
            adapterSearch.submitList(userList)
        }

        binding.rvHome.adapter = adapterSearch

        val navController = findNavController()

        binding.btnSetting.setOnClickListener {
            navController.navigate(R.id.action_homeFragment_to_settingFragment)
        }

        return view
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }
}