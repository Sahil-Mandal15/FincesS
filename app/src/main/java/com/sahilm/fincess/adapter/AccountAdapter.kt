package com.sahilm.fincess.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sahilm.fincess.databinding.RowAccountBinding
import com.sahilm.fincess.model.Account

class AccountAdapter(
    private val context: Context,
    private val accountList: List<Account>,
    private val accountsClickListener: AccountsClickListener
) : RecyclerView.Adapter<AccountAdapter.AccountViewHolder>() {

    interface AccountsClickListener {
        fun onAccountSelected(account: Account)
    }

    inner class AccountViewHolder(val binding: RowAccountBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountViewHolder {
        val binding = RowAccountBinding.inflate(LayoutInflater.from(context))
        return AccountViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return accountList.size

    }

    override fun onBindViewHolder(holder: AccountViewHolder, position: Int) {
        val account = accountList[position]
        holder.binding.tvAccountName.text = account.accountName
        holder.itemView.setOnClickListener {
            accountsClickListener.onAccountSelected(account)
        }
    }
}