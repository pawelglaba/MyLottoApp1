package com.example.mylottoapp

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import com.google.firebase.auth.FirebaseAuth

/**
 * MainActivity to główna aktywność aplikacji, która obsługuje logowanie użytkownika.
 */
class MainActivity : BaseActivity(), View.OnClickListener {

    private var inputEmail: EditText? = null
    private var inputPassword: EditText? = null
    private var loginButton: Button? = null

    /**
     * Metoda wywoływana przy tworzeniu aktywności.
     * Inicjalizuje widoki i ustawia nasłuchiwacze kliknięć.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicjalizacja pól wprowadzania i przycisku logowania
        inputEmail = findViewById(R.id.inputEmail)
        inputPassword = findViewById(R.id.inputPassword2)
        loginButton = findViewById(R.id.loginButton)

        // Ustawienie nasłuchiwacza kliknięcia na przycisk logowania
        loginButton?.setOnClickListener{
            logInRegisteredUser()
        }
    }

    /**
     * Metoda obsługująca kliknięcia na widżetach
     *
     * @param view Widok/widżet, który został kliknięty w tym przypadku TextView.
     */
    override fun onClick(view: View?) {
        if(view != null){
            when (view.id){
                R.id.registerTextView -> {
                    // Przejście do aktywności rejestracji
                    val intent = Intent(this, RegisterActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }

    /**
     * Metoda walidująca dane logowania.
     *
     * @return Zwraca true, jeśli dane logowania są poprawne, w przeciwnym razie false.
     */
    private fun validateLoginDetails(): Boolean {
        return when {
            // Sprawdzenie, czy email jest pusty
            TextUtils.isEmpty(inputEmail?.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_email), true)
                false
            }
            // Sprawdzenie, czy hasło jest puste
            TextUtils.isEmpty(inputPassword?.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_password), true)
                false
            }
            else -> {
                showErrorSnackBar("Your details are valid", false)
                true
            }
        }
    }

    /**
     * Metoda obsługująca logowanie zarejestrowanego użytkownika.
     */
    private fun logInRegisteredUser() {
        if (validateLoginDetails()) {
            val email = inputEmail?.text.toString().trim { it <= ' ' }
            val password = inputPassword?.text.toString().trim { it <= ' ' }

            // Logowanie za pomocą FirebaseAuth
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        showErrorSnackBar("You are logged in successfully.", false)
                        goToNumbSelectionActivity()
                        finish()
                    } else {
                        showErrorSnackBar(task.exception!!.message.toString(), true)
                    }
                }
        }
    }

    /**
     * Metoda przechodząca do aktywności wyboru numerów.
     * Przekazuje adres email zalogowanego użytkownika do nowej aktywności.
     */
    open fun goToNumbSelectionActivity() {
        val user = FirebaseAuth.getInstance().currentUser
        val uid = user?.email.toString()

        val intent = Intent(this, NumbSelectionActivity::class.java)
        intent.putExtra("uID", uid)
        startActivity(intent)
    }
}
