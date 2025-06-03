package com.example.myftn

data class ChatGPTRequest(
    val model: String, val messages: List<Map<String, String>>

)
