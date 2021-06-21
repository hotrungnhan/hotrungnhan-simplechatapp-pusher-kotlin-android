package com.example.simplechatapp.fragment

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.simplechatapp.model.ChatMessageModel
import com.example.simplechatapp.model.ChatRoomModel
import com.google.firebase.firestore.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class RoomViewModel(roomid: String) : ViewModel() {
    var room: ChatRoomModel = ChatRoomModel()
    var message: MutableLiveData<MutableList<ChatMessageModel>> = MutableLiveData()
    val store = FirebaseFirestore.getInstance()
    lateinit var roomdoc: DocumentReference;

    init {
        viewModelScope.launch {
            roomdoc = store.collection("room").document(roomid)
            roomdoc.set(room);
            room = roomdoc.get().await().toObject(ChatRoomModel::class.java)!!;
            loadMessage();
        }
    }

    fun postMessageToDB(data: ChatMessageModel) {
        viewModelScope.launch {
            var newmessage = roomdoc.collection("message")
                .add(data).await().get().await()
                .toObject(ChatMessageModel::class.java);
            message.value?.add(newmessage!!)
        }
    }


    fun loadMessage(limit: Long = 15) {
        viewModelScope.launch {
            var query = roomdoc.collection("message")
                .orderBy("create_at", Query.Direction.DESCENDING)
                .limit(limit).get().await().toObjects(ChatMessageModel::class.java)
            message.postValue(query)
        }
    }

    fun loadOldMessage(paginguuid: String, limit: Long = 15) {
        viewModelScope.launch {
            var query = roomdoc.collection("message")
                .orderBy("create_at", Query.Direction.DESCENDING).startAfter(paginguuid)
                .limit(limit).get().await().toObjects(ChatMessageModel::class.java)
            message.value?.addAll(query);
        }
    }
}