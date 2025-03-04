package com.app.talent360.home.fragment.studentFragment

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.talent360.databinding.BottomSheetDeleteStudentBinding
import com.app.talent360.databinding.FragmentStudentBinding
import com.app.talent360.home.fragment.studentFragment.adapter.StudentAdapter
import com.app.talent360.home.fragment.studentFragment.viewModel.StudentViewModel
import com.app.talent360.model.Student
import com.app.talent360.util.Constants.Companion.RESUME_PATH
import com.app.talent360.util.LoadingDialog
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await


class StudentFragment : Fragment() {
    private var _binding: FragmentStudentBinding? = null
    private val binding get() = _binding!!
    private var _studentAdapter: StudentAdapter? = null
    private val studentAdapter get() = _studentAdapter!!
    private val studentViewModel by viewModels<StudentViewModel>()
    private val students: MutableList<Student> = mutableListOf()
    private val loadingDialog by lazy { LoadingDialog(requireContext()) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStudentBinding.inflate(inflater, container, false)
        _studentAdapter = StudentAdapter(this@StudentFragment)

        setupUI()
        setupObserver()

        return binding.root
    }

    private fun setupUI() {
        studentViewModel.fetchStudents()
        with(binding) {
            ivPopOut.setOnClickListener {
                findNavController().popBackStack()
            }

            etSearch.addTextChangedListener { text: Editable? ->
                filterStudents(text)
            }

            rvStudent.adapter = studentAdapter
            rvStudent.layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun filterStudents(text: Editable?) {
        if (!text.isNullOrEmpty()) {
            val filteredStudent = students.filter { student ->
                val username = student.details?.username?.lowercase()!!
                val inputText = text.toString().lowercase()
                username.contains(inputText)
            }
            studentAdapter.setData(newStudents = filteredStudent)
        } else {
            studentAdapter.setData(newStudents = students)
        }
    }

    private fun setupObserver() {
        studentViewModel.students.observe(viewLifecycleOwner) { studentList ->
            studentAdapter.setData(studentList)
            students.clear()
            students.addAll(studentList)
        }
    }

    fun navigateToStudentView(student: Student) {
        lifecycleScope.launchWhenResumed {
            loadingDialog.show()
            val resumeRef = FirebaseStorage.getInstance().reference.child(RESUME_PATH)
                .child(student.uid.toString())
            val metadata = resumeRef.metadata.await()
            val fileName = metadata.getCustomMetadata("fileName") ?: ""
            val fileMetadata = metadata.getCustomMetadata("fileMetaData") ?: ""
            val direction = StudentFragmentDirections.actionStudentFragmentToStudentViewFragment(
                student = student,
                fileName = fileName,
                fileMetaData = fileMetadata
            )
            findNavController().navigate(direction)
            loadingDialog.dismiss()
        }
    }

    fun deleteStudent(student: Student) {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val studentDeleteSheetBinding = BottomSheetDeleteStudentBinding.inflate(layoutInflater)
        bottomSheetDialog.setContentView(studentDeleteSheetBinding.root)
        with(studentDeleteSheetBinding) {
            btnNo.setOnClickListener {
                bottomSheetDialog.dismiss()
            }
            btnRemoveStudent.setOnClickListener {
                studentViewModel.deleteStudent(student)
                bottomSheetDialog.dismiss()
            }
        }
        bottomSheetDialog.show()
    }

    override fun onDestroyView() {
        _binding = null
        _studentAdapter = null
        super.onDestroyView()
    }

}