package com.example.simplechatapp.model

import com.google.firebase.firestore.DocumentId

class ChatRoomModel {
    @DocumentId
    var room_id: String = "";
    var room_name: String = ""
    var attendee_uuid_list: MutableList<String> = mutableListOf()
}