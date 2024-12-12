package com.example.mylottoapp.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.mylottoapp.R
import com.example.mylottoapp.firestore.FireStoreData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

/**
 * BlankFragment is responsible for displaying the details of a selected lotto game.
 */
class BlankFragment : Fragment() {

    private val db = Firebase.firestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_blank, container, false)
        val position = arguments?.getInt("position")
        val winText = rootView.findViewById<TextView>(R.id.winTV)
        val backButton = rootView.findViewById<Button>(R.id.backButton)

        // Handle back button click to remove fragment
        backButton.setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commit()
        }

        // Fetch and display lotto game details if position is valid
        position?.let { fetchGameDetails(it, winText) }

        return rootView
    }

    /**
     * Fetches and displays the game details for the given position.
     *
     * @param position The position of the selected game.
     * @param winText The TextView where the winning amount will be displayed.
     */
    private fun fetchGameDetails(position: Int, winText: TextView) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val email = FirebaseAuth.getInstance().currentUser?.email.orEmpty()
                val querySnapshot = db.collection(email).get().await()
                val documentIds = querySnapshot.documents.map { it.id }

                if (position in documentIds.indices) {
                    val documentSnapshot = db.collection(email).document(documentIds[position]).get().await()
                    val dbData = documentSnapshot.toObject(FireStoreData::class.java)

                    // Update the UI on the main thread
                    CoroutineScope(Dispatchers.Main).launch {
                        winText.text = dbData?.win?.toString() ?: "No data available"
                    }
                } else {
                    Log.e("BlankFragment", "Position is out of range")
                }
            } catch (e: Exception) {
                Log.e("BlankFragment", "Error fetching game details", e)
            }
        }
    }
}
