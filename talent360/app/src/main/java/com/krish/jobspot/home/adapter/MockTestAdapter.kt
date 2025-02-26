package com.app.talent360.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.talent360.databinding.MockTestCardLayoutBinding
import com.app.talent360.home.fragments.MockTestFragment
import com.app.talent360.model.MockTestState

class MockTestAdapter(private val listener: MockTestFragment) :
    RecyclerView.Adapter<MockTestAdapter.MockTestViewHolder>() {

    private val mockTestState: MutableList<MockTestState> = mutableListOf()

    inner class MockTestViewHolder(
        private val binding: MockTestCardLayoutBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(mockTestState: MockTestState){
            binding.apply {
                tvMockTestName.text = mockTestState.quizName
                if (mockTestState.hasAttempted) {
                    tvMockTestAttemptStatus.text = "Already Attempted."
                } else {
                    tvMockTestAttemptStatus.text = "Not Attempted."
                }
                cvMockTest.setOnClickListener {
                    cvMockTest.isEnabled = false
                    if (mockTestState.hasAttempted){
                        listener.navigateToMockResult(mockTestState.quizUid)
                    } else {
                        listener.navigateToMockQuestion(mockTestState.quizUid)
                    }
                    cvMockTest.isEnabled = true
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MockTestViewHolder {
        return MockTestViewHolder(
            MockTestCardLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MockTestViewHolder, position: Int) {
        holder.bind(mockTestState[position])
    }

    override fun getItemCount(): Int = mockTestState.size

    fun setMockState(newQuizDetail: List<MockTestState>) {
        mockTestState.clear()
        mockTestState.addAll(newQuizDetail)
        notifyDataSetChanged()
    }

}