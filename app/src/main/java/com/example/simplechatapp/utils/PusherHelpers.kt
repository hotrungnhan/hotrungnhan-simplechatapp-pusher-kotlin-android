package com.example.simplechatapp.utils

import android.util.Log
import com.pusher.client.Pusher
import com.pusher.client.PusherOptions
import com.pusher.client.connection.ConnectionEventListener
import com.pusher.client.connection.ConnectionState
import com.pusher.client.connection.ConnectionStateChange
import com.pusher.client.util.HttpAuthorizer


class PusherHelpers {

    companion object {
        @Volatile
        private var instances: PusherHelpers? = null
        fun getInstance(): PusherHelpers {
            return instances ?: synchronized(this) {
                return PusherHelpers()
            }
        }
    }

    val Pusher: Pusher

    init {
        val options = PusherOptions()
        val authorizer = HttpAuthorizer("http://192.168.1.8:5000/pusher/auth")

        options.setCluster("ap1").setAuthorizer(authorizer)
        Pusher =
            Pusher("b0856dce59e982ff2386", options)

        Pusher.connect(object : ConnectionEventListener {
            override fun onConnectionStateChange(change: ConnectionStateChange) {
                Log.i(
                    "Pusher",
                    "State changed from ${change.previousState} to ${change.currentState}"
                )
            }

            override fun onError(
                message: String,
                code: String,
                e: Exception
            ) {
                Log.i(
                    "Pusher",
                    "There was a problem connecting! code ($code), message ($message), exception($e)"
                )
            }
        }, ConnectionState.ALL)
    }
}