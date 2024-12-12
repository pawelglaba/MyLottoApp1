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
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Interface defining a listener for button click events.
 */
interface ButtonClickListener {
    fun onButtonClick(position: Int)
}

/**
 * Adapter for displaying a list of strings with button functionality.
 *
 * ### Introduction to RecyclerView:
 * RecyclerView is a powerful and flexible view for displaying a collection of data.
 * It allows developers to reuse views efficiently and provides a built-in scrolling mechanism.
 * This adapter connects the data (a list of strings) to the RecyclerView and handles user interactions such as deleting an item.
 */
class StatisticsAdapter(
    private val dataSet: MutableList<String>,
    private val buttonClickListener: ButtonClickListener
) : RecyclerView.Adapter<StatisticsAdapter.ViewHolder>() {

    // ViewModel to store button states
    class ButtonViewModel {
        val buttonStates: MutableMap<Int, Boolean> = mutableMapOf()
    }

    private val buttonViewModel = ButtonViewModel()

    // Firestore instance for database operations
    private val db = FirebaseFirestore.getInstance()

    /**
     * ViewHolder class to hold references to views for each item.
     *
     * ### What is a ViewHolder?
     * A ViewHolder is a pattern used to improve RecyclerView's performance. It holds references to the
     * views inside a RecyclerView item to avoid unnecessary findViewById calls during scrolling.
     */
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.textViewSingleItem)
        val buttonDelete: Button = view.findViewById(R.id.DeleteButton)

        /**
         *The init block in Kotlin is a special construct used to initialize the class or perform
         * setup operations when an object is created. It's executed immediately after the primary constructor
         * */


        init {
            // Handle item clicks and interactions
            itemView.setOnClickListener {
                val demoFragment = BlankFragment()

                val position: Int = adapterPosition
                val bundle = Bundle()
                bundle.putInt("position", position)
                demoFragment.arguments = bundle

                val activity = view.context as AppCompatActivity

                val isButtonClicked = buttonViewModel.buttonStates[position] ?: false

                if (isButtonClicked) {
                    val defaultButtonColor =
                        ContextCompat.getColor(view.context, R.color.purple_700)
                    buttonDelete.setBackgroundColor(defaultButtonColor)
                } else {
                    val clickedButtonColor =
                        ContextCompat.getColor(view.context, R.color.purple_200)
                    buttonDelete.setBackgroundColor(clickedButtonColor)
                }

                buttonViewModel.buttonStates[position] = !isButtonClicked

                notifyItemChanged(position)

                activity.supportFragmentManager.beginTransaction()
                    .replace(R.id.statisticView, demoFragment)
                    .addToBackStack(null)
                    .commit()
            }
        }
    }

    /**
     * Inflates the layout for each item and creates a ViewHolder.
     *
     * ### onCreateViewHolder:
     * This method is called when RecyclerView needs a new ViewHolder.
     * The adapter inflates the item layout and creates
     * a ViewHolder to hold the views.
     *
     * @param parent The parent ViewGroup into which the new view will be added.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_example, parent, false)
        return ViewHolder(view)
    }

    /**
     * Binds data to the views inside the ViewHolder.
     *
     * ### onBindViewHolder:
     * This method is called to display the data at the specified position.
     * It updates the contents of the ViewHolder to reflect the item at the given position.
     *
     * @param holder The ViewHolder that should be updated to represent the contents of the item.
     * @param position The position of the item within the data set.
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = dataSet[position]
        holder.textView.text = item

        // Handle delete button click
        holder.buttonDelete.setOnClickListener {
            val documentId = dataSet[position] // Document ID (e.g., game date)

            // Remove item from the Firestore database
            db.collection("users") // Replace with your collection name
                .document(documentId)
                .delete()
                .addOnSuccessListener {
                    // Remove item from the list and notify adapter
                    dataSet.removeAt(position)
                    buttonViewModel.buttonStates.remove(position)

                    notifyItemRemoved(position)
                    notifyItemRangeChanged(position, dataSet.size)

                    buttonClickListener.onButtonClick(position)
                }
                .addOnFailureListener { e ->
                    // Log error or handle the failure
                    e.printStackTrace()
                }
        }

        // Update button appearance based on its state
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

    /**
     * Returns the total number of items in the data set.
     *
     * ### getItemCount:
     * This method tells RecyclerView how many items it needs to display.
     * It is essential for determining the size of the RecyclerView.
     *
     * @return The size of the data set.
     */
    override fun getItemCount(): Int {
        return dataSet.size
    }
}
