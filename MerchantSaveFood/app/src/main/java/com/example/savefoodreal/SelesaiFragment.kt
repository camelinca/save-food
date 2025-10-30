package com.example.customersavefood

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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


class SelesaiFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_selesai, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tampilkanPesananSelesai()
    }

    private fun tampilkanPesananSelesai() {
        auth = FirebaseAuth.getInstance()
        val currentUserID = auth.currentUser?.uid
        val databaseReference = FirebaseDatabase.getInstance().getReference("pesanan")
        databaseReference.orderByChild("id_merchant").equalTo(currentUserID)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val pesananList = mutableListOf<Pesanan>()

                    for (snapshot in dataSnapshot.children) {
                        val pesanan = snapshot.getValue(Pesanan::class.java)
                        if (pesanan?.status == "selesai") {
                            pesananList.add(pesanan!!)
                        }
                    }

                    tampilkanPesananRecyclerView(pesananList)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle error
                }
            })
    }

    private fun tampilkanPesananRecyclerView(pesananList: List<Pesanan>) {
        val recyclerView = view?.findViewById<RecyclerView>(R.id.listPesanan)
        val layoutManager = LinearLayoutManager(activity)
        recyclerView?.layoutManager = layoutManager
        val adapter = PesananAdapter(pesananList)
        recyclerView?.adapter = adapter
    }
    companion object {

    }
}