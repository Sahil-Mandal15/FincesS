package com.sahilm.fincess.view.fragments

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.sahilm.fincess.R
import com.sahilm.fincess.adapter.AccountAdapter
import com.sahilm.fincess.adapter.CategoryAdapter
import com.sahilm.fincess.databinding.FragmentAddTransactionBinding
import com.sahilm.fincess.databinding.ListDialogBinding
import com.sahilm.fincess.model.Account
import com.sahilm.fincess.model.Category
import com.sahilm.fincess.model.Transaction
import com.sahilm.fincess.utils.Constants
import com.sahilm.fincess.utils.Formatter
import com.sahilm.fincess.viewmodel.FincessViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Calendar

@AndroidEntryPoint
class AddTransactionFragment : BottomSheetDialogFragment()  {
    // TODO: Rename and change types of parameters
    private var _binding: FragmentAddTransactionBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FincessViewModel by viewModels()
    private lateinit var transaction: Transaction
    private val calendar: Calendar = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAddTransactionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        transaction = Transaction()

        binding.tvIncomeBtn.setOnClickListener {
            binding.tvIncomeBtn.setBackgroundResource(R.drawable.income_selector)
            binding.tvExpenseBtn.setBackgroundResource(R.drawable.default_selector)
            binding.tvExpenseBtn.setTextColor(requireContext().getColor(R.color.md_theme_onSurfaceVariant))
            binding.tvIncomeBtn.setTextColor(requireContext().getColor(R.color.greenColor))

            transaction.type = Constants.INCOME
        }

        binding.tvExpenseBtn.setOnClickListener {
            binding.tvIncomeBtn.setBackgroundResource(R.drawable.default_selector)
            binding.tvExpenseBtn.setBackgroundResource(R.drawable.expense_selector)
            binding.tvIncomeBtn.setTextColor(requireContext().getColor(R.color.md_theme_onSurfaceVariant))
            binding.tvExpenseBtn.setTextColor(requireContext().getColor(R.color.redColor))

            transaction.type = Constants.EXPENSE
        }

        binding.tifDateSelector.setOnClickListener {
            val calendar = Calendar.getInstance()
            val datePickerDialog = DatePickerDialog(requireContext(), { _, year, month, dayOfMonth ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                val dateToShow = Formatter().formatDate(calendar.time)
                binding.tifDateSelector.text = dateToShow.toEditable()

                transaction.date = calendar.time
                transaction.id = calendar.time.time
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))

            datePickerDialog.show()
        }

        binding.tifCategorySelector.setOnClickListener {
            showCategoryDialog(LayoutInflater.from(requireContext()))
        }

        binding.tifAccountSelector.setOnClickListener {
            showAccountDialog(LayoutInflater.from(requireContext()))
        }

        binding.btnSaveTransaction.setOnClickListener {
            saveTransaction()
        }


    }

    private fun showCategoryDialog(inflater: LayoutInflater) {
        val dialogBinding = ListDialogBinding.inflate(inflater)
        val categoryDialog = AlertDialog.Builder(requireContext()).create()
        categoryDialog.setView(dialogBinding.root)

        val categoryAdapter = CategoryAdapter(requireContext(), Constants.categories, object : CategoryAdapter.CategoryClickListener {
            override fun onCategoryClicked(category: Category) {
               binding.tifCategorySelector.text = category.categoryName.toEditable()
                if (transaction.category == null) {
                    transaction.category = Category()
                }
               transaction.category!!.categoryName = category.categoryName
                categoryDialog.dismiss()
            }
        })

        dialogBinding.rvListDialog.layoutManager = GridLayoutManager(requireContext(), 3)
        dialogBinding.rvListDialog.adapter = categoryAdapter

        categoryDialog.show()
    }

    private fun showAccountDialog(inflater: LayoutInflater) {
        val dialogBinding = ListDialogBinding.inflate(inflater)
        val accountsDialog = AlertDialog.Builder(requireContext()).create()
        accountsDialog.setView(dialogBinding.root)


        val accountsAdapter = AccountAdapter(requireContext(), Constants.accounts, object : AccountAdapter.AccountsClickListener {
            override fun onAccountSelected(account: Account) {
                binding.tifAccountSelector.text = account.accountName.toEditable()
                if (transaction.account == null) {
                    transaction.account = account
                }
                transaction.account!!.accountName = account.accountName
                accountsDialog.dismiss()
            }
        })

        dialogBinding.rvListDialog.layoutManager = GridLayoutManager(requireContext(), 2)
        dialogBinding.rvListDialog.adapter = accountsAdapter

        accountsDialog.show()
    }

    private fun saveTransaction() {
        val amount = binding.tifAmount.text.toString().toDoubleOrNull() ?: 0.0
        val note = binding.tifNote.text.toString()

        println("saveTransaction" + "amount: ${amount}")
        println("saveTransaction" + "note: ${note}")

        transaction.amount = if (transaction.type == Constants.EXPENSE) {
            amount * -1f
        } else {
            amount
        }.toFloat()

        transaction.note = note

        viewLifecycleOwner.lifecycleScope.launch{
            viewModel.addTransaction(transaction)
            println("addTransactionFragment" + " addTransaction is called")
            viewModel.getTransaction(calendar)
        }


        println("saveTransaction" + "amountinTransactionafter: ${transaction.amount}")
        println("saveTransaction" + "noteinTransactionafter: ${transaction.note}")

        dismiss()

    }

    fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)
}