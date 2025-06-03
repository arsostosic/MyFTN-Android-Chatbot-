package com.example.myftn
import junit.framework.TestCase.assertEquals
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
class NetworkTest {
    private lateinit var mockWebServer: MockWebServer
    private lateinit var openAI: OpenAI

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        openAI = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OpenAI::class.java)
    }

    @After
    fun teardown() {
        mockWebServer.shutdown()
    }

    @Test
    fun testGetChatGPTResponse() {
        // Arrange
        val mockResponse = MockResponse()
            .setBody("{\"model\":\"gpt-3.5-turbo\",\"choices\":[{\"message\":{\"role\":\"system\",\"content\":\"You are a helpful assistant.\"}}]}")
            .addHeader("Content-Type", "application/json")
        mockWebServer.enqueue(mockResponse)

        // Act
        val call = openAI.getChatGPTResponse(ChatGPTRequest(model = "gpt-3.5-turbo", messages = listOf()))
        val response = call.execute()

        // Assert
        assertEquals(200, response.code())
        assertEquals("gpt-3.5-turbo", response.body()?.model)
        assertEquals(1, response.body()?.choices?.size)
    }
}