package com.example.customersavefood

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DetailPesananActivity : AppCompatActivity() {

    private lateinit var detailPesananList: MutableList<DetailPesanan>
    private lateinit var detailPesananAdapter: DetailPesananAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_pesanan)

        // Assuming you have the orderId passed from the previous activity
        val orderId = intent.getStringExtra("id_order") ?: ""

        // Initialize RecyclerView and adapter
        val recyclerView: RecyclerView = findViewById(R.id.listMenu)
        detailPesananList = mutableListOf()
        detailPesananAdapter = DetailPesananAdapter(this, detailPesananList)
        recyclerView.adapter = detailPesananAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Fetch data from Firebase Realtime Database
        val databaseReference = FirebaseDatabase.getInstance().reference
        val detailPesananRef = databaseReference.child("detail_pesanan")

        detailPesananRef.orderByChild("id_order").equalTo(orderId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    detailPesananList.clear()

                    for (childSnapshot in snapshot.children) {
                        val detailPesanan = childSnapshot.getValue(DetailPesanan::class.java)
                        if (detailPesanan != null) {
                            detailPesananList.add(detailPesanan)
                        }
                    }

                    detailPesananAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle the error
                    Toast.makeText(
                        this@DetailPesananActivity,
                        "Failed to read value: ${error.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }
}