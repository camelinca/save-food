package com.example.customersavefood

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.savefoodreal.Pesanan
import com.example.savefoodreal.PesananAdapter
import com.example.savefoodreal.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class DalamProsesFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dalam_proses, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tampilkanPesananDalamProses()
    }

    private fun tampilkanPesananDalamProses() {
        auth = FirebaseAuth.getInstance()
        val currentUserID = auth.currentUser?.uid
        val databaseReference = FirebaseDatabase.getInstance().getReference("pesanan")
        databaseReference.orderByChild("id_merchant").equalTo(currentUserID)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val pesananList = mutableListOf<Pesanan>()

                    for (snapshot in dataSnapshot.children) {
                        val pesanan = snapshot.getValue(Pesanan::class.java)

                        // Filter pesanan berdasarkan status dan tambahkan ke list jika sesuai
                        if (pesanan?.status != "selesai") {
                            pesananList.add(pesanan!!)
                        }
                    }

                    // Tampilkan pesanan dalam RecyclerView
                    tampilkanPesananRecyclerView(pesananList)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle error
                }
            })
    }

    private fun tampilkanPesananRecyclerView(pesananList: List<Pesanan>) {
        // Inisialisasi RecyclerView
        val recyclerView = view?.findViewById<RecyclerView>(R.id.listPesanan)

        // Set layout manager
        val layoutManager = LinearLayoutManager(activity)
        recyclerView?.layoutManager = layoutManager

        // Inisialisasi adapter dan terapkan ke RecyclerView
        val adapter = PesananAdapter(pesananList)
        recyclerView?.adapter = adapter
    }
    companion object {

    }
}