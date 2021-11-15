package com.example.testyourself

import com.example.testyourself.services.QuizService
import com.example.testyourself.services.models.Response
import com.example.testyourself.services.models.Result
import com.example.testyourself.ui.QuizFragment
import com.example.testyourself.ui.QuizPresenter
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class QuizPresenterTest {

    private val quizService = mock<QuizService>()
    private val quizFragment = mock<QuizFragment>()
    private val quizPresenter = QuizPresenter(quizFragment, quizService)
    private val fakeResponse = Response(
        0, results = listOf(
            Result(
                category = "category",
                type = "type",
                difficulty = "difficulty",
                question = "question",
                correct_answer = "correct_answer",
                incorrect_answers = listOf("answer1", "answer2", "answer3")
            )
        )
    )

    @Test
    fun `when getQuiz is called then the getQuiz from the service should be called`() {
        quizPresenter.getQuiz()

        verify(quizService).getQuiz(any(), any())
    }

    @Test
    fun `when get quiz is called then it should return a list of quiz`() {
        whenever(quizService.getQuiz(any(), any())).thenAnswer {
            val onSuccess = it.arguments[0] as (Response) -> Unit
            onSuccess(fakeResponse)
        }

        quizPresenter.getQuiz()

        verify(quizFragment).setQuestionTxt("question")
        verify(quizFragment).setAnswers("correct_answer","answer1", "answer2", "answer3")
        verify(quizFragment).setMaxProgress(1)
        verify(quizFragment).setProgressText(0)
        verify(quizFragment).showViews()
        verify(quizFragment).hideLoading()


    }

}