package com.app.talent360.home.adapter

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.app.talent360.home.fragments.QuestionViewFragment
import com.app.talent360.model.MockQuestion

class MockQuestionPageAdapter(
    fragmentManager: FragmentManager,
    private val questions: List<MockQuestion>,
    lifecycle: Lifecycle
) : FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int {
        return questions.size
    }

    override fun createFragment(position: Int): Fragment {
        val bundle = Bundle()
        bundle.putParcelable("QUESTION", questions[position])
        bundle.putInt("QUESTION_ID", position)
        val questionViewFragment = QuestionViewFragment()
        questionViewFragment.arguments = bundle
        return questionViewFragment
    }



}