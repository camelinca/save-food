package com.example.savefoodreal

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.savefoodreal.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener


class MenuFragment : Fragment() {

    private lateinit var databaseReference: DatabaseReference
    private lateinit var menuAdapter: MenuAdapter
    private lateinit var itemList: MutableList<Menu> // Use your data class name

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_menu, container, false)

        val recyclerView: RecyclerView = view.findViewById(R.id.listMenu)
        val layoutManager = LinearLayoutManager(activity)
        recyclerView.layoutManager = layoutManager

        itemList = mutableListOf()
        menuAdapter = MenuAdapter(itemList)
        recyclerView.adapter = menuAdapter

        val auth = FirebaseAuth.getInstance()
        val userId = auth.currentUser?.uid

        if (userId != null) {
            // Query only the menu items with id_merchant equal to the current user's ID
            val query: Query = FirebaseDatabase.getInstance().getReference("menu")
                .orderByChild("id_merchant")
                .equalTo(userId)

            // Add a ValueEventListener to the query
            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    itemList.clear()

                    for (menuSnapshot in snapshot.children) {
                        val menu = menuSnapshot.getValue(Menu::class.java)
                        menu?.let { itemList.add(it) }
                            ?: Log.w("MenuFragment", "Failed to convert menuSnapshot to Menu")
                    }

                    menuAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle the error
                    Log.e("MenuFragment", "Database error: ${error.message}")
                }
            })
        } else {
            // Handle the case where the user is not logged in
            Log.e("MenuFragment", "User not logged in")
        }

        // Find the ImageButton in the layout
        val btnTambahMenu: ImageButton = view.findViewById(R.id.btnTambahMenu)

        // Set a click listener for the ImageButton
        btnTambahMenu.setOnClickListener {
            val intent = Intent(activity, TambahMenuActivity::class.java)
            startActivity(intent)
        }

        return view
    }

    private fun fetchDataFromFirebase() {
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                itemList.clear()

                for (menuSnapshot in snapshot.children) {
                    val menu = menuSnapshot.getValue(Menu::class.java)
                    menu?.let { itemList.add(it) } ?: Log.w("MenuFragment", "Failed to convert menuSnapshot to Menu")
                }

                menuAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error
            }
        })
    }
}