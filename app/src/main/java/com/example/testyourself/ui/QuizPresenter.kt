package com.example.testyourself.ui

import com.example.testyourself.services.QuizService
import com.example.testyourself.services.models.Response

class QuizPresenter(
    private val quizFragment: QuizFragment
) {

    lateinit var result: Response
    var currentQuestionIndex: Int = 0
    private lateinit var textAlternatives: Array<String?>

    private val quizService = QuizService(this)

    fun getQuiz() {
        quizService.getQuiz()
    }

    fun onSuccess(response: Response) {
        this.result = response

        quizFragment.setQuestionTxt(result.results[currentQuestionIndex].question)

        textAlternatives = arrayOf(
            result.results[currentQuestionIndex].correct_answer,
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

    fun onContinueClicked() {

    }
}