package com.example.simplechatapp.fragment

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.simplechatapp.R
import com.example.simplechatapp.adapter.ChatMessageRecycleViewAdapter
import com.example.simplechatapp.model.ChatMessageModel
import com.example.simplechatapp.model.UserModel
import com.example.simplechatapp.utils.PusherHelpers
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.pusher.client.channel.*
import java.lang.Exception

class ChatFragment : Fragment() {
    private lateinit var sender: UserModel
    private lateinit var receiver: UserModel

    private val pusher = PusherHelpers.getInstance().Pusher
    private lateinit var chatchannel: PrivateChannel
    private lateinit var channelname: String
    private val chatadapter = ChatMessageRecycleViewAdapter();
    private lateinit var roomViewModel: RoomViewModel;
    private lateinit var rc: RecyclerView;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.apply {
            sender = this.getSerializable("sender") as UserModel
            receiver = this.getSerializable("receiver") as UserModel
        }

        channelname = sender.uuid + "." + receiver.uuid
        roomViewModel = RoomViewModel(channelname);
        roomViewModel.room.apply {
            this?.room_id = channelname;
            this?.attendee_uuid_list = mutableListOf(sender.uuid, receiver.uuid)

        }
        chatchannel = pusher.subscribePrivate("private-" + channelname)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_chat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val content = view.findViewById<EditText>(R.id.chat_text_input)
        rc = view.findViewById<RecyclerView>(R.id.chat_rc)
        val btn = view.findViewById<Button>(R.id.chat_button)
        val layoutmanager = LinearLayoutManager(context).apply {
            orientation = RecyclerView.VERTICAL
            stackFromEnd = true
        };


        roomViewModel.message.observe(viewLifecycleOwner) {
            chatadapter.list = it;
        }
        rc.apply {
            this.adapter = chatadapter;
            this.layoutManager = layoutmanager
        }
        chatadapter.setOnMessageRender { viewholder, data ->
            if (data.sender_uuid == receiver.uuid) {
                viewholder.content.apply {
                    background =
                        resources.getDrawable(R.drawable.bg_chat_message_receiver)
                    setTextColor(Color.WHITE)
                    (viewholder.content.layoutParams as LinearLayout.LayoutParams).apply {
                        gravity = Gravity.RIGHT
                    }

                }
            }
        }
        //binding
        content.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                postMessage(content.text.toString())
                content.text.clear();
                return@setOnKeyListener true
            } else return@setOnKeyListener false
        }
        btn.setOnClickListener {
            postMessage(content.text.toString())
        }
        chatchannel.bind("sendmessage",
            object : PrivateChannelEventListener {
                override fun onEvent(e: PusherEvent?) {
                    var newchat = ChatMessageModel().apply {
                        this.content = e?.data
                        this.sender_uuid = receiver.uuid
                    }
                    chatadapter.appendNew(newchat)
                    scrollToLast()
                }

                override fun onSubscriptionSucceeded(channelName: String?) {
                }

                override fun onAuthenticationFailure(message: String?, e: Exception?) {
                }
            })
    }

    fun scrollToLast() {
        rc.scrollToPosition(chatadapter.list.lastIndex)
    }

    fun postMessage(content: String) {
        if (chatchannel.isSubscribed) {
            chatchannel.trigger("client-sendmessage", content)
        }
        var newchat = ChatMessageModel().apply {
            this.content = content
            this.sender_uuid = receiver.uuid
        }
        scrollToLast()
        roomViewModel.postMessageToDB(newchat)
        chatadapter.appendNew(newchat)
    }

    override fun onStop() {
        super.onStop()
        pusher.unsubscribe(channelname)
        Log.d("chat", "unsubcribe")
    }

    companion object {
        fun newInstance(sender: UserModel, receiver: UserModel) =
            ChatFragment().apply {
                arguments = Bundle().apply {
                    this.putSerializable("sender", sender)
                    this.putSerializable("receiver", receiver)
                }
            }
    }
}

