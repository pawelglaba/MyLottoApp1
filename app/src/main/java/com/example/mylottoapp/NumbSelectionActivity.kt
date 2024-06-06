package com.example.mylottoapp

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.NumberPicker
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.mylottoapp.firestore.FireStoreData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar

/**
 * Klasa odpowiedzialna za wybór numerów przez użytkownika.
 */
class NumbSelectionActivity : BaseActivity() {

    val db = Firebase.firestore

    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_numb_selection)


        val intent = intent
        val name = intent.getStringExtra("NAME")

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

        // Ustawienie nasłuchiwacza dla przycisku wyboru numerów
        selectButton.setOnClickListener {
            val selectedNumber = numbersPicker.value
            if (checkSelectedNumber(selectedNumber, numbersArray)) {
                numbersArray[i++] = selectedNumber
                text += selectedNumber.toString()
                text = "$text "
                numbersText.text = text
                if (i > numbersArray.size - 1) {
                    selectButton.isEnabled = false
                    getRichButton.isEnabled = true

                    val userId = FirebaseAuth.getInstance().currentUser?.uid
                    val email = FirebaseAuth.getInstance().currentUser?.email.toString()

                    val listOfNumbers = numbersArray.toList()
                    val firebaseData = FireStoreData(email, listOfNumbers, null, 0.0)

                    val currentDate = LocalDateTime.now()
                    formattedDateTime = currentDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                    // dodanie danych do bazy Firestore
                    GlobalScope.launch(Dispatchers.IO) {
                        userId?.let {
                            db.collection(FirebaseAuth.getInstance().currentUser?.email.toString())
                                .document(formattedDateTime)
                                .set(firebaseData).await()
                        }
                    }
                }
                showErrorSnackBar(
                    resources.getString(R.string.numbSelectedSuccessful),
                    errorMessage = false
                )
            } else showErrorSnackBar(resources.getString(R.string.numbSelectedError), true)
        }

        // Ustawienie nasłuchiwacza dla przycisku "get rich"
        getRichButton.setOnClickListener {
            val intentNot = Intent(this, ReminderBroadcast::class.java)
            val pendingIntent = PendingIntent.getBroadcast(this, 0, intentNot, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
            val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
            val calendar = Calendar.getInstance()
            val currentTime = calendar.timeInMillis
            val tenSecondsMillis = 1000 * 4

            alarmManager.set(AlarmManager.RTC_WAKEUP, currentTime + tenSecondsMillis, pendingIntent)

            val intent2 = Intent(this, NumbDrawingActivity::class.java)
            intent2.putExtra("DATETIME", formattedDateTime)
            startActivity(intent2)
        }
    }

    /**
     * Sprawdza, czy wybrany numer nie został już wybrany wcześniej.
     *
     * @param number Numer do sprawdzenia.
     * @param array Tablica wybranych numerów.
     * @return True, jeśli numer nie został jeszcze wybrany, w przeciwnym razie False.
     */
    fun checkSelectedNumber(number: Int, array: IntArray): Boolean {
        for (element in array) {
            if (element == number) {
                return false
            }
        }
        return true
    }
}
