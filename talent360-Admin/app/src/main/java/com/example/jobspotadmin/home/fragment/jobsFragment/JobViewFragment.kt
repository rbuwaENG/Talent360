package com.app.talent360.home.fragment.jobsFragment

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.load
import com.app.talent360.R
import com.app.talent360.databinding.BottomSheetDeleteJobBinding
import com.app.talent360.databinding.FragmentJobViewBinding
import com.app.talent360.home.fragment.jobsFragment.viewmodel.JobsViewModel
import com.app.talent360.model.Job
import com.app.talent360.util.LoadingDialog
import com.app.talent360.util.Status.SUCCESS
import com.app.talent360.util.Status.LOADING
import com.app.talent360.util.Status.ERROR
import com.app.talent360.util.convertToShortString
import com.app.talent360.util.showToast
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.chip.Chip

class JobViewFragment : Fragment() {
    private var _binding: FragmentJobViewBinding? = null
    private val binding get() = _binding!!
    private val args by navArgs<JobViewFragmentArgs>()
    private val jobsViewModel by viewModels<JobsViewModel>()
    private val loadingDialog : LoadingDialog by lazy { LoadingDialog(requireContext()) }
    private val job by lazy { args.job }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentJobViewBinding.inflate(inflater, container, false)

        setupUI()
        setupObserver()

        return binding.root
    }

    private fun setupUI() {
        binding.apply {
            ivPopOut.setOnClickListener {
                findNavController().popBackStack()
            }

            ivDeleteJob.setOnClickListener {
                deleteJobDialog(job = job)
            }

            ivEditJob.setOnClickListener {
                val direction = JobViewFragmentDirections.actionJobViewFragmentToJobEditFragment(job = job)
                findNavController().navigate(direction)
            }

            cvStudentApplied.setOnClickListener {
                val direction = JobViewFragmentDirections.actionJobViewFragmentToStudentJobFragment(jobId = job.uid)
                findNavController().navigate(direction)
            }

            ivCompanyLogo.load(job.imageUrl)

            tvRole.text = job.role
            tvCompanyLocation.text = getString(R.string.field_company_and_location, job.name, job.city)
            tvJobDescription.text = job.description
            tvResponsibility.text = job.responsibility
            tvSalary.text = createSalaryText(job.salary)

            job.skillSet.forEach { job ->
                createSkillSetChip(job)
            }
        }
    }

    private fun setupObserver() {
        jobsViewModel.deleteJobStatus.observe(viewLifecycleOwner){ deleteState ->
            when(deleteState.status){
                LOADING -> {
                    loadingDialog.show()
                }
                SUCCESS -> {
                    loadingDialog.dismiss()
                    val successMessage = deleteState.data!!
                    showToast(requireContext(), successMessage)
                }
                ERROR -> {
                    loadingDialog.dismiss()
                    val errorMessage = deleteState.message!!
                    showToast(requireContext(), errorMessage)
                }
            }
        }
    }

    private fun createSkillSetChip(job: String) {
        val chip = Chip(requireContext())
        chip.text = job
        chip.chipBackgroundColor = ContextCompat.getColorStateList(requireContext(), R.color.chip_background_color)
        chip.setTextColor(requireContext().getColor(R.color.chip_text_color))
        chip.chipCornerRadius = 8f
        binding.requiredSkillSetChipGroup.addView(chip)
    }

    private fun createSalaryText(salary: String): SpannableString {
        val shortSalary = convertToShortString(salary.toLong())
        val salaryText = SpannableString("LKR$shortSalary/year")
        val orangeColor = ContextCompat.getColor(requireActivity(), R.color.on_boarding_span_text_color)
        val greyColor = ContextCompat.getColor(requireActivity(), R.color.grey)
        val salaryColor = ForegroundColorSpan(orangeColor)
        val durationColor = ForegroundColorSpan(greyColor)
        salaryText.setSpan(salaryColor, 0, salaryText.length - 5, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        salaryText.setSpan(durationColor, salaryText.length - 5, salaryText.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        return salaryText
    }

    private fun deleteJobDialog(job: Job) {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val jobDeleteSheetBinding = BottomSheetDeleteJobBinding.inflate(layoutInflater)
        bottomSheetDialog.setContentView(jobDeleteSheetBinding.root)
        jobDeleteSheetBinding.apply {
            btnNo.setOnClickListener {
                bottomSheetDialog.dismiss()
            }
            btnRemoveJob.setOnClickListener {
                jobsViewModel.deleteJob(job)
                bottomSheetDialog.dismiss()
            }
        }
        bottomSheetDialog.show()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}