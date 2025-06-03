package com.example.myftn

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var messageInputLayout: TextInputLayout
    private lateinit var messageEditText: TextInputEditText
    private lateinit var chatAdapter: ChatAdapter

    private lateinit var sendSound: MediaPlayer
    private lateinit var receiveSound: MediaPlayer

    private lateinit var typingSound: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sendSound = MediaPlayer.create(this, R.raw.send_sound)
        receiveSound = MediaPlayer.create(this, R.raw.receive_sound)
        typingSound = MediaPlayer.create(this, R.raw.typing_sound)
        chatRecyclerView = findViewById(R.id.chatRecyclerView)
        messageInputLayout = findViewById(R.id.messageInputLayout)
        messageEditText = findViewById(R.id.messageEditText)

        chatAdapter = ChatAdapter(mutableListOf())
        chatRecyclerView.adapter = chatAdapter
        chatRecyclerView.layoutManager = LinearLayoutManager(this)

        messageEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (count > 0) {
                    typingSound.start()
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        messageInputLayout.setEndIconOnClickListener {
            val messageText = messageEditText.text.toString().trim()
            val userAvatar = R.drawable.avatar

            if (messageText.isNotEmpty()) {
                val userMessage = Message("user", messageText, userAvatar)
                chatAdapter.addMessage(userMessage)
                chatRecyclerView.smoothScrollToPosition(chatAdapter.getLastPosition())
                messageEditText.text?.clear()
                sendSound.start() // Play the send sound
                getAnswerFromChatGPT(listOf(userMessage))
            }
        }
    }

    fun getAnswerFromChatGPT(messages: List<Message>) {
        val service = ApiClient.instance.create(OpenAI::class.java)

        val formattedMessages = messages    .map {
            mapOf(
                "role" to it.role,
                "content" to it.content

            )
        }

        val requestBody = ChatGPTRequest("gpt-4o", formattedMessages)

        service.getChatGPTResponse(requestBody).enqueue(object : Callback<ChatGPTResponse> {
            override fun onResponse(call: Call<ChatGPTResponse>, response: Response<ChatGPTResponse>) {
                if (response.isSuccessful) {
                    val gptResponse = response.body()?.choices?.firstOrNull()?.message?.content
                    val assistantAvatar = R.drawable.assistant_avatar
                    if (gptResponse != null) {
                        val assistantMessage = Message("assistant", gptResponse, assistantAvatar)
                        runOnUiThread {
                            chatAdapter.addMessage(assistantMessage)
                            chatRecyclerView.smoothScrollToPosition(chatAdapter.getLastPosition())
                            receiveSound.start() // Play the receive sound
                        }
                    }
                } else {
                    Log.e("API Error", "Response Code: ${response.code()} Response Body: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<ChatGPTResponse>, t: Throwable) {
                Log.e("API Call", "Failed to get response: ${t.message}")
            }
        })
    }
}

data class Message(val role: String, val content: String, val avatar: Int)

class ChatAdapter(private val messages: MutableList<Message>) : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    fun getLastPosition(): Int {
        return messages.size - 1
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_chat, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val message = messages[position]
        holder.bind(message)
    }

    override fun getItemCount(): Int = messages.size

    fun addMessage(message: Message) {
        messages.add(message)
        notifyItemInserted(messages.size - 1)
    }

    inner class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val userMessageTextView: TextView = itemView.findViewById(R.id.userMessageTextView)
        private val assistantMessageTextView: TextView = itemView.findViewById(R.id.assistantMessageTextView)
        private val userAvatarImageView: ImageView = itemView.findViewById(R.id.userAvatarImageView)
        private val assistantAvatarImageView: ImageView = itemView.findViewById(R.id.assistantAvatarImageView)

        init {
            userMessageTextView.setOnLongClickListener {
                copyTextToClipboard(userMessageTextView.text.toString())
                true
            }

            assistantMessageTextView.setOnLongClickListener {
                copyTextToClipboard(assistantMessageTextView.text.toString())
                true
            }
        }

        private fun copyTextToClipboard(text: String) {
            val clipboardManager = itemView.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText("message", text)
            clipboardManager.setPrimaryClip(clipData)
            Toast.makeText(itemView.context, "Text copied to clipboard", Toast.LENGTH_SHORT).show()
        }

        fun bind(message: Message) {
            if (message.role == "user") {
                userMessageTextView.visibility = View.VISIBLE
                assistantMessageTextView.visibility = View.GONE
                userAvatarImageView.visibility = View.VISIBLE
                assistantAvatarImageView.visibility = View.GONE
                userMessageTextView.text = message.content
                userMessageTextView.maxLines = Int.MAX_VALUE // Enable text wrapping
            } else {
                userMessageTextView.visibility = View.GONE
                assistantMessageTextView.visibility = View.VISIBLE
                userAvatarImageView.visibility = View.GONE
                assistantAvatarImageView.visibility = View.VISIBLE
                assistantMessageTextView.text = message.content
                assistantMessageTextView.maxLines = Int.MAX_VALUE // Enable text wrapping
            }
        }
}   }