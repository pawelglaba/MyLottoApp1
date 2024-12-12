package com.example.mylottoapp

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import android.widget.Toast
import com.example.mylottoapp.firestore.FireStoreClass
import com.example.mylottoapp.firestore.User
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

/**
 * Activity responsible for user registration.
 */
class RegisterActivity : BaseActivity() {

    private var emailInput: EditText? = null
    private var nameInput: EditText? = null
    private var passwordInput: EditText? = null
    private var confirmPasswordInput: EditText? = null

    /**
     * Called when the activity is created. Initializes views and sets up click listeners.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Initialize the register button
        val registerButton: Button = findViewById(R.id.registerButton)
        registerButton.isEnabled = false

        // Set up age confirmation switch listener
        val ageSwitch = findViewById<Switch>(R.id.ageSwitch)
        ageSwitch.setOnCheckedChangeListener { _, isChecked ->
            registerButton.isEnabled = isChecked
            ageSwitch.text = if (isChecked) "YES" else "NO"
        }

        // Initialize input fields
        emailInput = findViewById(R.id.inputLEmaill)
        nameInput = findViewById(R.id.inputName)
        passwordInput = findViewById(R.id.inputPassword2)
        confirmPasswordInput = findViewById(R.id.inputPassword2repeat)

        // Set up register button listener
        registerButton.setOnClickListener {
            if (validateRegistrationDetails()) {
                registerUser()
            }
        }
    }

    /**
     * Validates the registration details provided by the user.
     *
     * @return true if the details are valid, false otherwise.
     */
    private fun validateRegistrationDetails(): Boolean {
        return when {
            TextUtils.isEmpty(emailInput?.text.toString().trim()) -> {
                showErrorSnackBar(getString(R.string.err_msg_enter_email), true)
                false
            }
            TextUtils.isEmpty(nameInput?.text.toString().trim()) -> {
                showErrorSnackBar(getString(R.string.err_msg_enter_name), true)
                false
            }
            TextUtils.isEmpty(passwordInput?.text.toString().trim()) -> {
                showErrorSnackBar(getString(R.string.err_msg_enter_password), true)
                false
            }
            TextUtils.isEmpty(confirmPasswordInput?.text.toString().trim()) -> {
                showErrorSnackBar(getString(R.string.err_msg_enter_reppassword), true)
                false
            }
            passwordInput?.text.toString().trim() != confirmPasswordInput?.text.toString().trim() -> {
                showErrorSnackBar(getString(R.string.err_msg_password_mismatch), true)
                false
            }
            else -> true
        }
    }

    /**
     * Navigates the user to the login activity.
     *
     * @param view The clicked view.
     */
    fun goToLogin(view: View) {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    /**
     * Registers the user using Firebase authentication.
     */
    private fun registerUser() {
        if (validateRegistrationDetails()) {
            val email: String = emailInput?.text.toString().trim()
            val password: String = passwordInput?.text.toString().trim()
            val name: String = nameInput?.text.toString().trim()

            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).addOnCompleteListener(
                OnCompleteListener<AuthResult> { task ->
                    if (task.isSuccessful) {
                        val firebaseUser: FirebaseUser = task.result!!.user!!
                        showErrorSnackBar("Registration successful. User ID: ${firebaseUser.uid}", false)

                        val user = User(
                            firebaseUser.uid,
                            name,
                            true,
                            email
                        )
                        FireStoreClass().registerUserFS(this@RegisterActivity, user)

                        FirebaseAuth.getInstance().signOut()
                        finish()
                    } else {
                        showErrorSnackBar(task.exception!!.message.toString(), true)
                    }
                }
            )
        }
    }

    /**
     * Called when the user is successfully registered in the Firestore database.
     */
    fun userRegistrationSuccess() {
        Toast.makeText(this@RegisterActivity, getString(R.string.register_success), Toast.LENGTH_LONG).show()
    }
}
