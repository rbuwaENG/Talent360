package com.app.talent360.home.fragment.quizFragment

import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.talent360.R
import com.app.talent360.databinding.FragmentQuizBinding
import com.app.talent360.home.fragment.quizFragment.adapter.MockTestAdapter
import com.app.talent360.home.fragment.quizFragment.viewmodel.MockViewModel
import com.app.talent360.model.MockDetail
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton

private const val TAG = "QuizFragment"

class QuizFragment : Fragment() {

    private var _binding: FragmentQuizBinding? = null
    private val binding get() = _binding!!
    private var _mockTestAdapter: MockTestAdapter? = null
    private val mockTestAdapter get() = _mockTestAdapter!!

    private val mockViewModel: MockViewModel by viewModels()
    private val mockDetails: MutableList<MockDetail> by lazy { mutableListOf() }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentQuizBinding.inflate(inflater, container, false)
        _mockTestAdapter = MockTestAdapter(this@QuizFragment)
        setupViews()

        return binding.root
    }

    private fun setupViews() {
        binding.apply {

            mockViewModel.fetchMockTest()

            rvQuiz.adapter = mockTestAdapter
            rvQuiz.layoutManager = LinearLayoutManager(requireContext())

            mockViewModel.mockDetails.observe(viewLifecycleOwner, Observer { mockDetailList ->
                mockDetails.clear()
                mockDetails.addAll(mockDetailList)
                mockTestAdapter.setQuizData(mockDetails)
            })

            ivPopOut.setOnClickListener {
                findNavController().popBackStack()
            }

            etSearch.addTextChangedListener { text: Editable? ->
                filterQuiz(text)

            }

            ivAddQuiz.setOnClickListener {
                findNavController().navigate(R.id.action_quizFragment_to_createQuizFragment)
            }
        }
    }

    private fun filterQuiz(text: Editable?) {
        if (!text.isNullOrEmpty()) {
            val filteredQuizList = mockDetails.filter { quizDetail ->
                val title = quizDetail.mockName.lowercase()
                val inputText = text.toString().lowercase()
                title.contains(inputText)
            }
            mockTestAdapter.setQuizData(newMockDetail = filteredQuizList)
        } else {
            mockTestAdapter.setQuizData(newMockDetail = mockDetails)
        }
    }

    fun showDeleteDialog(mockDetail: MockDetail) {
        val dialog = BottomSheetDialog(requireContext())
        val bottomSheet = layoutInflater.inflate(R.layout.bottom_sheet_delete_quiz, null)
        val btnNot: MaterialButton = bottomSheet.findViewById(R.id.btnNo)
        val btnRemove: MaterialButton = bottomSheet.findViewById(R.id.btnRemoveStudent)
        btnNot.setOnClickListener {
            dialog.dismiss()
        }
        btnRemove.setOnClickListener {
            Log.d(TAG, "Mock Detail : ${mockDetail}")
            mockViewModel.deleteMockTest(mockDetail)
            dialog.dismiss()
        }
        dialog.setContentView(bottomSheet)
        dialog.show()
    }

    override fun onDestroyView() {
        mockDetails.clear()
        _mockTestAdapter = null
        _binding = null
        super.onDestroyView()
    }
}