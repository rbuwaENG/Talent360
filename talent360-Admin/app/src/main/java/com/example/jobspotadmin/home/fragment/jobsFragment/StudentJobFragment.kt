package com.app.talent360.home.fragment.jobsFragment

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.talent360.databinding.FragmentStudentJobBinding
import com.app.talent360.home.fragment.jobsFragment.adapter.EvaluationStudentAdapter
import com.app.talent360.home.fragment.jobsFragment.adapter.PendingStudentAdapter
import com.app.talent360.home.fragment.jobsFragment.viewmodel.StudentJobViewModel
import com.app.talent360.model.JobApplication
import com.app.talent360.model.JobStatus

private const val TAG = "StudentJobFragment"

class StudentJobFragment : Fragment() {
    private var _binding: FragmentStudentJobBinding? = null
    private val binding get() = _binding!!
    private val args by navArgs<StudentJobFragmentArgs>()
    private val studentJobViewModel by viewModels<StudentJobViewModel>()
    private var _pendingStudentAdapter: PendingStudentAdapter? = null
    private val pendingStudentAdapter get() = _pendingStudentAdapter!!
    private var _evaluationStudentAdapter: EvaluationStudentAdapter? = null
    private val evaluationStudentAdapter get() = _evaluationStudentAdapter!!

    private val pendingStudents: MutableList<JobStatus> by lazy { mutableListOf() }
    private val evaluatedStudents: MutableList<JobStatus> by lazy { mutableListOf() }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStudentJobBinding.inflate(layoutInflater)
        _pendingStudentAdapter = PendingStudentAdapter(::setJobStatus)
        _evaluationStudentAdapter = EvaluationStudentAdapter()

        setupUI()
        setupObserver()

        return binding.root
    }

    private fun setupUI() {
        studentJobViewModel.fetchStudents(args.jobId)
        with(binding) {

            ivPopOut.setOnClickListener {
                findNavController().popBackStack()
            }

            rvApplicants.adapter = pendingStudentAdapter
            rvApplicants.layoutManager = LinearLayoutManager(requireContext())
            rvApplicants.hasFixedSize()
            rvApplicants.setItemViewCacheSize(10)


            rvRecorded.adapter = evaluationStudentAdapter
            rvRecorded.layoutManager = LinearLayoutManager(requireContext())
            rvRecorded.hasFixedSize()
            rvRecorded.setItemViewCacheSize(10)

            etSearch.addTextChangedListener { text: Editable? ->
                filterStudents(text)
            }
        }
    }

    private fun setupObserver() {
        studentJobViewModel.pendingApplications.observe(viewLifecycleOwner) { students ->
            pendingStudentAdapter.setPendingStudent(students)
            pendingStudents.clear()
            pendingStudents.addAll(students)
        }

        studentJobViewModel.evaluatedApplications.observe(viewLifecycleOwner) { students ->
            evaluationStudentAdapter.setEvaluatedStudent(students)
            evaluatedStudents.clear()
            evaluatedStudents.addAll(students)
        }
    }

    private fun filterStudents(text: Editable?) {
        if (text.isNullOrEmpty().not()) {
            if (pendingStudents.isNotEmpty()) {
                val filteredPendingStudents = pendingStudents.filter { jobStatus ->
                    val name = jobStatus.student.details?.username?.lowercase() ?: ""
                    val inputText = text.toString().lowercase()
                    name.contains(inputText)
                }
                pendingStudentAdapter.setPendingStudent(filteredPendingStudents)
            }
            if (evaluatedStudents.isNotEmpty()) {
                val filteredEvaluatedStudents = evaluatedStudents.filter { jobStatus ->
                    val name = jobStatus.student.details?.username?.lowercase() ?: ""
                    val inputText = text.toString().lowercase()
                    name.contains(inputText)
                }
                evaluationStudentAdapter.setEvaluatedStudent(filteredEvaluatedStudents)
            }
        } else {
            pendingStudentAdapter.setPendingStudent(pendingStudents)
            evaluationStudentAdapter.setEvaluatedStudent(evaluatedStudents)
        }
    }

    private fun setJobStatus(jobApplication: JobApplication) {
        studentJobViewModel.setSelectionStatus(jobApplication)
    }

    override fun onDestroyView() {
        _pendingStudentAdapter = null
        _evaluationStudentAdapter = null
        _binding = null
        super.onDestroyView()
    }
}


