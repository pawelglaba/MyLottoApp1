package com.example.mylottoapp

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.Random

class NumbDrawingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_numb_drawing)

        val drawingNumbersView = findViewById<TextView>(R.id.drawingNumbersV)
        val drawingButton = findViewById<Button>(R.id.numberDrawingButton)

        val selectedNumbers = intent.getIntArrayExtra("SELECTEDNUMBERS")

        val selectedNumbersView = findViewById<TextView>(R.id.selectedNumbers)
        var text = "Yours numbers are: "
        if (selectedNumbers != null) {
            for (element in 0..selectedNumbers.size - 1) {
                text += selectedNumbers[element].toString()
                text = "$text "


            }
        }
        selectedNumbersView.text = text

        drawingButton.setOnClickListener {
            val drawingNumbs = lotto()

            val stringBuilder = StringBuilder()
            stringBuilder.append("Drawing numbers are: ")
            for (number in drawingNumbs) {
                stringBuilder.append(number).append(" ")
            }
            drawingNumbersView.text = stringBuilder
        }


    }

    fun lotto(n: Int = 6, m: Int = 49): IntArray {

        if (m < n) {
            println("Range cannot be smaller than array size")
            return IntArray(n)
        } else {

            var numbers = IntArray(n)
            var iterator = 0
            var number: Int
            var check: Boolean
            do {
                number = (1..m).random()
                check = true
                for (i in 0 until iterator) {
                    if (numbers[i] == number) {
                        check = false
                        break
                    }
                }
                if (check) {
                    numbers[iterator++] = number
                }

            } while (iterator < n)
            return numbers

        }
    }

    fun IntRange.random() =
        Random().nextInt((endInclusive + 1) - start) + start


}







