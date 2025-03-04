package com.app.talent360.home.fragment.jobsFragment

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.app.talent360.R
import com.app.talent360.databinding.FragmentJobDetailOneBinding
import com.app.talent360.home.fragment.jobsFragment.viewmodel.JobsViewModel
import com.app.talent360.model.Job
import com.app.talent360.util.InputValidation
import com.app.talent360.util.addTextWatcher
import com.app.talent360.util.getInputValue
import com.app.talent360.util.showToast
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class JobDetailFragmentOne : Fragment() {
    private var _binding: FragmentJobDetailOneBinding? = null
    private val binding get() = _binding!!
    private val startForProfileImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            handleCapturedImage(result)
        }
    private val jobsViewModel by viewModels<JobsViewModel>()
    private var workType: String = ""
    private var designation : String = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentJobDetailOneBinding.inflate(inflater, container, false)

        setupUI()

        return binding.root
    }

    private fun setupUI() {
        with(binding) {

            if (jobsViewModel.getImageUri() != null) {
                ivCompany.setImageURI(jobsViewModel.getImageUri())
            }

            ivPopOut.setOnClickListener {
                findNavController().popBackStack()
            }

            ivCompany.setOnClickListener {
                startCrop()
            }

            etJobTitleContainer.addTextWatcher()
            etCompanyNameContainer.addTextWatcher()
            etCityNameContainer.addTextWatcher()
            etSalaryContainer.addTextWatcher()
            etJobDescContainer.addTextWatcher()

            workTypeSpinner.dismissWhenNotifiedItemSelected = true
            workTypeSpinner.setOnSpinnerItemSelectedListener<String> { _, _, _, selectedWorkType ->
                workTypeSpinner.error = null
                workType = selectedWorkType
            }

            designationSpinner.dismissWhenNotifiedItemSelected = true
            designationSpinner.setOnSpinnerItemSelectedListener<String>{ _, _, _, selectedDesignation ->
                designationSpinner.error = null
                designation = selectedDesignation
            }

            btnNext.setOnClickListener {
                val jobRole = etJobTitle.getInputValue()
                val companyName = etCompanyName.getInputValue()
                val city = etCityName.getInputValue()
                val salary = etSalary.getInputValue()
                val jobDescription = etJobDesc.getInputValue()
                val imageUrl = jobsViewModel.getImageUri()
                val currentUid = Firebase.auth.currentUser?.uid.toString()

                if (detailVerification(
                        imageUrl,
                        jobRole,
                        companyName,
                        city,
                        salary,
                        designation,
                        workType,
                        jobDescription
                    )
                ) {
                    val job = Job(
                        authorUid = currentUid,
                        imageUrl = imageUrl.toString(),
                        role = jobRole,
                        name = companyName,
                        city = city,
                        salary = salary,
                        designation = designation,
                        workType = workType,
                        description = jobDescription
                    )
                    navigateToDetailFragmentTwo(job = job)
                }
            }
        }
    }

    private fun navigateToDetailFragmentTwo(job: Job) {
        val direction =
            JobDetailFragmentOneDirections.actionJobDetailFragmentOneToJobDetailFragmentTwo(job = job)
        findNavController().navigate(direction)
    }

    private fun startCrop() {
        ImagePicker.with(this)
            .galleryOnly()
            .crop()
            .compress(1024)
            .maxResultSize(300, 300)
            .createIntent { intent ->
                startForProfileImageResult.launch(intent)
            }
    }

    private fun handleCapturedImage(result: ActivityResult) {
        val resultCode = result.resultCode
        val data = result.data

        when (resultCode) {
            Activity.RESULT_OK -> {
                jobsViewModel.setImageUri(imageUri = data?.data!!)
                binding.ivCompany.setImageURI(jobsViewModel.getImageUri())
            }
            ImagePicker.RESULT_ERROR -> {
                showToast(requireContext(), ImagePicker.getError(data))
            }
            else -> {
                showToast(requireContext(), "Task Cancelled")
            }
        }
    }

    private fun detailVerification(
        imageUrl: Uri?,
        title: String,
        company: String,
        city: String,
        salary: String,
        designation : String,
        workType: String,
        description: String
    ): Boolean {
        with(binding) {
            if (imageUrl == null) {
                showToast(requireContext(), getString(R.string.field_error_image))
                return false
            }

            val (isJobTitleValid, jobTitleError) = InputValidation.isJobTitleValid(title)
            if (isJobTitleValid.not()){
                etJobTitleContainer.error = jobTitleError
                return isJobTitleValid
            }

            val (isCompanyNameValid, companyNameError) = InputValidation.isCompanyNameValid(company)
            if (isCompanyNameValid.not()){
                etCompanyNameContainer.error = companyNameError
                return isCompanyNameValid
            }

            val (isCityValid, cityError) = InputValidation.isCityValid(city)
            if (isCityValid.not()){
                etCityNameContainer.error = cityError
                return isCityValid
            }

            val (isSalaryValid, salaryError) = InputValidation.isSalaryValid(salary)
            if (isSalaryValid.not()){
                etSalaryContainer.error = salaryError
                return isSalaryValid
            }

            if (InputValidation.checkNullity(workType)) {
                workTypeSpinner.error = ""
                return false
            }

            val (isDescriptionValid, descriptionError) = InputValidation.isJobDescriptionValid(description)
            if (isDescriptionValid.not()) {
                etJobDescContainer.error = descriptionError
                return isDescriptionValid
            }

            if (InputValidation.checkNullity(designation)) {
                designationSpinner.error = "Enter valid designation"
                return false
            }

            return true
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

}