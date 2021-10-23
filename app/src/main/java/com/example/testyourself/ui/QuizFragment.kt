package com.example.testyourself.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.testyourself.R
import com.example.testyourself.services.QuizService
import com.example.testyourself.services.models.Response
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textview.MaterialTextView
import com.google.gson.Gson

class QuizFragment : Fragment(R.layout.fragment_quiz) {

    private lateinit var firstAnswer: MaterialCardView
    private lateinit var secondAnswer: MaterialCardView
    private lateinit var thirdAnswer: MaterialCardView
    private lateinit var fourthAnswer: MaterialCardView
    private lateinit var txtQuestion: MaterialTextView
    private lateinit var alternatives: Array<MaterialCardView?>
    private var selectedIndex: Int = -1

    private lateinit var txtFirstAnswser: MaterialTextView
    private lateinit var txtSecondAnswser: MaterialTextView
    private lateinit var txtThirdAnswser: MaterialTextView
    private lateinit var txtFourthAnswser: MaterialTextView
    private lateinit var textAlternatives: Array<String?>
    var currentQuestionIndex: Int = 0

    lateinit var incorrectAnswers: List<String>
    var txtCorrectAnswer: String? = null
    private var btnContinue: MaterialButton? = null
    private var txtProgress: MaterialTextView? = null

    lateinit var result: Response
    lateinit var question: String
    private lateinit var progress: com.google.android.material.progressindicator.LinearProgressIndicator
    lateinit var backendJson: String

    private val quizService = QuizService(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            quizService.getQuiz()
        } else {
            val json: String? = savedInstanceState.getString(JSON)
            val objt = Gson().fromJson(json, Response::class.java)
            backendJson = json!!
            result = objt
            question = result.results[currentQuestionIndex].question
            txtCorrectAnswer = result.results[currentQuestionIndex].correct_answer
            incorrectAnswers = result.results[currentQuestionIndex].incorrect_answers
            currentQuestionIndex = savedInstanceState.getInt(CURRENT_QUESTION_INDEX_KEY)
            selectedIndex = savedInstanceState.getInt(SELECTED_INDEX_KEY)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(JSON, backendJson)
        outState.putInt(SELECTED_INDEX_KEY, selectedIndex)
        outState.putInt(CURRENT_QUESTION_INDEX_KEY, currentQuestionIndex)
    }

    fun setCardTexts() {
        txtQuestion.text = question
        textAlternatives = arrayOf(
            txtCorrectAnswer,
            incorrectAnswers[0],
            incorrectAnswers[1],
            incorrectAnswers[2]
        )
        textAlternatives.shuffle()
        txtFirstAnswser.text = textAlternatives[0]
        txtSecondAnswser.text = textAlternatives[1]
        txtThirdAnswser.text = textAlternatives[2]
        txtFourthAnswser.text = textAlternatives[3]
    }

    private fun setCardProperties(
        card: MaterialCardView?,
        background: Int = R.color.white,
        stroke: Int = R.color.white,
        strokeWidth: Int = 0
    ) {
        card?.setBackgroundColor(ContextCompat.getColor(requireContext(), background))
        card?.strokeColor = (ContextCompat.getColor(requireContext(), stroke))
        card?.strokeWidth = strokeWidth
    }

    private fun resetCardsProperties() {
        setCardProperties(firstAnswer)
        setCardProperties(secondAnswer)
        setCardProperties(thirdAnswer)
        setCardProperties(fourthAnswer)
    }

    fun setProgress(questionIndex: Int) {
        progress.progress = questionIndex
        progress.max = result.results.size
        txtProgress?.text = "${questionIndex}  / ${result.results.size}"
    }

    private fun setSelectedOption(selectedIndex: Int) {
        alternatives.forEachIndexed { index, _ ->
            if (selectedIndex == index) {
                setCardProperties(
                    alternatives[selectedIndex],
                    R.color.purple_200,
                    R.color.purple_500,
                    4
                )
            } else {
                setCardProperties(alternatives[index], R.color.white, R.color.black, 0)
            }
        }
    }

    private fun setAnswerBackground() {
        textAlternatives.forEachIndexed { index, s ->
            if (textAlternatives[index] == txtCorrectAnswer) {
                setCardProperties(alternatives[index], R.color.green, R.color.green, 0)
            } else if (textAlternatives[selectedIndex] != txtCorrectAnswer) {
                setCardProperties(alternatives[selectedIndex], R.color.red, R.color.red, 0)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firstAnswer = view.findViewById(R.id.first_answer)
        secondAnswer = view.findViewById(R.id.second_answer)
        thirdAnswer = view.findViewById(R.id.third_answer)
        fourthAnswer = view.findViewById(R.id.fourth_answer)
        txtQuestion = view.findViewById(R.id.txt_question)
        txtFirstAnswser = view.findViewById(R.id.txt_first_answer)
        txtSecondAnswser = view.findViewById(R.id.txt_second_answer)
        txtThirdAnswser = view.findViewById(R.id.txt_third_answer)
        txtFourthAnswser = view.findViewById(R.id.txt_fourth_answer)
        btnContinue = view.findViewById(R.id.btn_continue)
        btnContinue?.isEnabled = selectedIndex > 0
        txtProgress = view.findViewById(R.id.txt_progress)
        progress = view.findViewById(R.id.progress)

        alternatives = arrayOf(firstAnswer, secondAnswer, thirdAnswer, fourthAnswer)


        alternatives.forEachIndexed { index, materialCardView ->
            alternatives[index]?.setOnClickListener {
                btnContinue?.isEnabled = true
                setSelectedOption(index)
                selectedIndex = index
            }
        }
        btnContinue?.setOnClickListener {
            setProgress(currentQuestionIndex)
            setAnswerBackground()

            btnContinue?.isEnabled = false

            Handler(Looper.myLooper()!!).postDelayed({
                if (currentQuestionIndex < result.results.size) {
                    question = result.results[currentQuestionIndex].question
                    txtCorrectAnswer = result.results[currentQuestionIndex].correct_answer
                    incorrectAnswers = result.results[currentQuestionIndex].incorrect_answers
                    currentQuestionIndex++
                    setCardTexts()
                    resetCardsProperties()
                    btnContinue?.isEnabled = true
                }

            }, QUESTION_DELAY)
        }
        if (savedInstanceState != null) {
            setCardTexts()
            setProgress(currentQuestionIndex)
            setSelectedOption(selectedIndex)
            setProgress(currentQuestionIndex)
        }
    }

    companion object {
        private const val SELECTED_INDEX_KEY = "selectedIndex"
        private const val CURRENT_QUESTION_INDEX_KEY = "currentQuestionIndex"
        private const val JSON = "json"
        private const val QUESTION_DELAY = 1000L
    }
}



