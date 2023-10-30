package com.example.mylottoapp

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import java.util.Random


class NumbDrawingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_numb_drawing)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)


        val drawingButton = findViewById<Button>(R.id.numberDrawingButton)

        progressBar.max = 6
        val delayMillis = 1000L
        val handler = Handler(Looper.getMainLooper())

        val buttons = listOf(
            findViewById<Button>(R.id.button1),
            findViewById<Button>(R.id.button2),
            findViewById<Button>(R.id.button3),
            findViewById<Button>(R.id.button4),
            findViewById<Button>(R.id.button5),
            findViewById<Button>(R.id.button6)
        )

        val selectedNumbers = intent.getIntArrayExtra("SELECTEDNUMBERS")

//        val selectedNumbersView = findViewById<TextView>(R.id.selectedNumbers)
//        var text = "Yours numbers are: "
//        if (selectedNumbers != null) {
//            for (element in 0..selectedNumbers.size - 1) {
//                text += selectedNumbers[element].toString()
//                text = "$text "
//            }
//        }
//        selectedNumbersView.text = text

        drawingButton.setOnClickListener {

            drawingButton.isEnabled=false

            for (button in buttons) {
                button.visibility = View.INVISIBLE
            }


            Thread {
                var progressStatus = 0
                val drawingNumbs = lotto()

                for (button in buttons) {
                    progressStatus += 1
                    // Update the progress bar and display the
                    //current value in the text view
                    handler.post {
                        progressBar.progress = progressStatus
                        button.text = drawingNumbs[progressStatus-1].toString()
                if (selectedNumbers != null) {
                    if(drawingNumbs[progressStatus-1] in selectedNumbers){
                       button.setBackgroundColor(Color.GREEN)
                        button.setTextColor(Color.WHITE)
                    } else{
                        button.setBackgroundColor(Color.RED)
                        button.setTextColor(Color.WHITE)
                    }
                }
                button.visibility = View.VISIBLE



                    }
                    try {
                        // Sleep for 1000 milliseconds.
                        Thread.sleep(delayMillis)
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                }
                runOnUiThread {
                    drawingButton.isEnabled=true
                }
            }.start()

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







