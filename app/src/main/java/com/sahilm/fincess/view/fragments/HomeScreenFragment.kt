package com.sahilm.fincess.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.sahilm.fincess.adapter.TransactionsAdapter
import com.sahilm.fincess.auth.CredentialHelper
import com.sahilm.fincess.databinding.FragmentHomeScreenBinding
import com.sahilm.fincess.utils.Constants
import com.sahilm.fincess.utils.Formatter
import com.sahilm.fincess.viewmodel.FincessViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Calendar

@AndroidEntryPoint
class HomeScreenFragment : Fragment() {

    private var _binding: FragmentHomeScreenBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FincessViewModel by viewModels()
    private lateinit var credentialHelper: CredentialHelper
    private lateinit var calender: Calendar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHomeScreenBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        credentialHelper = CredentialHelper(requireContext())
        calender = Calendar.getInstance()
        updateDate()

//        println("HomeScreenFragment" + "UserName : ${CredentialHelper.USER_NAME}")
//        // add the user name from the data store preference
//        lifecycleScope.launch {
//            credentialHelper.loginState.collect { loginState ->
//                binding.tvUserName.text = loginState.userName.uppercase()
//
//                Glide.with(this@HomeScreenFragment)
//                    .load(loginState.userPhotoUri)
//                    .into(binding.ivUserPfp)
//            }
//        }



        viewModel.getTransaction(calender)

        binding.btnPrevDate.setOnClickListener {
            calender.add(Calendar.DATE, -1)
            updateDate()
            viewModel.getTransaction(calender)
        }
        binding.btnNextDate.setOnClickListener {
            calender.add(Calendar.DATE, 1)
            updateDate()
            viewModel.getTransaction(calender)
        }

        binding.tabLayout.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab!!.text!! == "Monthly") {
                    Constants.SELECTED_TAB = 1
                    updateDate()
                } else if (tab.text!! == "Daily") {
                    Constants.SELECTED_TAB = 0
                    updateDate()
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        binding.fabAddTransaction.setOnClickListener {
            AddTransactionFragment().show(parentFragmentManager, null)
        }

        binding.rvTransactionList.layoutManager = LinearLayoutManager(context)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.transaction.collect { transaction ->
                    println("HomeScreenFragment" + "Transaction: ${transaction}")

                    val transactionAdapter =
                        TransactionsAdapter(requireActivity(), transaction, viewModel)
                    binding.rvTransactionList.adapter = transactionAdapter
                    binding.ivEmptyState.visibility =
                        if (transaction.isNotEmpty()) View.GONE else View.VISIBLE
                }
            }
        }
            viewLifecycleOwner.lifecycleScope.launch {
    viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
        println("HomeScreenFragment" + "transactionSUmmary started")
        launch {
            viewModel.totalIncome.collect { income ->
                binding.tvIncomeLabel.text = income.toString().replace("[", "").replace("]", "")
            }
        }
        launch {
            viewModel.totalExpense.collect { expense ->
                binding.tvExpenseLabel.text = expense.toString().replace("[", "").replace("]","")
            }
        }
        launch {
            viewModel.totalBalance.collect { balance ->
                binding.tvBalanceLabel.text = balance.toString().replace("[", "").replace("]","")
            }
        }
    }
}
        }








    override fun onDestroy() {
        super.onDestroy()
        credentialHelper.clearCredentialManager()
    }

    private fun updateDate() {
        if (Constants.SELECTED_TAB == Constants.DAILY) {
            binding.tvCurrentDate.text = Formatter().formatDate(calender.time)
        } else if (Constants.SELECTED_TAB == Constants.MONTHLY) {
            binding.tvCurrentDate.text = Formatter().formatDate(calender.time).substring(3)
        }
    }
    }


