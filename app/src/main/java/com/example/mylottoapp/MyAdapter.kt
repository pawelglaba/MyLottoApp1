package com.example.mylottoapp



    import android.annotation.SuppressLint
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
class MyAdapter(private val dataSet: List<String>) :
    RecyclerView.Adapter<MyAdapter.ViewHolder>() {

    // ViewModel to store button states
    class ButtonViewModel {
        val buttonStates: MutableMap<Int, Boolean> = mutableMapOf()
    }

    // Initialize the ViewModel
    private val buttonViewModel = ButtonViewModel()

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.textViewSingleItem)
        val buttonDelete: Button = view.findViewById(R.id.DeleteButton)

        init {
            itemView.setOnClickListener {
                val demoFragment = BlankFragment()

                // Transfer position value to the fragment using arguments
                val position: Int = adapterPosition
                println("BUTTON PRESSED $position")
                val bundle = Bundle()
                bundle.putInt("position", position)
                demoFragment.arguments = bundle

                val activity = view.context as AppCompatActivity

                // Get the button state from the ViewModel
                val isButtonClicked = buttonViewModel.buttonStates[position] ?: false

                if (isButtonClicked) {
                    // If the button is already clicked, restore the original color
                    val defaultButtonColor =
                        ContextCompat.getColor(view.context, R.color.purple_700)
                    buttonDelete.setBackgroundColor(defaultButtonColor)
                } else {
                    // If the button is not clicked, change the color
                    val clickedButtonColor =
                        ContextCompat.getColor(view.context, R.color.purple_200)
                    buttonDelete.setBackgroundColor(clickedButtonColor)
                }

                // Update the button state in the ViewModel
                buttonViewModel.buttonStates[position] = !isButtonClicked

                // Notify RecyclerView about the item change
                notifyItemChanged(position)

                // Commit the fragment transaction
                activity.supportFragmentManager.beginTransaction()
                    .replace(R.id.statisticView, demoFragment)
                    .addToBackStack(null)
                    .commit()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_example, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = dataSet[position]
        holder.textView.text = item

        // Set the button state based on the ViewModel
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

    override fun getItemCount(): Int {
        return dataSet.size
    }
}

