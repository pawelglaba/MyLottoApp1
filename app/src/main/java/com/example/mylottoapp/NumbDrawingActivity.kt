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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.mylottoapp.firestore.FireStoreData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.random.Random

/**
 * Klasa odpowiedzialna za rysowanie numerów i wyświetlanie wyników losowania.
 */
class NumbDrawingActivity : BaseActivity() {

    val db = Firebase.firestore

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
        val statisticsBtn = findViewById<Button>(R.id.statisticsBtn)
        statisticsBtn.isEnabled = false

        val buttons = listOf(
            findViewById<Button>(R.id.button1),
            findViewById<Button>(R.id.button2),
            findViewById<Button>(R.id.button3),
            findViewById<Button>(R.id.button4),
            findViewById<Button>(R.id.button5),
            findViewById<Button>(R.id.button6)
        )

        var selectedNumbers: IntArray? = IntArray(6)

        // Pobieranie danych z Firestore
        if (formattedDateTime != null) {
            db.collection(FirebaseAuth.getInstance().currentUser?.email.toString())
                .document(formattedDateTime)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val dbData = documentSnapshot.toObject(FireStoreData::class.java)
                        selectedNumbers = dbData?.selNumb?.toIntArray()
                    }
                }
                .addOnFailureListener { e ->
                    println("Error getting document usersNumbers - " +
                            "${FirebaseAuth.getInstance().currentUser?.email}: $e")
                }
        }

        // Ustawienie nasłuchiwacza dla przycisku losowania
        drawingButton.setOnClickListener {
            drawingButton.isEnabled = false

            for (button in buttons) {
                button.visibility = View.INVISIBLE
            }

            Thread {
                var progressStatus = 0
                val drawingNumbs = lotto()

                for (button in buttons) {
                    progressStatus += 1
                    handler.post {
                        progressBar.progress = progressStatus
                        button.text = drawingNumbs[progressStatus - 1].toString()
                        if (selectedNumbers?.contains(drawingNumbs[progressStatus - 1]) == true) {
                            button.setBackgroundColor(Color.GREEN)
                            button.setTextColor(Color.WHITE)
                            score++
                        } else {
                            button.setBackgroundColor(Color.RED)
                            button.setTextColor(Color.WHITE)
                        }
                        button.visibility = View.VISIBLE
                    }
                    try {
                        Thread.sleep(delayMillis)
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                }

                runOnUiThread {
                    val winsNumb = simPlayers(doubleArrayOf(7.2e-8, 1.8e-5, 0.00097, 0.077))
                    val win = calculateWin(Random.nextDouble(0.0, 50e6), winsNumb, score)

                    val updates = mapOf(
                        "win" to win,
                        "drawNumb" to drawingNumbs.toList()
                    )

                    if (formattedDateTime != null) {
                        db.collection(FirebaseAuth.getInstance().currentUser?.email.toString())
                            .document(formattedDateTime)
                            .update(updates)
                            .addOnSuccessListener {
                                println("Document updated successfully in usersNumbers" +
                                        "/${FirebaseAuth.getInstance().currentUser?.email}")
                            }
                            .addOnFailureListener { e ->
                                println("Error updating document in usersNumbers" +
                                        "/${FirebaseAuth.getInstance().currentUser?.email}: $e")
                            }
                    }
                    statisticsBtn.isEnabled = true

                    showErrorSnackBar("You win: $win $", errorMessage = false)
                    drawingButton.isEnabled = true
                }
            }.start()
        }

        // Ustawienie nasłuchiwacza dla przycisku statystyk
        statisticsBtn.setOnClickListener {
            val intent = Intent(this, StatisticsActivity::class.java)
            startActivity(intent)
        }
    }

    /**
     * Funkcja symulująca wygrane innych graczy na podstawie tablicy prawdopodobieństw.
     *
     * @param probabilityArray Tablica prawdopodobieństw dla różnych wygranych.
     * @param numberOfPopulation Liczba populacji graczy (domyślnie 1000).
     * @return Tablica z liczbą wygranych dla różnych kategorii.
     */
    fun simPlayers(probabilityArray: DoubleArray, numberOfPopulation: Int = 1000): IntArray {
        val numberOfPlayers = Random.nextInt(numberOfPopulation)
        val numberOfWins = IntArray(4)
        var iterator = 0
        for (probability in probabilityArray) {
            var numberOfWinningPlayers = 0
            repeat(numberOfPlayers) {
                val randomValue = Random.nextDouble(0.0, 1.0)
                if (randomValue < probability) {
                    numberOfWinningPlayers++
                }
            }
            numberOfWins[iterator] = numberOfWinningPlayers
            iterator++
            numberOfWinningPlayers = 0
        }
        return numberOfWins
    }

    /**
     * Funkcja obliczająca wygraną gracza na podstawie kumulacji, liczby innych zwycięzców i wyniku gracza.
     *
     * @param cummulate Kwota kumulacji.
     * @param winners Tablica z liczbą innych zwycięzców.
     * @param score Wynik gracza (liczba trafionych liczb).
     * @return Kwota wygranej gracza.
     */
    fun calculateWin(cummulate: Double = 0.0, winners: IntArray, score: Int = 0): Double {
        return when (score) {
            6 -> (cummulate * 0.44) / (winners[0] + 1)
            5 -> (cummulate * 0.08) / (winners[1] + 1)
            4 -> (cummulate * 0.48) / (winners[2] + 1)
            3 -> (cummulate * 0.48) / (winners[3] + 1)
            else -> 0.0
        }
    }

    /**
     * Funkcja losująca liczby.
     *
     * @param n Liczba losowanych liczb (domyślnie 6).
     * @param m Zakres losowania (domyślnie 6).
     * @return Tablica z wylosowanymi liczbami.
     */
    fun lotto(n: Int = 6, m: Int = 6): IntArray {
        if (m < n) {
            println("Range cannot be smaller than array size")
            return IntArray(n)
        } else {
            val numbers = IntArray(n)
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

    /**
     * Funkcja pobierająca dane z Firestore.
     *
     * @param formattedDateTime Sformatowana data i czas.
     * @return Dane z Firestore w postaci FireStoreData.
     */
    suspend fun fetchDataFromFirestore(formattedDateTime: String): FireStoreData? {
        return suspendCancellableCoroutine { continuation ->
            val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email.toString()
            val db = FirebaseFirestore.getInstance()
            val docRef = db.collection(currentUserEmail).document(formattedDateTime)

            docRef.get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val dbData = documentSnapshot.toObject(FireStoreData::class.java)
                        continuation.resume(dbData)
                    } else {
                        continuation.resume(null)
                    }
                }
                .addOnFailureListener { e ->
                    continuation.resumeWithException(e)
                }
        }
    }
}
