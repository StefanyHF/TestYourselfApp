package com.example.testyourself.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.testyourself.R
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

                activity?.runOnUiThread{
                    firstQuestion?.text = question

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

        alternatives = arrayOf(firstAnswer, secondAnswer, thirdAnswer, fourthAnswer)

        alternatives.forEachIndexed { index, materialCardView ->
            alternatives[index]?.setOnClickListener {
                setSelectedOption(index)
            }
        }

    }

}