package com.sahilm.fincess.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.sahilm.fincess.R
import com.sahilm.fincess.databinding.FragmentLandingScreenBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LandingScreenFragment : Fragment() {

    private var _binding: FragmentLandingScreenBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentLandingScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonGetStarted.setOnClickListener {

            findNavController().navigate(R.id.action_landingScreenFragment_to_userAuthFragment)

        }
    }
}