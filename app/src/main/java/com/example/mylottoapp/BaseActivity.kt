package com.example.mylottoapp

import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar

/**
 * BaseActivity is an open class extending AppCompatActivity.
 * It provides a method to display a Snackbar with a custom message and background color based on the error state.
 */
open class BaseActivity : AppCompatActivity() {

    /**
     * Displays a Snackbar with the specified message.
     * The Snackbar's background color is determined by the errorMessage parameter.
     *
     * @param message The message to display in the Snackbar.
     * @param errorMessage Boolean indicating whether the message is an error message.
     */
    fun showErrorSnackBar(message: String, errorMessage: Boolean) {
        // Create a Snackbar with the specified message
        val snackbar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT)
        val snackbarView = snackbar.view

        // Set the background color based on the error state
        val backgroundColorRes = if (errorMessage) {
            R.color.snackBarError
        } else {
            R.color.snackBarSuccessful
        }

        snackbarView.setBackgroundColor(ContextCompat.getColor(this@BaseActivity, backgroundColorRes))

        // Show the Snackbar
        snackbar.show()
    }
}
