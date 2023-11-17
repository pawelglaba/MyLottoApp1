package com.example.mylottoapp

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import com.google.firebase.auth.FirebaseAuth


class MainActivity :BaseActivity(), View.OnClickListener {

    private var inputEmail: EditText? = null
    private var inputPassword: EditText? = null
    private var loginButton: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        inputEmail = findViewById(R.id.inputEmail)
        inputPassword = findViewById(R.id.inputPassword2)
        loginButton = findViewById(R.id.loginButton)


        loginButton?.setOnClickListener{

            logInRegisteredUser()

        }

    }

    override fun onClick(view: View?) {
        if(view !=null){
            when (view.id){
                R.id.registerTextView ->{
                    val intent = Intent(this,RegisterActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }


    private fun validateLoginDetails(): Boolean {

        return when{
            TextUtils.isEmpty(inputEmail?.text.toString().trim{ it <= ' '}) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_email), true)
                false
            }

            TextUtils.isEmpty(inputPassword?.text.toString().trim{ it <= ' '}) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_password),true)
                false
            }

            else -> {
                showErrorSnackBar("Your details are valid",false)
                true
            }
        }


    }

    private fun logInRegisteredUser(){


        if(validateLoginDetails()){
            val email = inputEmail?.text.toString().trim(){ it<= ' '}
            val password = inputPassword?.text.toString().trim(){ it<= ' '}

            //Log-in using FirebaseAuth

            FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password)
                .addOnCompleteListener{task ->

                    if(task.isSuccessful){
                        showErrorSnackBar("You are logged in successfully.", false)
                        goToNumbSelectionActivity()
                        finish()

                    } else{
                        showErrorSnackBar(task.exception!!.message.toString(),true)
                    }
                }
        }
    }

    open fun goToNumbSelectionActivity() {

        val user = FirebaseAuth.getInstance().currentUser;
        val uid = user?.email.toString()

        val intent = Intent(this, NumbSelectionActivity::class.java)
        intent.putExtra("uID",uid)
        startActivity(intent)
    }

}

//class MainActivity : AppCompatActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//
//        //val switchView = findViewById<Switch>(R.id.ageSwitch)
//        val buttonGame = findViewById<Button>(R.id.gameButton)
//        buttonGame.isEnabled = false
//
////        switchView.setOnCheckedChangeListener { _, isChecked ->
////            buttonGame.isEnabled = isChecked
////            switchView.text = "YES"
////            if (!isChecked)
////                switchView.text = "NO"
////        }
//        val inputName = findViewById<EditText>(R.id.editTextEmail)
//        val inputMail = findViewById<EditText>(R.id.editTextTextEmailAddress)
//        //val inputPhone = findViewById<EditText>(R.id.editTextPhone)
//        buttonGame.setOnClickListener {
//
//            var name = inputName.text.toString()
//            if (name.isEmpty()) {
//                name = "unknown"
//            }
//            var email = inputMail.text.toString()
//            if (email.isEmpty()) {
//                email = "unknown"
//            }
////            var phone = inputPhone.text.toString()
////            if (phone.isEmpty()) {
////                phone = "unknown"
////            }
//
//            val intent = Intent(this, NumbSelectionActivity::class.java)
//            intent.putExtra("NAME", name)
//            intent.putExtra("EMAIL", email)
//            //intent.putExtra("PHONE", phone)
//            startActivity(intent)
//
//        }
//    }
//}



