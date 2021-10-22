package com.example.testyourself.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.testyourself.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textview.MaterialTextView
import com.google.gson.Gson
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

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
    private var currentQuestionIndex: Int = 0

    private lateinit var incorrectAnswers: List<String>
    private var txtCorrectAnswer: String? = null
    private var btnContinue: MaterialButton? = null
    private var txtProgress: MaterialTextView? = null

    private lateinit var result: Response
    private lateinit var question: String
    private lateinit var progress: com.google.android.material.progressindicator.LinearProgressIndicator


    private fun setCardTexts() {
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
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
                    result = Gson().fromJson(response.toString(), Response::class.java)
                    question = result.results[currentQuestionIndex].question
                    txtCorrectAnswer = result.results[currentQuestionIndex].correct_answer
                    incorrectAnswers = result.results[currentQuestionIndex].incorrect_answers

                    activity?.runOnUiThread {
                        setCardTexts()
                        setProgress(currentQuestionIndex)
                    }
                } catch (ex: Exception) {
                    Toast.makeText(requireContext(), ex.localizedMessage, Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            val json = savedInstanceState.getString("firstState")
            val gson = Gson()
            val objt = gson.fromJson(json, Response::class.java)
            result = objt
            question = result.results[currentQuestionIndex].question
            txtCorrectAnswer = result.results[currentQuestionIndex].correct_answer
            incorrectAnswers = result.results[currentQuestionIndex].incorrect_answers
            currentQuestionIndex = savedInstanceState.getInt("progressBar")
            selectedIndex = savedInstanceState.getInt("selectedIndex")
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val gson = Gson()
        val json = gson.toJson(result)
        outState.putString("firstState", json)
        outState.putInt("selectedIndex", selectedIndex)
        outState.putInt("progressBar", currentQuestionIndex)
    }

    private fun setCardProperties(
        card: MaterialCardView?,
        background: Int,
        stroke: Int,
        strokeWidth: Int
    ) {
        card?.setBackgroundColor(ContextCompat.getColor(requireContext(), background))
        card?.strokeColor = (ContextCompat.getColor(requireContext(), stroke))
        card?.strokeWidth = strokeWidth
    }

    private fun resetCardsProperties() {
        setCardProperties(firstAnswer, R.color.white, R.color.white, 0)
        setCardProperties(secondAnswer, R.color.white, R.color.white, 0)
        setCardProperties(thirdAnswer, R.color.white, R.color.white, 0)
        setCardProperties(fourthAnswer, R.color.white, R.color.white, 0)
    }

    private fun setProgress(questionIndex: Int) {
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
            textAlternatives.forEachIndexed { index, s ->
                if (textAlternatives[index] == txtCorrectAnswer) {
                    setCardProperties(alternatives[index], R.color.green, R.color.green, 0)
                } else if (textAlternatives[selectedIndex] != txtCorrectAnswer) {
                    setCardProperties(alternatives[selectedIndex], R.color.red, R.color.red, 0)
                }
            }

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

            }, 1000)
        }
        if (savedInstanceState != null) {
            setCardTexts()
            setProgress(currentQuestionIndex)
            setSelectedOption(selectedIndex)
            setProgress(currentQuestionIndex)
        }
    }
}



