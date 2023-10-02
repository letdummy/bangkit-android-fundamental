package com.sekalisubmit.githubmu.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.sekalisubmit.githubmu.databinding.FragmentFavoriteBinding
import com.sekalisubmit.githubmu.ui.activity.DetailUserActivity
import com.sekalisubmit.githubmu.ui.adapter.FavoriteAdapter
import com.sekalisubmit.githubmu.ui.viewmodel.FavoriteViewModel
import com.sekalisubmit.githubmu.ui.viewmodel.ViewModelFactory

class FavoriteFragment : Fragment() {
    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapterFav: FavoriteAdapter

    private val favViewModel by viewModels<FavoriteViewModel> {
        ViewModelFactory.getInstance(requireActivity().application)
    }

    private val navController by lazy { findNavController() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteBinding.inflate(layoutInflater, container, false)

        adapterFav = FavoriteAdapter { user ->
            val moveIntent = Intent(requireContext(), DetailUserActivity::class.java)
            moveIntent.putExtra(DetailUserActivity.EXTRA_USER, user.login)
            startActivity(moveIntent)
        }


        favViewModel.getAllFavUsers().observe(viewLifecycleOwner) { userList ->
            adapterFav.submitList(userList)

            if (userList.isEmpty()) {
                binding.favEmpty.visibility = View.VISIBLE
                binding.rvFavorite.visibility = View.GONE
            } else {
                binding.favEmpty.visibility = View.GONE
                binding.rvFavorite.visibility = View.VISIBLE
            }
        }

        val layoutManager = LinearLayoutManager(requireContext())
        binding.rvFavorite.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(requireContext(), layoutManager.orientation)
        binding.rvFavorite.addItemDecoration(itemDecoration)

        binding.rvFavorite.adapter = adapterFav

        binding.btnSetting.setOnClickListener{
            navController.navigate(FavoriteFragmentDirections.actionFavoriteFragmentToSettingFragment())
        }

        return binding.root
    }
}