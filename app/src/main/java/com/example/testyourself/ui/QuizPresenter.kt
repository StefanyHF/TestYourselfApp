package com.example.testyourself.ui

import android.os.Handler
import android.os.Looper
import com.example.testyourself.R
import com.example.testyourself.services.QuizService
import com.example.testyourself.services.models.Response

class QuizPresenter(
    private val quizFragment: QuizFragment,
    private val quizService: QuizService
) {
    private lateinit var result: Response
    private var currentQuestionIndex: Int = 0
    private lateinit var correctAnswer: String
    private lateinit var textAlternatives: Array<String?>
    private var selectedIndex: Int = -1

    fun getQuiz() {
        quizService.getQuiz(onSuccess = { response ->
            this.result = response

            quizFragment.setQuestionTxt(result.results[currentQuestionIndex].question)
            correctAnswer = result.results[currentQuestionIndex].correct_answer

            textAlternatives = arrayOf(
                correctAnswer,
                result.results[currentQuestionIndex].incorrect_answers[0],
                result.results[currentQuestionIndex].incorrect_answers[1],
                result.results[currentQuestionIndex].incorrect_answers[2]
            )

            textAlternatives.shuffle()

            quizFragment.setAnswers(
                textAlternatives[0].orEmpty(),
                textAlternatives[1].orEmpty(),
                textAlternatives[2].orEmpty(),
                textAlternatives[3].orEmpty()
            )
            quizFragment.setMaxProgress(result.results.size)
            quizFragment.setProgressText(currentQuestionIndex)

            quizFragment.showViews()
            quizFragment.hideLoading()

        }, onFailure = { message ->
            quizFragment.showError(message)

        })
    }

    fun setBtnContinueClick() {
        quizFragment.setProgressText(currentQuestionIndex)
        setAnswerBackground()
        quizFragment.disableContinueButton()

        Handler(Looper.myLooper()!!).postDelayed({
            if (currentQuestionIndex < result.results.size) {
                currentQuestionIndex++
                quizFragment.setQuestionTxt(result.results[currentQuestionIndex].question)
                quizFragment.setAnswers(
                    result.results[currentQuestionIndex].correct_answer,
                    result.results[currentQuestionIndex].incorrect_answers[0],
                    result.results[currentQuestionIndex].incorrect_answers[1],
                    result.results[currentQuestionIndex].incorrect_answers[2]
                )
                quizFragment.resetCardsProperties()
            }
        }, 1000)
    }

    fun setSelectedOption(selectedIndex: Int) {
        this.selectedIndex = selectedIndex
        quizFragment.enableContinueButton()

        textAlternatives.forEachIndexed { index, _ ->
            if (selectedIndex == index) {
                quizFragment.setCardPropertiesByIndex(
                    selectedIndex,
                    R.color.purple_200,
                    R.color.purple_500,
                    4
                )
            } else {
                quizFragment.setCardPropertiesByIndex(index, R.color.white, R.color.black, 0)
            }
        }
    }

    private fun setAnswerBackground() {
        textAlternatives.forEachIndexed { index, s ->
            if (textAlternatives[index] == correctAnswer) {
                quizFragment.setCardPropertiesByIndex(
                    index,
                    R.color.green,
                    R.color.green,
                    0
                )
            } else if (textAlternatives[selectedIndex] != correctAnswer) {
                quizFragment.setCardPropertiesByIndex(
                    selectedIndex,
                    R.color.red,
                    R.color.red,
                    0
                )
            }
        }
    }
}