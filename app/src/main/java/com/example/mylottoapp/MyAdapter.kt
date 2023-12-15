package com.example.mylottoapp



    import android.view.LayoutInflater
    import android.view.View
    import android.view.ViewGroup
    import android.widget.TextView
    import android.widget.Toast
    import androidx.recyclerview.widget.RecyclerView
    import com.example.mylottoapp.firestore.FireStoreData
    import com.google.firebase.Firebase
    import com.google.firebase.auth.FirebaseAuth
    import com.google.firebase.firestore.firestore

class MyAdapter(private val dataSet: List<String>) :
        RecyclerView.Adapter<MyAdapter.ViewHolder>() {


        // The ViewHolder class represents a single view element within the RecyclerView.
        // In the case of this class, it will be a single item in the list.

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

            // Declarations of views for individual elements inside a single list item.
            val textView: TextView = view.findViewById(R.id.textViewSingleItem)

//            In kotlin the init block is often used within classes. The init block is a special code
//            block that is executed during the initialization of an instance of a class
//            this block allows the execution of onitialization code without placing it directly in
//            the body of the class constructor
            init{
                itemView.setOnClickListener { view : View ->
                    val position: Int = adapterPosition
                    Toast.makeText(itemView.context, "You clicked on item # ${position +1}",
                        Toast.LENGTH_SHORT).show()

                }
            }
        }

        // The onCreateViewHolder method creates new instances of ViewHolder, which will be used by the RecyclerView.
        // It is called only when the RecyclerView needs to create a new ViewHolder, for example, during
        // initialization or when scrolling to a new position in the view.

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            // Creating a new view from the XML file (item_example.xml).
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_example, parent, false)
            return ViewHolder(view)
        }

        // The onBindViewHolder method associates data with the appropriate elements inside the ViewHolder.
        // It is called every time the RecyclerView needs to display a new item.

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            // Setting the text in the TextView for the current position.
            holder.textView.text = dataSet[position]
        }

        // The getItemCount method returns the number of items in the data set.
        // It is called by the RecyclerView to determine how many items it needs to display.
        override fun getItemCount(): Int {
            return dataSet.size
        }





}