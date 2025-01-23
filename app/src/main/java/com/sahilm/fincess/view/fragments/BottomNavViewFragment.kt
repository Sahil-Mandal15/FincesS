package com.sahilm.fincess.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.sahilm.fincess.R
import com.sahilm.fincess.auth.CredentialHelper
import com.sahilm.fincess.databinding.FragmentBottomNavViewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BottomNavViewFragment : Fragment() {

    private var _binding: FragmentBottomNavViewBinding? = null
    private val binding get() = _binding!!
    private lateinit var credentialHelper: CredentialHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentBottomNavViewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        credentialHelper = CredentialHelper(requireContext())
        lifecycleScope.launch {
            credentialHelper.loginState.collect { loginState ->
                Glide.with(this@BottomNavViewFragment)
                    .load(loginState.userPhotoUri)
                    .into(binding.ivUserPfp)
            }
        }

        setupBottomNavView()
    }

    private fun setupBottomNavView() {
        binding.llTransaction.setOnClickListener {
            findNavController().navigate(R.id.homeScreenFragment)
        }
        binding.llStats.setOnClickListener {
            findNavController().navigate(R.id.statsFragment)
        }
        binding.llProfile.setOnClickListener {
            findNavController().navigate(R.id.profileFragment)
        }
    }
}