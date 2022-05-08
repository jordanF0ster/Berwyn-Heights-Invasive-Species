package com.example.invasivespecies

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

// https://firebase.google.com/docs/auth/android/start used as a reference

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    private lateinit var mUsernameEditText: EditText
    private lateinit var mEmailEditText: EditText
    private lateinit var mPasswordEditText: EditText
    private lateinit var mCreateAccountButton: Button
    private lateinit var mSignInButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initialize Firebase Auth
        auth = Firebase.auth

        mUsernameEditText = findViewById(R.id.usernameEditText)
        mEmailEditText = findViewById(R.id.emailEditText)
        mPasswordEditText = findViewById(R.id.passwordEditText)
        mCreateAccountButton = findViewById(R.id.createAccountButton)
        mSignInButton = findViewById(R.id.SignInButton)

        mCreateAccountButton.setOnClickListener {
            createAccount(
                mEmailEditText.text.toString(),
                mPasswordEditText.text.toString()
            )
        }
        mSignInButton.setOnClickListener {
            signInAccount(
                mEmailEditText.text.toString(),
                mPasswordEditText.text.toString()
            )
        }
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if(currentUser != null){
            reload();
        }
    }

    private fun reload() {
        updateUI(auth.currentUser)
    }

    private fun createAccount(email: String, password: String) {
        if (TextUtils.isEmpty(mUsernameEditText.text.toString())) {
            mUsernameEditText.error = "Can't leave Username empty"
            return
        }
        if (TextUtils.isEmpty(email)) {
            mEmailEditText.error = "Can't leave Email empty"
            return
        }
        if (TextUtils.isEmpty(password)) {
            mPasswordEditText.error = "Can't leave Password empty"
            return
        }

            auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    val user = auth.currentUser
                    if (user != null) {
                        user.updateProfile(
                            UserProfileChangeRequest.Builder().setDisplayName(
                                mUsernameEditText.text.toString()
                            ).build()
                        )
                    }
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                    updateUI(null)
                }
            }
    }

    private fun signInAccount(email: String, password: String) {
        if (TextUtils.isEmpty(mUsernameEditText.text.toString())) {
            mUsernameEditText.error = "Can't leave Username empty"
            return
        }
        if (TextUtils.isEmpty(email)) {
            mEmailEditText.error = "Can't leave Email empty"
            return
        }
        if (TextUtils.isEmpty(password)) {
            mPasswordEditText.error = "Can't leave Password empty"
            return
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }
            }
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            startActivity(
                Intent(
                    this,
                    ReportingHomeActivity::class.java
                )
            )
            finish()
        }
    }

    companion object {
        private const val TAG = "LoginActivity"
    }
}