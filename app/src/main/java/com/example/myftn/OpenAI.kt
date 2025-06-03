package com.example.myftn
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
interface OpenAI {
    @Headers("Content-Type: application/json")
    @POST("chat/completions")
    fun getChatGPTResponse(@Body requestBody: ChatGPTRequest): Call<ChatGPTResponse>

}