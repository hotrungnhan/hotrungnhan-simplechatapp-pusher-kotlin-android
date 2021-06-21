package com.example.simplechatapp.adapter

import android.content.BroadcastReceiver
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.simplechatapp.R
import com.example.simplechatapp.model.ChatMessageModel
import com.example.simplechatapp.model.UserModel
import java.util.zip.Inflater


class ChatMessageRecycleViewAdapter() :
    RecyclerView.Adapter<ChatMessageViewHolder>() {
    private var onmessagerender: OnMessageRender? = null;
    var list: MutableList<ChatMessageModel> = mutableListOf()
        set(value) {
            field = value
            this.notifyDataSetChanged()
        }

    fun appendNew(messageModel: ChatMessageModel) {
        list.add(messageModel)
        this.notifyItemInserted(list.lastIndex);
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatMessageViewHolder {
        val root = LayoutInflater.from(parent.context).inflate(R.layout.item_chat, parent, false)
        return ChatMessageViewHolder(root)
    }

    override fun onBindViewHolder(holder: ChatMessageViewHolder, position: Int) {
        holder.bind(list[position]);
        onmessagerender?.onRender(holder, list[position])
    }

    override fun getItemCount(): Int = list.size
    fun setOnMessageRender(e: OnMessageRender) {
        this.onmessagerender = e;
    }
}

class ChatMessageViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview) {
    var content: TextView = itemview.findViewById(R.id.chat_content)

    fun bind(data: ChatMessageModel) {
        content.text = data.content

    }
}

fun interface OnMessageRender {
    fun onRender(view: ChatMessageViewHolder, data: ChatMessageModel)
}