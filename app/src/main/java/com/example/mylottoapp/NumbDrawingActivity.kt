package com.example.mylottoapp

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import com.example.mylottoapp.firestore.FireStoreData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlin.random.Random

/**
 * Class responsible for drawing numbers and displaying the results.
 */
class NumbDrawingActivity : BaseActivity() {

    private val db = Firebase.firestore

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_numb_drawing)

        val formattedDateTime = intent.getStringExtra("DATETIME")

        var score = 0
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        val drawingButton = findViewById<Button>(R.id.numberDrawingButton)

        progressBar.max = 6
        val delayMillis = 1000L
        val handler = Handler(Looper.getMainLooper())
        val statisticsBtn = findViewById<Button>(R.id.statisticsBtn).apply {
            isEnabled = false
        }

        val buttons = listOf(
            findViewById<Button>(R.id.button1),
            findViewById<Button>(R.id.button2),
            findViewById<Button>(R.id.button3),
            findViewById<Button>(R.id.button4),
            findViewById<Button>(R.id.button5),
            findViewById<Button>(R.id.button6)
        )

        var selectedNumbers: IntArray? = IntArray(6)

        // Fetching data from Firestore
        formattedDateTime?.let { dateTime ->
            db.collection(FirebaseAuth.getInstance().currentUser?.email.orEmpty())
                .document(dateTime)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val dbData = documentSnapshot.toObject(FireStoreData::class.java)
                        selectedNumbers = dbData?.selNumb?.toIntArray()
                    }
                }
                .addOnFailureListener { e ->
                    println("Error getting document for user ${FirebaseAuth.getInstance().currentUser?.email}: $e")
                }
        }

        // Listener for the "Draw Numbers" button
        drawingButton.setOnClickListener {
            drawingButton.isEnabled = false
            buttons.forEach { it.visibility = View.INVISIBLE }

            Thread {
                var progressStatus = 0
                val drawnNumbers = generateRandomNumbers()

                buttons.forEachIndexed { index, button ->
                    progressStatus++
                    handler.post {
                        progressBar.progress = progressStatus
                        button.text = drawnNumbers[index].toString()
                        if (selectedNumbers?.contains(drawnNumbers[index]) == true) {
                            button.setBackgroundColor(Color.GREEN)
                            button.setTextColor(Color.WHITE)
                            score++
                        } else {
                            button.setBackgroundColor(Color.RED)
                            button.setTextColor(Color.WHITE)
                        }
                        button.visibility = View.VISIBLE
                    }
                    Thread.sleep(delayMillis)
                }

                runOnUiThread {
                    val winners = simulatePlayerWins(doubleArrayOf(7.2e-8, 1.8e-5, 0.00097, 0.077))
                    val winAmount = calculateWinnings(Random.nextDouble(0.0, 50e6), winners, score)

                    formattedDateTime?.let { dateTime ->
                        val updates = mapOf(
                            "win" to winAmount,
                            "drawNumb" to drawnNumbers.toList()
                        )
                        db.collection(FirebaseAuth.getInstance().currentUser?.email.orEmpty())
                            .document(dateTime)
                            .update(updates)
                            .addOnSuccessListener {
                                println("Document successfully updated for user ${FirebaseAuth.getInstance().currentUser?.email}")
                            }
                            .addOnFailureListener { e ->
                                println("Error updating document for user ${FirebaseAuth.getInstance().currentUser?.email}: $e")
                            }
                    }

                    statisticsBtn.isEnabled = true
                    showErrorSnackBar("You win: $winAmount $", errorMessage = false)
                    drawingButton.isEnabled = true
                }
            }.start()
        }

        // Listener for the "Statistics" button
        statisticsBtn.setOnClickListener {
            startActivity(Intent(this, StatisticsActivity::class.java))
        }
    }

    /**
     * Simulates player wins based on probability array.
     *
     * @param probabilities Array of probabilities for different win categories.
     * @param population Number of players in the simulation (default 1000).
     * @return Array with the count of winners in each category.
     */
    private fun simulatePlayerWins(probabilities: DoubleArray, population: Int = 1000): IntArray {
        val playerCount = Random.nextInt(population)
        return probabilities.map { probability ->
            (0 until playerCount).count {
                Random.nextDouble() < probability
            }
        }.toIntArray()
    }

    /**
     * Calculates the winnings for the player based on jackpot, other winners, and the player's score.
     *
     * @param jackpot Total jackpot amount.
     * @param winners Array of other winners in each category.
     * @param score Player's score (number of matched numbers).
     * @return Winnings amount for the player.
     */
    private fun calculateWinnings(jackpot: Double, winners: IntArray, score: Int): Double {
        return when (score) {
            6 -> (jackpot * 0.44) / (winners[0] + 1)
            5 -> (jackpot * 0.08) / (winners[1] + 1)
            4 -> (jackpot * 0.48) / (winners[2] + 1)
            3 -> (jackpot * 0.48) / (winners[3] + 1)
            else -> 0.0
        }
    }

    /**
     * Generates a set of random numbers.
     *
     * @param count Number of numbers to generate (default 6).
     * @param range Range of numbers (default 1 to 49).
     * @return Array of randomly generated numbers.
     */
    private fun generateRandomNumbers(count: Int = 6, range: Int = 49): IntArray {
        return (1..range).shuffled().take(count).toIntArray()
    }
}