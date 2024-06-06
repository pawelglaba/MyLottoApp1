package com.example.mylottoapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.mylottoapp.fragments.BlankFragment

// Interfejs definiujący nasłuchiwacz kliknięć przycisku
interface ButtonClickListener {
    fun onButtonClick(position: Int)
}

// Adapter z interfejsem ButtonClickListener
class MyAdapter(private val dataSet: List<String>, private val buttonClickListener: ButtonClickListener) :
    RecyclerView.Adapter<MyAdapter.ViewHolder>() {

    // ViewModel do przechowywania stanów przycisków
    class ButtonViewModel {
        val buttonStates: MutableMap<Int, Boolean> = mutableMapOf()
    }

    // Inicjalizacja ViewModel
    private val buttonViewModel = ButtonViewModel()

    // ViewHolder odpowiedzialny za przechowywanie referencji do widoków
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.textViewSingleItem)
        val buttonDelete: Button = view.findViewById(R.id.DeleteButton)

        init {
            // Ustawienie nasłuchiwacza kliknięć na itemView
            itemView.setOnClickListener {
                val demoFragment = BlankFragment()

                // Przekazanie wartości pozycji do fragmentu za pomocą argumentów
                val position: Int = adapterPosition
                println("BUTTON PRESSED $position")
                val bundle = Bundle()
                bundle.putInt("position", position)
                demoFragment.arguments = bundle

                val activity = view.context as AppCompatActivity

                // Pobranie stanu przycisku z ViewModel
                val isButtonClicked = buttonViewModel.buttonStates[position] ?: false

                if (isButtonClicked) {
                    // Jeśli przycisk był już kliknięty, przywrócenie domyślnego koloru
                    val defaultButtonColor =
                        ContextCompat.getColor(view.context, R.color.purple_700)
                    buttonDelete.setBackgroundColor(defaultButtonColor)
                } else {
                    // Jeśli przycisk nie był kliknięty, zmiana koloru
                    val clickedButtonColor =
                        ContextCompat.getColor(view.context, R.color.purple_200)
                    buttonDelete.setBackgroundColor(clickedButtonColor)
                }

                // Aktualizacja stanu przycisku w ViewModel
                buttonViewModel.buttonStates[position] = !isButtonClicked

                // Powiadomienie RecyclerView o zmianie elementu
                notifyItemChanged(position)

                // Przeprowadzenie transakcji fragmentu
                activity.supportFragmentManager.beginTransaction()
                    .replace(R.id.statisticView, demoFragment)
                    .addToBackStack(null)
                    .commit()
            }
        }
    }

    // Tworzenie ViewHoldera i inicjalizacja widoku
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_example, parent, false)
        return ViewHolder(view)
    }

    // Przypisywanie danych do odpowiednich elementów wewnątrz ViewHoldera
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = dataSet[position]
        holder.textView.text = item

        // Ustawienie metody onClick dla przycisku
        holder.buttonDelete.setOnClickListener {
            // Wywołanie metody onButtonClick z interfejsu
            buttonClickListener.onButtonClick(position)
        }

        // Ustawienie stanu przycisku na podstawie ViewModel
        val isButtonClicked = buttonViewModel.buttonStates[position] ?: false
        if (isButtonClicked) {
            val clickedButtonColor =
                ContextCompat.getColor(holder.itemView.context, R.color.purple_200)
            holder.buttonDelete.setBackgroundColor(clickedButtonColor)
        } else {
            val defaultButtonColor =
                ContextCompat.getColor(holder.itemView.context, R.color.purple_700)
            holder.buttonDelete.setBackgroundColor(defaultButtonColor)
        }
    }

    // Zwracanie liczby elementów w dataSet
    override fun getItemCount(): Int {
        return dataSet.size
    }
}
