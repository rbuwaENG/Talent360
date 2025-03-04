package com.app.talent360.home.fragment.homeFragment

import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import coil.load
import com.app.talent360.R
import com.app.talent360.auth.AuthActivity
import com.app.talent360.databinding.BottomSheetLogoutBinding
import com.app.talent360.databinding.FragmentHomeBinding
import com.app.talent360.home.fragment.homeFragment.viewmodel.HomeViewModel
import com.app.talent360.util.Constants.Companion.ROLE_TYPE_ADMIN
import com.app.talent360.util.LoadingDialog
import com.app.talent360.util.Status.*
import com.app.talent360.util.getGreeting
import com.app.talent360.util.showToast
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

private const val TAG = "HomeFragment"

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val homeViewModel by viewModels<HomeViewModel>()
    private val user: FirebaseUser? by lazy { Firebase.auth.currentUser }
    private val loadingDialog by lazy { LoadingDialog(requireContext()) }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        setupUI()
        setupObserver()

        return binding.root
    }

    private fun setupUI() {
        with(binding)  {
            homeViewModel.fetchCounts()
            val bundle: Bundle? = requireActivity().intent.extras
            if (
                bundle != null
                && user != null
                && bundle.containsKey("ROLE_TYPE")
            ) {
                val roleType = bundle.getString("ROLE_TYPE").toString()
                if (roleType == ROLE_TYPE_ADMIN) {
                    ivProfileImage.visibility = View.GONE
                    ivLogout.visibility = View.VISIBLE
                } else {
                    ivProfileImage.load(user?.photoUrl)
                    cvPlacementOfficer.visibility = View.GONE
                }
                binding.tvWelcomeHeading.text = createGreetingText(user?.displayName!!)
            }

            if (ivLogout.visibility == View.VISIBLE) {
                ivLogout.setOnClickListener {
                    showLogoutBottomSheet()
                }
            }

            ivProfileImage.setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment_to_profileFragment)
            }

            cvJob.setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment_to_jobsFragment)
            }

            cvMockTest.setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment_to_quizFragment)
            }

            cvStudent.setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment_to_studentFragment)
            }

            cvNotification.setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment_to_notificationFragment)
            }

            cvPlacementOfficer.setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment_to_tpoFragment)
            }
        }
    }

    private fun setupObserver() {
        homeViewModel.metaCounts.observe(viewLifecycleOwner) { resource ->
            when (resource.status) {
                LOADING -> {
                    loadingDialog.show()
                }
                SUCCESS -> {
                    val counts = resource.data!!
                    counterAnimation(counts.studentCount, binding.tvStudentCount)
                    counterAnimation(counts.jobCount, binding.tvJobCount)
                    counterAnimation(counts.mockCount, binding.tvMockTestCount)
                    counterAnimation(counts.notificationCount, binding.tvNotificationCount)
                    if (binding.cvPlacementOfficer.visibility == View.VISIBLE){
                        counterAnimation(counts.tpoCount, binding.tvPlacementOfficerCount)
                    }
                    loadingDialog.dismiss()
                }
                ERROR -> {
                    val errorMessage = resource.message!!
                    showToast(requireContext(), errorMessage)
                    loadingDialog.dismiss()
                }
            }
        }
    }

    private fun counterAnimation(count: Int, textView: TextView) {
        if (count == 0) {
            textView.text = "0"
        } else {
            val animator = ValueAnimator.ofInt(0, count)
            animator.duration = 500
            animator.interpolator = LinearInterpolator()
            animator.addUpdateListener {
                val counter = it.animatedValue as Int
                textView.text = counter.toString()
            }
            animator.start()
        }
    }

    private fun showLogoutBottomSheet() {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val logoutSheetBinding = BottomSheetLogoutBinding.inflate(layoutInflater)
        bottomSheetDialog.setContentView(logoutSheetBinding.root)
        logoutSheetBinding.apply {
            btnNo.setOnClickListener {
                bottomSheetDialog.dismiss()
            }
            btnLogout.setOnClickListener {
                bottomSheetDialog.dismiss()
                logout()
            }
        }
        bottomSheetDialog.show()
    }

    private fun logout() {
        Firebase.auth.signOut()
        requireActivity().finishAffinity()
        val loginIntent = Intent(requireContext(), AuthActivity::class.java)
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        startActivity(loginIntent)
    }

    private fun createGreetingText(username: String): SpannableString {
        val greeting = getGreeting()
        val greetingWithUsername = "$greeting\n$username"
        val color = ContextCompat.getColor(requireActivity(), R.color.on_boarding_span_text_color)
        val greetingColor = ForegroundColorSpan(color)
        val greetingText = SpannableString(greetingWithUsername)
        val userNameStart = greetingWithUsername.indexOf(username)
        val userNameEnd = userNameStart + username.length
        greetingText.setSpan(greetingColor, userNameStart, userNameEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        return greetingText
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}