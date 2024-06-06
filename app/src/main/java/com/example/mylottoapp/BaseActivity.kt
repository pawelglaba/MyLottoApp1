package com.example.mylottoapp

import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar

/**
 * BaseActivity jest otwartą klasą, która rozszerza AppCompatActivity.
 * Zapewnia metodę do wyświetlania Snackbar z niestandardowym komunikatem i kolorem tła w zależności od stanu błędu.
 */
open class BaseActivity : AppCompatActivity() {

    /**
     * Wyświetla Snackbar z określonym komunikatem.
     * Kolor tła Snackbar jest określany przez parametr errorMessage.
     *
     * @param message Komunikat do wyświetlenia w Snackbar.
     * @param errorMessage Boolean wskazujący, czy komunikat jest komunikatem o błędzie.
     */
    fun showErrorSnackBar(message: String, errorMessage: Boolean) {
        // Utwórz Snackbar z określonym komunikatem
        val snackbar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT)
        val snackbarView = snackbar.view

        // Ustaw kolor tła w zależności od tego, czy jest to komunikat o błędzie
        if (errorMessage) {
            snackbarView.setBackgroundColor(
                ContextCompat.getColor(
                    this@BaseActivity,
                    R.color.snackBarSuccessful
                )
            )
        } else {
            snackbarView.setBackgroundColor(
                ContextCompat.getColor(
                    this@BaseActivity,
                    R.color.snackBarError
                )
            )
        }

        // Pokaż Snackbar
        snackbar.show()
    }
}
