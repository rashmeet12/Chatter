package com.example.chatter.feature.chat

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.chatter.model.Message
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.storage.storage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID
import javax.inject.Inject

class ChatViewModel @Inject constructor():ViewModel() {
    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages=_messages.asStateFlow()
    val db= Firebase.database

    fun sendMessage(channelID: String, messageText: String?,image:String?=null) {
        val message = Message(
            db.reference.push().key ?: UUID.randomUUID().toString(),
            Firebase.auth.currentUser?.uid ?: "",
            messageText,
            System.currentTimeMillis(),
            Firebase.auth.currentUser?.displayName ?: "",
            null,
            imageUrl = image
        )

        db.reference.child("messages").child(channelID).push().setValue(message)
    }

    fun sendImageMessage(channelID: String, uri: Uri){
        val imageRef = Firebase.storage.reference.child("images/${UUID.randomUUID()}")

        imageRef.putFile(uri).continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            imageRef.downloadUrl
        }.addOnCompleteListener { task ->
            val currentUser = Firebase.auth.currentUser
            if (task.isSuccessful) {
                val downloadUri = task.result
                sendMessage(channelID, null, downloadUri.toString())
            }
            else {
                // Handle the error
                Log.e("ChatViewModel", "Error uploading image", task.exception)
            }
        }

    }

    fun listenForMessages(channelID:String){
        db.getReference("messages").child(channelID).orderByChild("createdAt")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = mutableListOf<Message>()
                    snapshot.children.forEach { data ->
                        val message = data.getValue(Message::class.java)
                        message?.let {
                            list.add(it)
                        }
                    }
                    _messages.value = list
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                    Log.e("ChatViewModel", "Error listening for messages", error.toException())
                }
            })

    }
}