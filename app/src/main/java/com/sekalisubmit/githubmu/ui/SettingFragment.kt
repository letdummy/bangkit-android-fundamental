package com.sekalisubmit.githubmu.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.sekalisubmit.githubmu.R
import com.sekalisubmit.githubmu.databinding.FragmentSettingBinding

class SettingFragment : Fragment() {
    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingBinding.inflate(inflater, container, false)

        val navController = findNavController()

        binding.btnHome.setOnClickListener {
            // Use the new action to navigate to HomeFragment
            navController.navigate(R.id.action_settingFragment_to_homeFragment)
        }
        return binding.root
    }
}