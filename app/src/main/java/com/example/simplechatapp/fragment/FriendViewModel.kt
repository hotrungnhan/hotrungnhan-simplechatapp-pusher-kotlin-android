package com.example.simplechatapp.fragment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.example.simplechatapp.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await

class FriendViewModel : ViewModel() {
    val friend: MutableLiveData<List<UserModel>> = MutableLiveData()
    private val usercol = FirebaseFirestore.getInstance().collection("user")
    private var currentuser = FirebaseAuth.getInstance().currentUser;

    lateinit var currentuserinfo: UserModel;

    init {
        updateFriendList();
    }


    fun updateFriendList() {
        viewModelScope.launch {
            currentuserinfo =
                usercol.document(currentuser?.uid!!).get().await().toObject(UserModel::class.java)!!
            if (currentuserinfo?.friend_uuid?.isNotEmpty() == true) {
                friend.postValue(
                    usercol.whereIn(FieldPath.documentId(), currentuserinfo?.friend_uuid!!)
                        .limit(10).get()
                        .await()
                        .toObjects(UserModel::class.java)
                )
            }
        }
    }

    fun addFriend(email: String): MutableLiveData<String> {
        var res: MutableLiveData<String> = MutableLiveData()
        viewModelScope.launch {
            if (currentuserinfo.email == email) {
                res.postValue("Không thể add bản thân bạn")
            } else {
                val finded = usercol.whereEqualTo("email", email).limit(1).get().await()
                    .toObjects(UserModel::class.java)
                if (finded.size > 0 && !currentuserinfo.friend_uuid.contains(finded[0].uuid)) {
                    res.postValue("đã thêm ${finded[0].email} vào danh sách bạn của cả hai")
                    adduuidToUser(currentuserinfo.uuid, finded[0].uuid)
                    adduuidToUser(finded[0].uuid, currentuserinfo.uuid)
                    updateFriendList();
                } else res.postValue("Không tìm thấy")
            }
        }
        return res;
    }

    private fun adduuidToUser(owneruuid: String, frienduuid: String) {
        viewModelScope.launch {
            usercol.document(owneruuid).update("friend_uuid", FieldValue.arrayUnion(frienduuid))
                .await()
        }
    }

    fun deleteFriend(uuid: String) {
        var res: MutableLiveData<String> = MutableLiveData()
        viewModelScope.launch {
            usercol.document(currentuserinfo.uuid)
                .update("friend_uuid", FieldValue.arrayRemove(uuid))

        }
        updateFriendList();
    }

}