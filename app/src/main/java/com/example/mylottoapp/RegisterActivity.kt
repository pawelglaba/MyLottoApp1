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


class RegisterActivity : BaseActivity() {

    private var inputEmail: EditText? = null
    private var inputName: EditText? = null
    private var inputPassword: EditText? = null
    private var inputRepPass: EditText? = null





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)


        var registerButton: Button = findViewById(R.id.registerButton)
        registerButton.isEnabled = false

        val switchView = findViewById<Switch>(R.id.ageSwitch)
        switchView.setOnCheckedChangeListener { _, isChecked ->
            registerButton.isEnabled = isChecked
            switchView.text = "YES"
            if (!isChecked)
                switchView.text = "NO"
        }


        inputEmail = findViewById(R.id.inputLEmaill)
        inputName = findViewById(R.id.inputName)
        inputPassword = findViewById(R.id.inputPassword2)
        inputRepPass = findViewById(R.id.inputPassword2repeat)

        registerButton?.setOnClickListener{
            validateRegisterDetails()
            registerUser()

        }
    }

    private fun validateRegisterDetails(): Boolean {

        return when{
            TextUtils.isEmpty(inputEmail?.text.toString().trim{ it <= ' '}) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_email),true)
                false
            }
            TextUtils.isEmpty(inputName?.text.toString().trim{ it <= ' '}) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_name),true)
                false
            }

            TextUtils.isEmpty(inputPassword?.text.toString().trim{ it <= ' '}) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_password),true)
                false
            }

            TextUtils.isEmpty(inputRepPass?.text.toString().trim{ it <= ' '}) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_reppassword),true)
                false
            }

            inputPassword?.text.toString().trim {it <= ' '} != inputRepPass?.text.toString().trim{it <= ' '} -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_password_mismatch),true)
                false
            }


            else -> {
                //showErrorSnackBar("Your details are valid",false)
                true
            }
        }


    }

    fun goToLogin(view: View) {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
    private fun registerUser(){
        if (validateRegisterDetails()){
            val login: String = inputEmail?.text.toString().trim() {it <= ' '}
            val password: String = inputPassword?.text.toString().trim() {it <= ' '}
            val name: String = inputName?.text.toString().trim() {it <= ' '}



            FirebaseAuth.getInstance().createUserWithEmailAndPassword(login,password).addOnCompleteListener(
                OnCompleteListener <AuthResult>{ task ->

                     if(task.isSuccessful){
                    val firebaseUser: FirebaseUser = task.result!!.user!!
                    showErrorSnackBar("You are registered successfully. Your user id is ${firebaseUser.uid}",false)

                    val user = User("Testowe ID",
                        name,
                        true,
                        login,
                    )
                    FireStoreClass().registerUserFS(this@RegisterActivity, user)



                    FirebaseAuth.getInstance().signOut()
                    finish()


                      } else{
                    showErrorSnackBar(task.exception!!.message.toString(),true)
                      }

                }
            )

        }
    }

    fun  userRegistrationSuccess(){

        Toast.makeText(this@RegisterActivity, resources.getString(R.string.register_success),
            Toast.LENGTH_LONG).show()
    }



}