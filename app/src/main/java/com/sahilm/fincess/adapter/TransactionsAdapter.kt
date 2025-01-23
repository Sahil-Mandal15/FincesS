package com.sahilm.fincess.adapter

import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sahilm.fincess.R
import com.sahilm.fincess.databinding.TransactionItemBinding
import com.sahilm.fincess.model.Transaction
import com.sahilm.fincess.utils.Constants
import com.sahilm.fincess.viewmodel.FincessViewModel

class TransactionsAdapter(
    private val context: Context,
    private val transactions: List<Transaction>,
    private val viewModel: FincessViewModel
): RecyclerView.Adapter<TransactionsAdapter.TransactionViewHolder>() {

    inner class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding: TransactionItemBinding = TransactionItemBinding.bind(itemView)
    }

//    private val transactionDiffCallback = object : DiffUtil.ItemCallback<Transaction>() {
//        override fun areItemsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
//            return oldItem.id == newItem.id
//        }
//
//        override fun areContentsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
//            return oldItem == newItem
//        }
//
//    }
//    val differ = AsyncListDiffer(this, transactionDiffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        return TransactionViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.transaction_item, parent, false)
        )
    }

    override fun getItemCount(): Int = transactions.size

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = transactions[position]

        holder.binding.tvCategory.text = transaction.category?.categoryName
        holder.binding.tvTransactionDate.text = transaction.date.toString()
        holder.binding.tvAmount.text = transaction.amount.toString()
        holder.binding.tvAccountLabel.text = transaction.account!!.accountName


        val transactioncategory = transaction.category?.let {
            Constants.getCategoryDetails(transaction.category.toString())
        }
        transactioncategory?.let {
            Glide.with(context)
                .load(transactioncategory.categoryImage)
                .into(holder.binding.ivTransactionIcon)


            holder.binding.ivTransactionIcon.backgroundTintList =
                context.getColorStateList(transactioncategory.categoryColor)
        }
        holder.binding.tvAccountLabel.backgroundTintList = context.getColorStateList(Constants.getAccountsColor(transaction.account!!.accountName))

        holder.binding.tvAmount.setTextColor(
            when (transaction.type) {
                Constants.INCOME -> context.getColor(R.color.greenColor)
                Constants.EXPENSE -> context.getColor(R.color.redColor)
                else -> context.getColor(R.color.default_color)
            }
        )

        holder.itemView.setOnClickListener {
            AlertDialog.Builder(context).apply {
                setTitle("Delete Transaction")
                setMessage("Are you sure to delete this transaction?")
                setPositiveButton("yes") { dialogInterface: DialogInterface, i: Int ->
                    viewModel.deleteTransaction(transaction)
                }
                setNegativeButton("No") { dialogInterface: DialogInterface, i: Int ->
                    dialogInterface.dismiss()

                }
                show()
            }
            true
        }
    }


}