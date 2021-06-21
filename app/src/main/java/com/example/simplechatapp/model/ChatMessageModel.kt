package com.example.simplechatapp.model

import com.google.firebase.firestore.DocumentId
import java.util.*

class ChatMessageModel {
    @DocumentId
    var message_id: String = ""
    var sender_uuid: String? = ""
    var content: String? = ""
    var create_at: Date? = null;

    init {
        create_at = Date();
    }
}