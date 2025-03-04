package com.app.talent360.auth.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.app.talent360.databinding.FragmentRoleSelectBinding
import com.app.talent360.util.Constants.Companion.ROLE_TYPE_ADMIN
import com.app.talent360.util.Constants.Companion.ROLE_TYPE_TPO


class RoleSelectFragment : Fragment() {
    private var _binding: FragmentRoleSelectBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRoleSelectBinding.inflate(inflater, container, false)

        setupUI()

        return binding.root
    }

    private fun setupUI() {
        with(binding) {
            ivRoleAdmin.setOnClickListener {
                navigateToLogin(roleType = ROLE_TYPE_ADMIN)
            }
            ivRoleTpo.setOnClickListener {
                navigateToLogin(roleType = ROLE_TYPE_TPO)
            }
        }
    }

    private fun navigateToLogin(roleType : String) {
        val direction = RoleSelectFragmentDirections.actionRoleSelectFragmentToLoginFragment(roleType = roleType)
        findNavController().navigate(direction)
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}