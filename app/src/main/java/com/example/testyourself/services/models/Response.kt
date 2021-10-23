package com.example.testyourself.services.models

data class Response(
    val response_code: Int,
    val results: List<Result>
)

data class Result(
    val category: String,
    val type: String,
    val difficulty: String,
    val question: String,
    val correct_answer: String,
    val incorrect_answers: List<String>
)
