package com.example.customersavefood

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class KeranjangActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MenuKeranjangAdapter
    private lateinit var keranjangList: MutableList<Keranjang>

    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private var totalAmount: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_keranjang)

        auth = FirebaseAuth.getInstance()
        val currentUserId = auth.currentUser?.uid
        val tvTotal = findViewById<TextView>(R.id.total)
        recyclerView = findViewById(R.id.listMenu)
        recyclerView.layoutManager = LinearLayoutManager(this)
        keranjangList = mutableListOf()
        adapter = MenuKeranjangAdapter(keranjangList)
        recyclerView.adapter = adapter

        database = FirebaseDatabase.getInstance().reference.child("keranjang")
        val merchantId = database.orderByChild("id_konsumen").equalTo(currentUserId)
        // Query data based on the current user ID
        val query: Query = database.orderByChild("id_konsumen").equalTo(currentUserId)
        val queri = database.orderByChild("id_konsumen").equalTo(currentUserId)

        queri.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Check if there are any matching items
                if (dataSnapshot.exists()) {
                    // Get the first item
                    val firstItem = dataSnapshot.children.first()
                    val idMerchant = firstItem.child("id_merchant").getValue(String::class.java).toString()
                    fetchMerchantData(idMerchant)
                } else {
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle errors here
                println("Database error: ${databaseError.message}")
            }
        })

        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                keranjangList.clear()
                totalAmount = 0.0
                for (snapshot in dataSnapshot.children) {
                    val keranjangItem = snapshot.getValue(Keranjang::class.java)
                    keranjangItem?.let {
                        keranjangList.add(it)
                        totalAmount += it.total
                    }
                }
                adapter.notifyDataSetChanged()
                tvTotal.text = "Total: $totalAmount"
            }


            override fun onCancelled(databaseError: DatabaseError) {
                // Handle error
            }
        })

        val btnBuatPesanan = findViewById<Button>(R.id.btnBuatPesanan)

        btnBuatPesanan.setOnClickListener {
            createOrdersAndClearCart()
        }


    }
    private fun fetchMerchantData(merchantId: String) {
        val merchantReference = FirebaseDatabase.getInstance().getReference("merchant").child(merchantId)

        merchantReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val merchant = snapshot.getValue(Merchant::class.java)
                    val tvNamaMerchant = findViewById<TextView>(R.id.nama_restoran)
                    tvNamaMerchant.text =  "Restoran: ${merchant?.nama}"
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("RestoranActivity", "Merchant data fetch cancelled: ${error.message}")
            }
        })
    }

    private fun createOrdersAndClearCart() {
        val currentUserId = auth.currentUser?.uid
        val orderReference = FirebaseDatabase.getInstance().getReference("pesanan")
        val detailPesananReference = FirebaseDatabase.getInstance().getReference("detail_pesanan")

        // Get the current date and time
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        // Generate a unique order ID
        val orderId = orderReference.push().key ?: ""

        // Fetch the merchant ID from the first item in the cart
        val firstCartItem = keranjangList.firstOrNull()
        val idMerchant = firstCartItem?.id_merchant ?: ""

        // Create a new Pesanan object with id_merchant
        val pesanan = Pesanan(
            id_order = orderId,
            tanggal_pesanan = currentDate,
            total_harga = totalAmount,
            status = "diajukan",
            id_konsumen = currentUserId ?: "",
            id_merchant = idMerchant
        )

        // Push the new order to the "pesanan" node in Firebase
        orderReference.child(orderId).setValue(pesanan)

        // Iterate through keranjangList to create DetailPesanan entries
        for (keranjangItem in keranjangList) {
            // Generate a unique detail order ID
            val detailOrderId = detailPesananReference.push().key ?: ""

            // Create a new DetailPesanan object
            val detailPesanan = DetailPesanan(
                id_detail = detailOrderId,
                id_order = orderId,
                id_menu = keranjangItem.id_menu,
                jumlah = keranjangItem.jumlah,
                total_harga = keranjangItem.total
            )

            // Push the new detail order to the "detail_pesanan" node in Firebase
            detailPesananReference.child(detailOrderId).setValue(detailPesanan)
        }

        // Clear the cart (delete all items with the current user ID)
        val cartReference = FirebaseDatabase.getInstance().getReference("keranjang")
        val query: Query = cartReference.orderByChild("id_konsumen").equalTo(currentUserId)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    snapshot.ref.removeValue()
                }

                // Show a Toast message
                Toast.makeText(this@KeranjangActivity, "Pesanan berhasil dibuat", Toast.LENGTH_SHORT).show()

                // Navigate to the desired fragment (replace MainActivity::class.java with your main activity)
//                val intent = Intent(this@KeranjangActivity, MainActivity::class.java)
//                intent.putExtra("selectedFragment", YourFragment::class.java.name)
//                startActivity(intent)
//                finish()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle error
            }
        })
    }

}