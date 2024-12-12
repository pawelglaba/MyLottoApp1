package com.example.mylottoapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

/**
 * Activity displaying user game statistics.
 */
class StatisticsActivity : AppCompatActivity() {

    private val db = Firebase.firestore
    private val gameResultsList: MutableList<String> = mutableListOf()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistics)

        val recyclerView: RecyclerView = findViewById(R.id.statisticRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this) // Set RecyclerView to vertical layout

        val email = FirebaseAuth.getInstance().currentUser?.email.orEmpty()


        CoroutineScope(Dispatchers.IO).launch {
            try {
                fetchGameResults(email)
                CoroutineScope(Dispatchers.Main).launch {
                    val adapter = StatisticsAdapter(gameResultsList, object : ButtonClickListener {
                        override fun onButtonClick(position: Int) {
                            Log.d("StatisticsActivity", "Button clicked at position: $position")
                        }
                    })
                    recyclerView.adapter = adapter
                }
            } catch (e: Exception) {
                Log.e("StatisticsActivity", "Error fetching documents", e)
            }
        }
    }

    /**
     * Fetches game results from Firestore and updates the gameResultsList.
     *
     * @param email The email of the logged-in user, used to access their collection.
     */
    private suspend fun fetchGameResults(email: String) {
        val querySnapshot = db.collection(email).get().await()
        for (document in querySnapshot.documents) {
            gameResultsList.add(document.id)
        }
    }
}
