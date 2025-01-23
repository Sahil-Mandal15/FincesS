package com.sahilm.fincess.view.fragments

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.MPPointF
import com.google.android.material.tabs.TabLayout
import com.sahilm.fincess.R
import com.sahilm.fincess.databinding.FragmentStatsBinding
import com.sahilm.fincess.utils.Constants
import com.sahilm.fincess.utils.Formatter
import com.sahilm.fincess.viewmodel.FincessViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Calendar

@AndroidEntryPoint
class StatsFragment : Fragment() {

    private var _binding: FragmentStatsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FincessViewModel by viewModels()
    private lateinit var calendar: Calendar
    private lateinit var pieChart: PieChart

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        _binding = FragmentStatsBinding.inflate(inflater, container, false)
        return(binding.root)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        calendar = Calendar.getInstance()
        updateDate()

        binding.tvIncomeBtn.setOnClickListener {
            binding.tvIncomeBtn.background = requireContext().getDrawable(R.drawable.income_selector)
            binding.tvExpenseBtn.background = requireContext().getDrawable(R.drawable.default_selector)
            binding.tvExpenseBtn.setTextColor(requireContext().getColor(R.color.default_color))
            binding.tvIncomeBtn.setTextColor(requireContext().getColor(R.color.greenColor))

            Constants.SELECTED_STATS_TYPE = Constants.INCOME
            updateDate()
        }
        binding.tvExpenseBtn.setOnClickListener {
            binding.tvIncomeBtn.background = requireContext().getDrawable(R.drawable.default_selector)
            binding.tvExpenseBtn.background = requireContext().getDrawable(R.drawable.expense_selector)
            binding.tvExpenseBtn.setTextColor(requireContext().getColor(R.color.redColor))
            binding.tvIncomeBtn.setTextColor(requireContext().getColor(R.color.default_color))

            Constants.SELECTED_STATS_TYPE = Constants.EXPENSE
            updateDate()
        }

        binding.btnNextDate.setOnClickListener {
            if (Constants.SELECTED_TAB_STATUS == Constants.DAILY) {
                calendar.add(Calendar.DATE, 1)
            } else if (Constants.SELECTED_TAB_STATUS == Constants.MONTHLY) {
                calendar.add(Calendar.MONTH, 1)
            }
            updateDate()
        }
        binding.btnPrevDate.setOnClickListener {
            if (Constants.SELECTED_TAB_STATUS == Constants.DAILY) {
                calendar.add(Calendar.DATE, -1)
            } else if (Constants.SELECTED_TAB_STATUS == Constants.MONTHLY) {
                calendar.add(Calendar.MONTH, -1)
            }
            updateDate()
        }

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab!!.text) {
                    "Monthly" -> {
                        Constants.SELECTED_TAB_STATUS = 1
                        updateDate()
                    }
                    "Daily" -> {
                        Constants.SELECTED_TAB_STATUS = 0
                        updateDate()
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}

        })

        pieChart = binding.pieChart

        viewLifecycleOwner.lifecycleScope.launch {
                viewModel.getPieEntries().collect { pieEntries ->
                    setupPieChart(pieEntries)

            }
        }
    }

    private fun setupPieChart(pieEntries: List<PieEntry>) {
        pieChart.clear()

        pieChart.setUsePercentValues(true)

        pieChart.description.isEnabled = false
        pieChart.setExtraOffsets(5f, 10f, 5f, 5f)

        pieChart.dragDecelerationFrictionCoef = 0.95f
        pieChart.isDrawHoleEnabled = true
        pieChart.setHoleColor(Color.WHITE)

        pieChart.setTransparentCircleAlpha(110)
        pieChart.holeRadius = 100f
        pieChart.transparentCircleRadius = 61f
        pieChart.setDrawCenterText(true)
        pieChart.rotationAngle = 0f

        pieChart.legend.isEnabled = false
        pieChart.setEntryLabelColor(Color.WHITE)
        pieChart.setEntryLabelTextSize(12f)



        val dataSet = PieDataSet(pieEntries, "Categories Description")
        dataSet.setDrawIcons(false)
        dataSet.sliceSpace = 3f
        dataSet.iconsOffset = MPPointF(0f, 40f)
        dataSet.selectionShift = 5f

        val colors: ArrayList<Int> = ArrayList()
        colors.add(resources.getColor(R.color.category1))
        colors.add(resources.getColor(R.color.category2))
        colors.add(resources.getColor(R.color.category3))
        colors.add(resources.getColor(R.color.category4))
        colors.add(resources.getColor(R.color.category5))
        colors.add(resources.getColor(R.color.category6))

        dataSet.colors = colors

        val data = PieData(dataSet)

        data.setValueFormatter(PercentFormatter())
        data.setValueTextSize(15f)

        dataSet.valueTypeface = Typeface.DEFAULT_BOLD
        data.setValueTextColor(Color.WHITE)
        pieChart.data = data
        pieChart.highlightValues(null)
        pieChart.invalidate()

    }

    private fun updateDate () {
        if (Constants.SELECTED_TAB_STATUS == Constants.DAILY) {
            binding.tvCurrentDate.text = Formatter().formatDate(calendar.time)
        } else if (Constants.SELECTED_TAB_STATUS == Constants.MONTHLY) {
            binding.tvCurrentDate.text = Formatter().formatDate(calendar.time).substring(3)
        }
        viewModel.getTransaction(calendar, Constants.SELECTED_STATS_TYPE)
    }
}