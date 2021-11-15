package com.example.testyourself.services

import android.util.Log
import android.widget.Toast
import com.example.testyourself.ui.QuizFragment
import com.example.testyourself.services.models.Response
import com.example.testyourself.ui.QuizPresenter
import com.google.gson.Gson
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

class QuizService() {

    fun getQuiz(
        onSuccess: (Response) -> Unit,
        onFailure: (String) -> Unit
    ) {
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
                onSuccess(result)
            } catch (ex: Exception) {
                onFailure(ex.localizedMessage.orEmpty())
            }
        }
    }
}