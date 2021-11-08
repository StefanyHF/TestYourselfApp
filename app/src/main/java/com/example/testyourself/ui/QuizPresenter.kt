package com.example.testyourself.ui

import android.os.Handler
import android.os.Looper
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import com.example.testyourself.R
import com.example.testyourself.services.QuizService
import com.example.testyourself.services.models.Response
import com.google.android.material.card.MaterialCardView

class QuizPresenter(
    private val quizFragment: QuizFragment
) {

    lateinit var result: Response
    var currentQuestionIndex: Int = 0
    lateinit var correctAnswer: String
    lateinit var textAlternatives: Array<String?>
    var selectedIndex: Int = -1



    private val quizService = QuizService(this)

    fun getQuiz() {
        quizService.getQuiz()
    }

    fun onSuccess(response: Response) {
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
    }

    fun onFailure(message: String) {
        quizFragment.showError(message)
    }

    fun setBtnContinueClick() {
        quizFragment.setProgressText(currentQuestionIndex)
        setAnswerBackground()
        quizFragment.disableContinueButton()

        Handler(Looper.myLooper()!!).postDelayed({
            if (currentQuestionIndex < result.results.size) {
                currentQuestionIndex++
                quizFragment.setQuestionTxt(result.results[currentQuestionIndex].question)
                quizFragment.setAnswers(result.results[currentQuestionIndex].correct_answer, result.results[currentQuestionIndex].incorrect_answers[0],result.results[currentQuestionIndex].incorrect_answers[1],result.results[currentQuestionIndex].incorrect_answers[2] )
                resetCardsProperties()
            }
        }, 1000)
    }

    fun setSelectedOption(selectedIndex:Int){
        this.selectedIndex = selectedIndex
        quizFragment.alternatives.forEachIndexed { index, _ ->
            if (selectedIndex == index) {
                quizFragment.setCardProperties(
                    quizFragment.alternatives[selectedIndex],
                    R.color.purple_200,
                    R.color.purple_500,
                    4
                )
                quizFragment.enableContinueButton()
            } else {
               quizFragment.setCardProperties(quizFragment.alternatives[index], R.color.white, R.color.black, 0)
            }
        }
    }

    fun resetCardsProperties() {
        quizFragment.setCardProperties(quizFragment.firstAnswer)
        quizFragment.setCardProperties(quizFragment.secondAnswer)
        quizFragment.setCardProperties(quizFragment.thirdAnswer)
        quizFragment.setCardProperties(quizFragment.fourthAnswer)
    }

    fun setAnswerBackground(){
        textAlternatives.forEachIndexed { index, s ->
            if (textAlternatives[index] == correctAnswer) {
                quizFragment.setCardProperties(quizFragment.alternatives[index], R.color.green, R.color.green, 0)
            } else if (textAlternatives[selectedIndex] != correctAnswer) {
                quizFragment.setCardProperties(quizFragment.alternatives[selectedIndex], R.color.red, R.color.red, 0)
            }
        }
    }

}