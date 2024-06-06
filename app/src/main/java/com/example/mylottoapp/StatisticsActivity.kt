package com.example.mylottoapp

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

/**
 * Aktywność wyświetlająca statystyki gier użytkownika.
 */
class StatisticsActivity : AppCompatActivity() {

    val db = Firebase.firestore
    val list: MutableList<String> = ArrayList()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistics)

        val recyclerView: RecyclerView = findViewById(R.id.statisticRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this) // Konfiguracja RecyclerView - ustawienie elementów w trybie pionowym

        val email = FirebaseAuth.getInstance().currentUser?.email.toString()

        // Pobieranie dokumentów z Firestore dla zalogowanego użytkownika
        db.collection(email).get()
            .addOnCompleteListener(OnCompleteListener<QuerySnapshot> { task ->
                if (task.isSuccessful) {
                    for (document in task.result) {
                        list.add(document.id)
                    }
                    val gameResults: List<String> = list.toList()
                    val adapter = MyAdapter(gameResults, object : ButtonClickListener {
                        override fun onButtonClick(position: Int) {
                            // dla testów
                            println("Button clicked in item at position: $position")
                        }
                    })
                    recyclerView.adapter = adapter

                    Log.d(TAG, list.toString())
                } else {
                    Log.d(TAG, "Error getting documents: ", task.exception)
                }
            })
    }
}
