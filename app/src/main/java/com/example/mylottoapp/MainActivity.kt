package com.example.mylottoapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val switchView = findViewById<Switch>(R.id.ageSwitch)
        val buttonGame = findViewById<Button>(R.id.gameButton)
        buttonGame.isEnabled = false

        switchView.setOnCheckedChangeListener { _, isChecked ->
            buttonGame.isEnabled = isChecked
            switchView.text = "YES"
            if (!isChecked)
                switchView.text = "NO"
        }
        val inputName = findViewById<EditText>(R.id.editTextName)
        val inputMail = findViewById<EditText>(R.id.editTextTextEmailAddress)
        val inputPhone = findViewById<EditText>(R.id.editTextPhone)
        buttonGame.setOnClickListener {

            var name = inputName.text.toString()
            if (name.isEmpty()) {
                name = "unknown"
            }
            var email = inputMail.text.toString()
            if (email.isEmpty()) {
                email = "unknown"
            }
            var phone = inputPhone.text.toString()
            if (phone.isEmpty()) {
                phone = "unknown"
            }

            val intent = Intent(this, NumbSelectionActivity::class.java)
            intent.putExtra("NAME", name)
            intent.putExtra("EMAIL", email)
            intent.putExtra("PHONE", phone)
            startActivity(intent)

        }
    }
}



