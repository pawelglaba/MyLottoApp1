package com.example.mylottoapp.firestore

import com.example.mylottoapp.RegisterActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

/**
 * Class responsible for interacting with the Firestore database.
 */
class FireStoreClass {

    private val mFireStore = FirebaseFirestore.getInstance()

    /**
     * Registers a user in the Firestore database.
     *
     * @param activity The activity where this method is called.
     * @param userInfo The user information to be stored.
     */
    fun registerUserFS(activity: RegisterActivity, userInfo: User) {
        mFireStore.collection("users")
            .document(userInfo.id)
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.userRegistrationSuccess()
            }
            .addOnFailureListener { exception ->
                // Log the error or handle the failure appropriately
                activity.showErrorSnackBar("Failed to register user: ${exception.message}", true)
            }
    }
}