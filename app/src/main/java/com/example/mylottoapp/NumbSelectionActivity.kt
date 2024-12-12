package com.example.mylottoapp


import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.NumberPicker
import android.widget.TextView
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
 * Class responsible for letting users select their lucky numbers.
 */
class NumbSelectionActivity : BaseActivity() {

    private val db = Firebase.firestore

    /**
     * Called when the activity is created. Sets up views and click listeners.
     */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_numb_selection)

        val intent = intent
        val name = intent.getStringExtra("NAME")

        var formattedDateTime: String = ""

        val welcomeText = findViewById<TextView>(R.id.selectNumbersText)
        welcomeText.text = "$name, please select your lucky numbers!"

        val numbersText = findViewById<TextView>(R.id.selectedNumbersView)

        val numberPicker = findViewById<NumberPicker>(R.id.numbersPicker)
        numberPicker.maxValue = 49
        numberPicker.minValue = 1

        val selectButton = findViewById<Button>(R.id.selectButton)
        val getRichButton = findViewById<Button>(R.id.getRichButton)
        getRichButton.isEnabled = false

        val selectedNumbers = IntArray(6)
        var index = 0
        var displayText = ""

        // Listener for the "Select" button
        selectButton.setOnClickListener {
            val selectedNumber = numberPicker.value
            if (isNumberUnique(selectedNumber, selectedNumbers)) {
                selectedNumbers[index++] = selectedNumber
                displayText += "$selectedNumber "
                numbersText.text = displayText
                if (index >= selectedNumbers.size) {
                    selectButton.isEnabled = false
                    getRichButton.isEnabled = true

                    val userId = FirebaseAuth.getInstance().currentUser?.uid
                    val email = FirebaseAuth.getInstance().currentUser?.email.toString()

                    val listOfNumbers = selectedNumbers.toList()
                    val fireStoreData = FireStoreData(email, listOfNumbers, null, 0.0)

                    val currentDate = LocalDateTime.now()
                    formattedDateTime = currentDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))

                    // Add data to Firestore
                    GlobalScope.launch(Dispatchers.IO) {
                        userId?.let {
                            db.collection(email)
                                .document(formattedDateTime)
                                .set(fireStoreData).await()
                        }
                    }
                }
                showErrorSnackBar(getString(R.string.numbSelectedSuccessful), false)
            } else {
                showErrorSnackBar(getString(R.string.numbSelectedError), true)
            }
        }

        // Listener for the "Get Rich" button
        getRichButton.setOnClickListener {
            val reminderIntent = Intent(this, ReminderBroadcast::class.java)

            val pendingIntent = PendingIntent.getBroadcast(
                this,
                0,
                reminderIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
                        or PendingIntent.FLAG_IMMUTABLE)

            val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager

            val calendar = Calendar.getInstance()
            val triggerTime = calendar.timeInMillis + 4000 // Trigger after 4 seconds

            alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)

            val drawingIntent = Intent(this, NumbDrawingActivity::class.java)
            drawingIntent.putExtra("DATETIME", formattedDateTime)
            startActivity(drawingIntent)
        }
    }

    /**
     * Checks if a selected number is unique (not already chosen).
     *
     * @param number The number to check.
     * @param array Array of already selected numbers.
     * @return True if the number is unique, false otherwise.
     */
    private fun isNumberUnique(number: Int, array: IntArray): Boolean {
        return number !in array
    }
}
