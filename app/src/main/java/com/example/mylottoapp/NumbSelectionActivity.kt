package com.example.mylottoapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.NumberPicker
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar
import androidx.core.content.ContextCompat

class NumbSelectionActivity : BaseActivity() {
    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_numb_selection)
        val intent = intent
        val name = intent.getStringExtra("NAME")
        val email = intent.getStringExtra("EMAIL")
        val phone = intent.getStringExtra("PHONE")

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
                }
                showErrorSnackBar(
                    resources.getString(R.string.numbSelectedSuccessful),
                    errorMessage = false
                )

            } else showErrorSnackBar(resources.getString(R.string.numbSelectedError), true)
        }

        getRichButton.setOnClickListener {
            val intent2 = Intent(this, NumbDrawingActivity::class.java)
            intent2.putExtra("SELECTEDNUMBERS", numbersArray)
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