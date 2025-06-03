package com.example.myftn

data class ChatGPTResponse(
    val model: String, val choices: List<Choice>
)
