package com.sahilm.fincess.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.sahilm.fincess.R
import com.sahilm.fincess.auth.CredentialHelper
import com.sahilm.fincess.databinding.FragmentUserAuthBinding
import com.sahilm.fincess.viewmodel.FincessViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UserAuthFragment() : Fragment() {

    private var _binding: FragmentUserAuthBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FincessViewModel by activityViewModels()
    lateinit var credentialHelper: CredentialHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentUserAuthBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        credentialHelper = CredentialHelper(requireContext())

        binding.btnSignIn.setOnClickListener {
            lifecycleScope.launch {
                credentialHelper.signIn()
                findNavController().navigate(R.id.action_userAuthFragment_to_homeScreenFragment)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        credentialHelper.clearCredentialManager()
    }
}