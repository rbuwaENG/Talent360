package com.app.talent360.home.fragment.quizFragment.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.talent360.R
import com.app.talent360.databinding.MockTestCardLayoutBinding
import com.app.talent360.home.fragment.quizFragment.QuizFragment
import com.app.talent360.home.fragment.quizFragment.adapter.diff_util.MockDetailDiffCallBack
import com.app.talent360.model.MockDetail

class MockTestAdapter(
    private val listener: QuizFragment
) : RecyclerView.Adapter<MockTestAdapter.MockTestViewHolder>() {

    private val mockDetail: MutableList<MockDetail> = mutableListOf()

    inner class MockTestViewHolder(
        private val binding: MockTestCardLayoutBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(mockDetail: MockDetail) {
            binding.tvQuizName.text = mockDetail.mockName
            binding.tvQuizStudentCount.text = itemView.context.getString(
                R.string.field_quiz_student_count,
                mockDetail.studentIds.size
            )
            binding.ivDeleteQuiz.setOnClickListener {
                listener.showDeleteDialog(mockDetail = mockDetail)
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
        holder.bind(mockDetail[position])
    }

    override fun getItemCount(): Int = mockDetail.size

    fun setQuizData(newMockDetail: List<MockDetail>) {
        val diffCallBack = MockDetailDiffCallBack(oldList = mockDetail, newList = newMockDetail)
        val diffMockDetail = DiffUtil.calculateDiff(diffCallBack)
        mockDetail.clear()
        mockDetail.addAll(newMockDetail)
        diffMockDetail.dispatchUpdatesTo(this)
    }
}