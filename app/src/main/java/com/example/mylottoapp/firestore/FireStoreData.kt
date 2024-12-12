package com.example.mylottoapp.firestore

/**
 * Data class representing the Firestore data structure for lotto results.
 *
 * @property email The email address of the user associated with the data.
 * @property selNumb The list of selected numbers chosen by the user.
 * @property drawNumb The list of numbers drawn in the lotto.
 * @property win The amount won by the user in the lotto.
 */
data class FireStoreData(
    var email: String = "",
    var selNumb: List<Int>? = null,
    var drawNumb: List<Int>? = null,
    var win: Double = 0.0
)
