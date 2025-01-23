package com.sahilm.fincess.view

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.sahilm.fincess.R
import com.sahilm.fincess.auth.CredentialHelper
import com.sahilm.fincess.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!
    private lateinit var credentialHelper: CredentialHelper
    private lateinit var navController: NavController


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHostFragment.navController

        credentialHelper = CredentialHelper(this)

        lifecycleScope.launch {
                checkLoginState()
            }
        enableEdgeToEdge()

        navController.addOnDestinationChangedListener { _, currentDestination, _ ->
            checkToDisplayBottomNavBar(currentDestination.id)
        }

    }

    private fun checkToDisplayBottomNavBar(currentDestinationId: Int) {
            when (currentDestinationId) {
                R.id.landingScreenFragment, R.id.userAuthFragment -> {
                    binding.bottomNavView.visibility = View.GONE
                }
                else -> {
                    binding.bottomNavView.visibility = View.VISIBLE
                }
        }
        println("bottomnavbar check done")
    }

    private suspend fun checkLoginState() {
        try {
            val loginState = credentialHelper.loginState.first()
            println("MainActivityResults : " + "LoginState : " + loginState.isLoggedIn)

            val startDestination = if (loginState.isLoggedIn) {
                println("MainActivityResults : " + "opening HomeScreenFragment")
                R.id.homeScreenFragment
            } else {
                println("MainActivityResults : " + "opening LandingScreenFragment")
                R.id.landingScreenFragment
            }
            println("MainActivityResults : " + "StartDestination : " + startDestination.toString())

            val navGraph = navController.navInflater.inflate(R.navigation.nav_graph)
            navGraph.setStartDestination(startDestination)
            navController.graph = navGraph

        } catch (e: Exception) {
            Log.d("MainActivityResults", "Error checking login state: ", e)

            navController.navigate(R.id.landingScreenFragment)
        }
    }
}