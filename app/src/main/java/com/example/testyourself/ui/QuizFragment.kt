package com.example.testyourself.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.testyourself.R
import com.google.android.material.card.MaterialCardView

class QuizFragment : Fragment(R.layout.fragment_quiz) {

    private var firstAnswer: MaterialCardView? = null
    private var secondAnswer: MaterialCardView? = null
    private var thirdAnswer: MaterialCardView? = null
    private var fourthAnswer: MaterialCardView? = null

    private lateinit var alternatives: Array <MaterialCardView?>

    private fun setSelectedOption(selectedIndex: Int) {
        alternatives.forEachIndexed { index, _ ->
            if (selectedIndex == index) {
                alternatives[selectedIndex]?.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.purple_200))
                alternatives[selectedIndex]?.strokeColor = (ContextCompat.getColor(requireContext(), R.color.purple_500))
                alternatives[selectedIndex]?.strokeWidth = 4

            }else{
                alternatives[index]?.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
                alternatives[index]?.strokeColor = (ContextCompat.getColor(requireContext(), R.color.white))
                alternatives[index]?.strokeWidth = 0
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firstAnswer = view.findViewById(R.id.first_answer)
        secondAnswer = view.findViewById(R.id.second_answer)
        thirdAnswer = view.findViewById(R.id.third_answer)
        fourthAnswer = view.findViewById(R.id.fourth_answer)

        alternatives = arrayOf(firstAnswer, secondAnswer, thirdAnswer, fourthAnswer)

        alternatives.forEachIndexed { index, materialCardView ->
            alternatives[index]?.setOnClickListener {
                setSelectedOption(index)
            }
        }

    }

}