package com.example.simplechatapp.model

import com.google.firebase.firestore.DocumentId
import java.io.Serializable

class UserModel : Serializable {
    @DocumentId
    var uuid: String = ""
    var friend_uuid: MutableList<String> = mutableListOf()
    var email = ""
    var display_name: String = ""
}