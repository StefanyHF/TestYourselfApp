package com.example.testyourself.ui

import android.os.Handler
import android.os.Looper
import com.example.testyourself.services.QuizService
import com.example.testyourself.services.models.Response

class QuizPresenter(
    private val quizFragment: QuizFragment
) {

    lateinit var result: Response
    var currentQuestionIndex: Int = 0
    lateinit var correctAnswer: String
    lateinit var textAlternatives: Array<String?>


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
        quizFragment.setAnswerBackground()
        quizFragment.disableContinueButton()

        Handler(Looper.myLooper()!!).postDelayed({
            if (currentQuestionIndex < result.results.size) {
                currentQuestionIndex++
                quizFragment.setQuestionTxt(result.results[currentQuestionIndex].question)
                quizFragment.setAnswers(result.results[currentQuestionIndex].correct_answer, result.results[currentQuestionIndex].incorrect_answers[0],result.results[currentQuestionIndex].incorrect_answers[1],result.results[currentQuestionIndex].incorrect_answers[2] )
                quizFragment.resetCardsProperties()
                quizFragment.enableContinueButton()
            }
        }, 1000)
    }

}