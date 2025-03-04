package com.app.talent360.auth.fragments

import android.content.Intent
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.app.talent360.R
import com.app.talent360.auth.viewmodel.AuthViewModel
import com.app.talent360.databinding.FragmentLoginBinding
import com.app.talent360.home.HomeActivity
import com.app.talent360.util.*
import com.app.talent360.util.Constants.Companion.ROLE_TYPE_ADMIN
import com.app.talent360.util.Constants.Companion.ROLE_TYPE_TPO
import com.app.talent360.util.Status.*

private const val TAG = "LoginFragmentTAG"

class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val args by navArgs<LoginFragmentArgs>()
    private val authViewModel by viewModels<AuthViewModel>()
    private val loadingDialog: LoadingDialog by lazy { LoadingDialog(requireContext()) }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentLoginBinding.inflate(inflater, container, false)

        setupUI()
        setupObserver()

        return binding.root
    }

    private fun setupUI() {
        with(binding) {
            tvSignup.text = createSignupText()
            tvSignup.setOnClickListener {
                navigateToSignup(roleType = args.roleType)
            }
            tvForgetPassword.setOnClickListener {
                findNavController().navigate(R.id.action_loginFragment_to_forgetPassFragment)
            }
            etEmailContainer.addTextWatcher()
            etPasswordContainer.addTextWatcher()
            btnLogin.setOnClickListener {
                val email = etEmail.getInputValue()
                val password = etPassword.getInputValue()
                if (detailVerification(email, password)) {
                    authViewModel.login(email, password)
                    clearField()
                }
            }
        }
    }

    private fun setupObserver() {
        authViewModel.loginStatus.observe(viewLifecycleOwner) { resource ->
            when (resource.status) {
                LOADING -> {
                    loadingDialog.show()
                }
                SUCCESS -> {
                    loadingDialog.dismiss()
                    val user = resource.data!!
                    if (user.roleType == args.roleType) {
                        if (user.userInfoExist.not() && user.roleType == ROLE_TYPE_TPO) {
                            navigateToUserDetail(username = user.username, email = user.email)
                        } else if (user.userInfoExist && user.roleType == ROLE_TYPE_TPO) {
                            navigateToHomeActivity(roleType = user.roleType,)
                        } else if (user.roleType == ROLE_TYPE_ADMIN) {
                            navigateToHomeActivity(roleType = user.roleType)
                        }
                    } else {
                        showToast(requireContext(), "Account doesn't exist")
                    }
                }
                ERROR -> {
                    loadingDialog.dismiss()
                    val errorMessage = resource.message!!
                    showToast(requireContext(), errorMessage)
                }
            }
        }
    }

    private fun createSignupText(): SpannableString {
        val signupText = SpannableString(getString(R.string.sign_up_prompt))
        val color = ContextCompat.getColor(requireActivity(), R.color.on_boarding_span_text_color)
        val signupColor = ForegroundColorSpan(color)
        signupText.setSpan(UnderlineSpan(), 31, signupText.length, 0)
        signupText.setSpan(signupColor, 31, signupText.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        return signupText
    }

    private fun clearField() {
        binding.etEmail.clearText()
        binding.etPassword.clearText()
    }

    private fun navigateToHomeActivity(roleType: String) {
        val homeActivity = Intent(requireContext(), HomeActivity::class.java)
        homeActivity.putExtra("ROLE_TYPE", roleType)
        requireActivity().startActivity(homeActivity)
        requireActivity().finish()
    }

    private fun navigateToUserDetail(username: String, email: String) {
        val direction = LoginFragmentDirections.actionLoginFragmentToUserDetailFragment(username, email)
        findNavController().navigate(direction)
    }

    private fun navigateToSignup(roleType: String) {
        val direction = LoginFragmentDirections.actionLoginFragmentToSignupFragment(roleType = roleType)
        findNavController().navigate(direction)
    }

    private fun detailVerification(
        email: String,
        password: String
    ): Boolean {
        with(binding) {
            val (isEmailValid, emailError) = InputValidation.isEmailValid(email)
            if (isEmailValid.not()) {
                etEmailContainer.error = emailError
                return isEmailValid
            }

            val (isPasswordValid, passwordError) = InputValidation.isPasswordValid(password)
            if (isPasswordValid.not()) {
                etPasswordContainer.error = passwordError
                return isPasswordValid
            }
            return true
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}