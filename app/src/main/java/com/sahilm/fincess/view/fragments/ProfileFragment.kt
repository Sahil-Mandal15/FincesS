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
import com.sahilm.fincess.databinding.FragmentProfileBinding
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var credentialHelper: CredentialHelper


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return (binding.root)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        credentialHelper = CredentialHelper(requireContext())

        lifecycleScope.launch {
            credentialHelper.loginState.collect { loginState ->
                val userPfp = loginState.userPhotoUri

                Glide.with(this@ProfileFragment)
                    .load(userPfp)
                    .into(binding.ivUserPfp)

                binding.tvUserName.text = loginState.userName
            }
        }

            binding.btnSignOut.setOnClickListener {
                lifecycleScope.launch {
                credentialHelper.signOut()
                    findNavController().navigate(R.id.action_profileFragment_to_userAuthFragment)
            }
        }
    }
}