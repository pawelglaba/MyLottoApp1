package com.example.mylottoapp.firestore

/**
 * Data class representing a user in the Firestore database.
 *
 * @property id The unique ID of the user.
 * @property name The name of the user.
 * @property registeredUser Boolean indicating whether the user is registered.
 * @property email The email address of the user.
 */
data class User(
    val id: String = "",
    val name: String = "",
    val registeredUser: Boolean = false,
    val email: String = ""
)
