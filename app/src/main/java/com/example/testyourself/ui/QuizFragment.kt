package com.example.testyourself.ui

import android.os.Bundle
import android.util.Log
import android.view.View
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

    private var firstAnswer: MaterialCardView? = null
    private var secondAnswer: MaterialCardView? = null
    private var thirdAnswer: MaterialCardView? = null
    private var fourthAnswer: MaterialCardView? = null
    private var firstQuestion: MaterialTextView? = null
    private lateinit var alternatives: Array<MaterialCardView?>
    private var selectedIndex: Int = -1

    private var txtFirstAnswser: MaterialTextView? = null
    private var txtSecondAnswser: MaterialTextView? = null
    private var txtThirdAnswser: MaterialTextView? = null
    private var txtFourthAnswser: MaterialTextView? = null
    private lateinit var textAlternatives: Array<String?>

    private lateinit var incorrectAnswers: List<String>
    private var txtCorrectAnswer: String? = null

    private var btnContinue: MaterialButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
                val result = Gson().fromJson(response.toString(), Response::class.java)

                val question = result.results.first().question
                txtCorrectAnswer = result.results.first().correct_answer
                incorrectAnswers = result.results.first().incorrect_answers

                activity?.runOnUiThread {
                    firstQuestion?.text = question
                    textAlternatives = arrayOf(
                        txtCorrectAnswer,
                        incorrectAnswers[0],
                        incorrectAnswers[1],
                        incorrectAnswers[2]
                    )
                    textAlternatives.shuffle()
                    txtFirstAnswser?.text = textAlternatives[0]
                    txtSecondAnswser?.text = textAlternatives[1]
                    txtThirdAnswser?.text = textAlternatives[2]
                    txtFourthAnswser?.text = textAlternatives[3]
                }
            } catch (ex: Exception) {

            }
        }

    }

    private fun setSelectedOption(selectedIndex: Int) {
        alternatives.forEachIndexed { index, _ ->
            if (selectedIndex == index) {
                alternatives[selectedIndex]?.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.purple_200
                    )
                )
                alternatives[selectedIndex]?.strokeColor =
                    (ContextCompat.getColor(requireContext(), R.color.purple_500))
                alternatives[selectedIndex]?.strokeWidth = 4


            } else {
                alternatives[index]?.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.white
                    )
                )
                alternatives[index]?.strokeColor =
                    (ContextCompat.getColor(requireContext(), R.color.white))
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
        firstQuestion = view.findViewById(R.id.txt_question)
        txtFirstAnswser = view.findViewById(R.id.txt_first_answer)
        txtSecondAnswser = view.findViewById(R.id.txt_second_answer)
        txtThirdAnswser = view.findViewById(R.id.txt_third_answer)
        txtFourthAnswser = view.findViewById(R.id.txt_fourth_answer)
        btnContinue = view.findViewById(R.id.btn_continue)
        btnContinue?.isEnabled = selectedIndex > 0

        alternatives = arrayOf(firstAnswer, secondAnswer, thirdAnswer, fourthAnswer)


        alternatives.forEachIndexed { index, materialCardView ->
            alternatives[index]?.setOnClickListener {
                btnContinue?.isEnabled = true
                setSelectedOption(index)
                selectedIndex = index
            }
        }
        btnContinue?.setOnClickListener {
            textAlternatives.forEachIndexed { index, s ->
                if (textAlternatives[index] == txtCorrectAnswer) {
                    alternatives[index]?.setBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.green
                        )
                    )
                } else if(textAlternatives[selectedIndex] != txtCorrectAnswer) {
                    alternatives[selectedIndex]?.setBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.red
                        )
                    )
                }
            }
        }
    }
}

