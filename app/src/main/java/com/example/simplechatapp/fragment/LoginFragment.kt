package com.example.simplechatapp.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.simplechatapp.R
import com.example.simplechatapp.model.UserModel
import com.example.simplechatapp.utils.PusherHelpers
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.pusher.client.Pusher

class LoginFragment : Fragment() {
    companion object {
        private const val TAG = "GoogleActivity"
    }

    private val mStore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var navhost: NavController
    private lateinit var googleloginlauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)).requestEmail()
            .build()
        googleSignInClient =
            GoogleSignIn.getClient(requireActivity(), gso)

        googleloginlauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                try {
                    // There are no request codes
                    val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
                    val account = task.getResult(ApiException::class.java)!!
                    Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                    firebaseAuthWithGoogle(account)
                } catch (e: ApiException) {
                    // Google Sign In failed, update UI appropriately
                    Log.w(TAG, "Google sign in failed", e)
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var root = inflater.inflate(R.layout.fragment_login, container, false)
        root.findViewById<Button>(R.id.login).setOnClickListener(this::loginWithGoogle)
        navhost = this.findNavController()
        return root;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (mAuth.currentUser != null) {
            loginSuccess()
        }
    }

    fun loginSuccess() {
        navhost.popBackStack()
        navhost.navigate(R.id.friend_list_fragment)
    }

    fun loginWithGoogle(view: View) {
        val signInIntent = googleSignInClient.signInIntent
        googleloginlauncher.launch(signInIntent);
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener() { task ->
                if (task.isSuccessful) {
                    if (task.result?.additionalUserInfo?.isNewUser == true) {
                        createUserDocument(UserModel().apply {
                            this.uuid = mAuth.currentUser?.uid!!
                            this.email = account.email.toString()
                            this.display_name = account.displayName.toString()
                        })
                    }
                    loginSuccess()
                } else {
                    Toast("login fail")
                }
            }
    }

    fun createUserDocument(data: UserModel) {
        mStore.collection("user").document(data.uuid).set(data)
    }

    fun Toast(text: String) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT)
    }
}