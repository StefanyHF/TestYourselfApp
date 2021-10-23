package com.example.testyourself.services

import android.util.Log
import android.widget.Toast
import com.example.testyourself.ui.QuizFragment
import com.example.testyourself.services.models.Response
import com.google.gson.Gson
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

class QuizService(
    private val quizFragment: QuizFragment
) {

    fun getQuiz() {
        thread {
            val url =
                URL("https://opentdb.com/api.php?amount=10&category=9&difficulty=easy&type=multiple")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.setRequestProperty("Content-Type", "application/json; utf-8")
            connection.doOutput = true

            connection.connect()

            try {
                val bufferedReader =
                    BufferedReader(InputStreamReader(connection.inputStream, "UTF-8"))
                val response = StringBuilder()

                var responseLine = bufferedReader.readLine()

                while (responseLine != null) {
                    response.append(responseLine)
                    responseLine = bufferedReader.readLine()
                }
                Log.i("RESPONSE", response.toString())
                quizFragment.backendJson = response.toString()
                quizFragment.result = Gson().fromJson(quizFragment.backendJson, Response::class.java)
                quizFragment.question = quizFragment.result.results[quizFragment.currentQuestionIndex].question
                quizFragment.txtCorrectAnswer = quizFragment.result.results[quizFragment.currentQuestionIndex].correct_answer
                quizFragment.incorrectAnswers = quizFragment.result.results[quizFragment.currentQuestionIndex].incorrect_answers

                quizFragment.activity?.runOnUiThread {
                    quizFragment.setCardTexts()
                    quizFragment.setProgress(quizFragment.currentQuestionIndex)
                }
            } catch (ex: Exception) {
                Toast.makeText(quizFragment.requireContext(), ex.localizedMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }
}