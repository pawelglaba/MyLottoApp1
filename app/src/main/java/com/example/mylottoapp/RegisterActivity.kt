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
 * Klasa odpowiedzialna za rejestrację użytkownika.
 */
class RegisterActivity : BaseActivity() {

    private var inputEmail: EditText? = null
    private var inputName: EditText? = null
    private var inputPassword: EditText? = null
    private var inputRepPass: EditText? = null

    /**
     * Metoda wywoływana przy tworzeniu aktywności.
     * Inicjalizuje widoki i ustawia nasłuchiwacze kliknięć.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Inicjalizacja przycisku rejestracji
        var registerButton: Button = findViewById(R.id.registerButton)
        registerButton.isEnabled = false

        // Ustawienie nasłuchiwacza przełącznika wieku
        val switchView = findViewById<Switch>(R.id.ageSwitch)
        switchView.setOnCheckedChangeListener { _, isChecked ->
            registerButton.isEnabled = isChecked
            switchView.text = if (isChecked) "YES" else "NO"
        }

        // Inicjalizacja pól wprowadzania
        inputEmail = findViewById(R.id.inputLEmaill)
        inputName = findViewById(R.id.inputName)
        inputPassword = findViewById(R.id.inputPassword2)
        inputRepPass = findViewById(R.id.inputPassword2repeat)

        // Ustawienie nasłuchiwacza dla przycisku rejestracji
        registerButton?.setOnClickListener {
            if (validateRegisterDetails()) {
                registerUser()
            }
        }
    }

    /**
     * Metoda walidująca dane rejestracyjne.
     *
     * @return Zwraca true, jeśli dane są poprawne, w przeciwnym razie false.
     */
    private fun validateRegisterDetails(): Boolean {
        return when {
            TextUtils.isEmpty(inputEmail?.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_email), true)
                false
            }
            TextUtils.isEmpty(inputName?.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_name), true)
                false
            }
            TextUtils.isEmpty(inputPassword?.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_password), true)
                false
            }
            TextUtils.isEmpty(inputRepPass?.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_reppassword), true)
                false
            }
            inputPassword?.text.toString().trim { it <= ' ' } != inputRepPass?.text.toString().trim { it <= ' ' } -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_password_mismatch), true)
                false
            }
            else -> true
        }
    }

    /**
     * Metoda przechodząca do aktywności logowania.
     *
     * @param view Widok, który został kliknięty.
     */
    fun goToLogin(view: View) {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    /**
     * Metoda rejestrująca użytkownika.
     */
    private fun registerUser() {
        if (validateRegisterDetails()) {
            val login: String = inputEmail?.text.toString().trim { it <= ' ' }
            val password: String = inputPassword?.text.toString().trim { it <= ' ' }
            val name: String = inputName?.text.toString().trim { it <= ' ' }

            FirebaseAuth.getInstance().createUserWithEmailAndPassword(login, password).addOnCompleteListener(
                OnCompleteListener<AuthResult> { task ->
                    if (task.isSuccessful) {
                        val firebaseUser: FirebaseUser = task.result!!.user!!
                        showErrorSnackBar("You are registered successfully. Your user id is ${firebaseUser.uid}", false)

                        val user = User(
                            firebaseUser.uid,
                            name,
                            true,
                            login
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
     * Metoda wywoływana po pomyślnej rejestracji użytkownika.
     */
    fun userRegistrationSuccess() {
        Toast.makeText(this@RegisterActivity, resources.getString(R.string.register_success), Toast.LENGTH_LONG).show()
    }
}
