package com.app.talent360.home.activity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.talent360.R
import com.app.talent360.databinding.ActivityMockResultBinding
import com.app.talent360.home.adapter.MockSolutionAdapter
import com.app.talent360.home.viewmodel.MockSolutionViewModel
import com.app.talent360.util.LoadingDialog
import com.app.talent360.util.Status.*
import com.app.talent360.util.checkTimeUnit
import com.app.talent360.util.showToast

class MockResultActivity : AppCompatActivity() {
    private var _binding: ActivityMockResultBinding? = null
    private val binding get() = _binding!!
    private var _mockSolutionAdapter: MockSolutionAdapter? = null
    private val mockSolutionAdapter get() = _mockSolutionAdapter!!
    private val mockSolutionViewModel by viewModels<MockSolutionViewModel>()
    private val loadingDialog by lazy { LoadingDialog(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMockResultBinding.inflate(layoutInflater)
        _mockSolutionAdapter = MockSolutionAdapter()
        setContentView(binding.root)
        val mockId = intent.extras?.getString("MOCK_ID")!!
        mockSolutionViewModel.fetchMockResult(mockId)

        setupUI()
        setupObserver()
    }

    private fun setupUI() {

        binding.ivPopOut.setOnClickListener {
            finish()
        }

        binding.rvSolution.adapter = mockSolutionAdapter
        binding.rvSolution.layoutManager = LinearLayoutManager(this)
    }

    private fun setupObserver() {
        mockSolutionViewModel.mockResultState.observe(this) { mockResultState ->
            when (mockResultState.status) {
                LOADING -> {
                    loadingDialog.show()
                }
                SUCCESS -> {
                    loadingDialog.dismiss()
                    val mockSolutionState = mockResultState.data
                    if (mockSolutionState != null) {
                        val totalQuestion = mockSolutionState.mockResult.totalQuestion.toFloat()
                        val correctAns = mockSolutionState.mockResult.correctAns.toInt()
                        val progress = (correctAns / totalQuestion) * 100
                        binding.scoreProgressBar.progress = progress.toInt()
                        binding.tvScore.text = getString(
                            R.string.field_score,
                            mockSolutionState.mockResult.correctAns,
                            mockSolutionState.mockResult.totalQuestion
                        )
                        binding.tvIncorrectScore.text = getString(
                            R.string.field_score,
                            mockSolutionState.mockResult.incorrectAns,
                            mockSolutionState.mockResult.totalQuestion
                        )
                        binding.tvUnAttemptedScore.text = getString(
                            R.string.field_score,
                            mockSolutionState.mockResult.unAttempted,
                            mockSolutionState.mockResult.totalQuestion
                        )
                        binding.tvTimeTakenScore.text = checkTimeUnit(mockSolutionState.mockResult.timeTaken)
                        mockSolutionAdapter.setMockQuestions(mockSolutionState.mockQuestions)
                    }
                }
                ERROR -> {
                    loadingDialog.dismiss()
                    val errorMessage = mockResultState.message!!
                    showToast(this, errorMessage)
                }
            }

        }
    }

    override fun onDestroy() {
        _mockSolutionAdapter = null
        _binding = null
        super.onDestroy()
    }
}