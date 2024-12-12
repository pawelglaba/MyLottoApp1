package com.example.mylottoapp

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import com.google.firebase.auth.FirebaseAuth

/**
 * MainActivity is the main activity of the application that handles user login.
 */
class MainActivity : BaseActivity(), View.OnClickListener {

    private var emailInput: EditText? = null
    private var passwordInput: EditText? = null
    private var loginButton: Button? = null

    /**
     * Called when the activity is created. Initializes views and sets up click listeners.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize input fields and login button
        emailInput = findViewById(R.id.inputEmail)
        passwordInput = findViewById(R.id.inputPassword2)
        loginButton = findViewById(R.id.loginButton)

        // Set click listener for the login button
        loginButton?.setOnClickListener {
            logInRegisteredUser()
        }
    }

    /**
     * Handles click events for widgets.
     *
     * @param view The clicked widget.
     */
    override fun onClick(view: View?) {
        if (view != null) {
            when (view.id) {
                R.id.registerTextView -> {
                    // Navigate to the registration activity
                    val intent = Intent(this, RegisterActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }

    /**
     * Validates login details entered by the user.
     *
     * @return true if login details are valid, false otherwise.
     */
    private fun validateLoginDetails(): Boolean {
        return when {
            // Check if the email is empty
            TextUtils.isEmpty(emailInput?.text.toString().trim()) -> {
                showErrorSnackBar(getString(R.string.err_msg_enter_email), true)
                false
            }
            // Check if the password is empty
            TextUtils.isEmpty(passwordInput?.text.toString().trim()) -> {
                showErrorSnackBar(getString(R.string.err_msg_enter_password), true)
                false
            }
            else -> {
                showErrorSnackBar("Your details are valid", false)
                true
            }
        }
    }

    /**
     * Logs in the registered user using Firebase Authentication.
     */
    private fun logInRegisteredUser() {
        if (validateLoginDetails()) {
            val email = emailInput?.text.toString().trim()
            val password = passwordInput?.text.toString().trim()

            // Sign in using FirebaseAuth
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        showErrorSnackBar("You are logged in successfully.", false)
                        goToNumberSelectionActivity()
                        finish()
                    } else {
                        showErrorSnackBar(task.exception!!.message.toString(), true)
                    }
                }
        }
    }

    /**
     * Navigates to the number selection activity.
     * Passes the email of the logged-in user to the new activity.
     */
    private fun goToNumberSelectionActivity() {
        val user = FirebaseAuth.getInstance().currentUser
        val email = user?.email.toString()

        val intent = Intent(this, NumbSelectionActivity::class.java)
        intent.putExtra("userEmail", email)
        startActivity(intent)
    }
}
