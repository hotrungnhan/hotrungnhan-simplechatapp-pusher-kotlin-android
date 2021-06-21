package com.example.simplechatapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.simplechatapp.R
import com.example.simplechatapp.model.UserModel

class FriendListRecycleViewAdapter : RecyclerView.Adapter<FriendViewHolder>() {
    private var onfrienditemclick: OnFriendItemClick? = null;
    var list: MutableList<UserModel> = mutableListOf()
        set(value) {
            field = value;
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.item_friend, parent, false);
        return FriendViewHolder(view);
    }

    override fun onBindViewHolder(holder: FriendViewHolder, position: Int) {
        holder.bind(list[position])
        holder.itemView.setOnClickListener {
            onfrienditemclick?.onClick(list[position]);
        }
    }

    override fun getItemCount(): Int = list.size

    fun setOnFriendItemClick(e: OnFriendItemClick) {
        onfrienditemclick = e;
    }


}

class FriendViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview) {
    val email: TextView = itemview.findViewById(R.id.friend_email)
    val name: TextView = itemview.findViewById(R.id.friend_name)
    fun bind(data: UserModel) {
        email.text = data.email
        name.text = data.display_name;
    }
}

fun interface OnFriendItemClick {
    fun onClick(friend: UserModel);
}