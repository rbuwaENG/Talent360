package com.app.talent360.home.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.talent360.databinding.FragmentAlertBinding
import com.app.talent360.home.adapter.AlertAdapter
import com.app.talent360.home.viewmodel.AlertViewModel
import com.app.talent360.util.Status.*
import com.app.talent360.util.showToast

class AlertFragment : Fragment() {
    private var _binding : FragmentAlertBinding? = null
    private val binding get() = _binding!!
    private var _alertAdapter : AlertAdapter? = null
    private val alertAdapter get() = _alertAdapter!!
    private val alertViewModel by viewModels<AlertViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAlertBinding.inflate(inflater, container, false)
        _alertAdapter = AlertAdapter()

        setupUI()
        setupObserver()

        return binding.root
    }

    private fun setupUI() {
        binding.apply {
            alertViewModel.fetchNotifications()
            rvNotification.adapter = alertAdapter
            rvNotification.layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun setupObserver() {
        alertViewModel.notificationStatus.observe(viewLifecycleOwner){ notificationState ->
            when(notificationState.status){
                LOADING -> Unit
                SUCCESS -> {
                    val notifications = notificationState.data!!
                    alertAdapter.setData(notifications)
                }
                ERROR -> {
                    val errorMessage = notificationState.message!!
                    showToast(requireContext(), errorMessage)
                }
            }
        }
    }
    override fun onDestroyView() {
        _binding = null
        _alertAdapter = null
        super.onDestroyView()
    }
}