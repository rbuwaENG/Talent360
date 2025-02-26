package com.app.talent360.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.talent360.R
import com.app.talent360.databinding.MockSolutionCardLayoutBinding
import com.app.talent360.model.MockQuestion

class MockSolutionAdapter : RecyclerView.Adapter<MockSolutionAdapter.MockSolutionViewHolder>() {

    private val mockQuestion: MutableList<MockQuestion> = mutableListOf()

    inner class MockSolutionViewHolder(private val binding: MockSolutionCardLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(mockQuestion: MockQuestion, position: Int) {
            binding.apply {
                tvQuestionCount.text = "Question ${position + 1}"
                tvQuestion.text = mockQuestion.question
                tvOptionOneAnswer.text = mockQuestion.options[0]
                tvOptionTwoAnswer.text = mockQuestion.options[1]
                tvOptionThreeAnswer.text = mockQuestion.options[2]
                tvOptionFourAnswer.text = mockQuestion.options[3]
                tvSolution.text = mockQuestion.feedback
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MockSolutionViewHolder {
        val view = MockSolutionCardLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MockSolutionViewHolder(view)
    }

    override fun onBindViewHolder(holder: MockSolutionViewHolder, position: Int) {
        holder.bind(mockQuestion[position], position)
    }

    override fun getItemCount(): Int = mockQuestion.size

    fun setMockQuestions(newMockQuestion: List<MockQuestion>) {
        mockQuestion.clear()
        mockQuestion.addAll(newMockQuestion)
        notifyDataSetChanged()
    }
}