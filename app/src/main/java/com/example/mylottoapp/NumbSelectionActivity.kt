package com.example.mylottoapp

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import android.nfc.Tag
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.NumberPicker
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar
import androidx.core.content.ContextCompat
import com.example.mylottoapp.firestore.FireStoreData
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class NumbSelectionActivity : BaseActivity() {

    val db = Firebase.firestore

    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_numb_selection)
        val intent = intent
        val name = intent.getStringExtra("NAME")
        val email = intent.getStringExtra("EMAIL")
        val phone = intent.getStringExtra("PHONE")
        var formattedDateTime: String = ""

        val welcomeText = findViewById<TextView>(R.id.selectNumbersText)
        welcomeText.text = "$name, please select your lucky numbers!"

        val numbersText = findViewById<TextView>(R.id.selectedNumbersView)


        val numbersPicker = findViewById<NumberPicker>(R.id.numbersPicker)
        numbersPicker.maxValue = 49
        numbersPicker.minValue = 1

        val selectButton = findViewById<Button>(R.id.selectButton)
        val getRichButton = findViewById<Button>(R.id.getRichButton)
        getRichButton.isEnabled = false

        val numbersArray = IntArray(6)
        var i = 0
        var text = ""

        selectButton.setOnClickListener {
            var selectedNumber = numbersPicker.value
            if (checkSelectedNumber(selectedNumber, numbersArray)) {

                numbersArray[i++] = selectedNumber
                text += selectedNumber.toString()
                text = "$text "
                numbersText.text = text
                if (i > numbersArray.size - 1) {
                    selectButton.isEnabled = false
                    getRichButton.isEnabled = true

                    val userId = FirebaseAuth.getInstance().currentUser?.uid
                   val email =  FirebaseAuth.getInstance().currentUser?.email.toString()


                    val listOfNumbers =numbersArray.toList()
                    val firebaseData = FireStoreData(email,listOfNumbers,null,0.0)


                    val currentDate = LocalDateTime.now()
                    formattedDateTime = currentDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))


                    userId?.let {
                        db.collection(FirebaseAuth.getInstance().currentUser?.email.toString())
                            .document(formattedDateTime)
                            .set(firebaseData)
                            .addOnSuccessListener {
                                // Handle success
                                println("Document added with ID: $email")
                            }
                            .addOnFailureListener { e ->
                                // Handle failure
                                println("Error adding document: $e")
                            }
                    }
                }
                showErrorSnackBar(
                    resources.getString(R.string.numbSelectedSuccessful),
                    errorMessage = false
                )

            } else showErrorSnackBar(resources.getString(R.string.numbSelectedError), true)

        }

        getRichButton.setOnClickListener {
            val intent2 = Intent(this, NumbDrawingActivity::class.java)
            intent2.putExtra("DATETIME", formattedDateTime)
            startActivity(intent2)
        }


    }

    fun checkSelectedNumber(number: Int, array: IntArray): Boolean {
        for (element in array) {
            if (element == number) {
                return false
            }
        }
        return true
    }


}

