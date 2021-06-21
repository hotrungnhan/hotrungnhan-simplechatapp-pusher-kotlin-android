package com.example.simplechatapp.fragment

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.simplechatapp.R
import com.example.simplechatapp.adapter.FriendListRecycleViewAdapter
import com.example.simplechatapp.model.UserModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

class FriendListFragment : Fragment() {
    var friendwm = FriendViewModel();
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_friend_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val rc: RecyclerView = view.findViewById(R.id.friend_rc)
        val adapter = FriendListRecycleViewAdapter();
        val layoutmanager = LinearLayoutManager(context).apply {
            orientation = RecyclerView.VERTICAL
        };
        ;
        val addbtn: FloatingActionButton = view.findViewById(R.id.friend_add_btn)

        addbtn.setOnClickListener(this::addDialog)
        friendwm.friend.observe(viewLifecycleOwner) {
            adapter.apply {
                list = friendwm.friend.value!! as MutableList<UserModel>;
                notifyDataSetChanged();
            }
        }
        rc.apply {
            this.adapter = adapter
            this.layoutManager = layoutmanager
        }
        adapter.setOnFriendItemClick { friend ->
            this.parentFragmentManager.beginTransaction()
                .replace(
                    R.id.fragmentContainerView,
                    ChatFragment.newInstance(friendwm.currentuserinfo, friend)
                )
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .addToBackStack("back")
                .commit()
        }
    }

    fun addDialog(view: View) {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog, null, false);
        val content = view.findViewById<EditText>(R.id.dialog_content)
        var alert = AlertDialog.Builder(context).setView(view)
            .setPositiveButton("Thêm bạn") { dialogInterface: DialogInterface, i: Int ->
                friendwm.addFriend(content.text.toString()).observe(viewLifecycleOwner) {
                    toast(it)
                }
            }
            .setNegativeButton("Hủy ỏ") { dialogInterface: DialogInterface, i: Int ->

            }
        alert.show();
    }

    fun toast(text: String) {
        Toast.makeText(requireContext(), text, Toast.LENGTH_LONG).show()
    }
}