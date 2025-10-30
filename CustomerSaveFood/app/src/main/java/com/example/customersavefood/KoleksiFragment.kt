package com.example.customersavefood

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class KoleksiFragment : Fragment() {

    private lateinit var database: FirebaseDatabase
    private lateinit var favoritRef: DatabaseReference
    private lateinit var merchantRef: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inisialisasi Firebase
        database = FirebaseDatabase.getInstance()
        favoritRef = database.getReference("favorit")
        merchantRef = database.getReference("merchant")
        loadFavoritData()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_koleksi, container, false)
    }
    private fun loadFavoritData() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val idKonsumen = currentUser?.uid

        favoritRef.orderByChild("id_konsumen").equalTo(idKonsumen).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val favoritList = mutableListOf<Favorit>()

                for (favoritSnapshot in snapshot.children) {
                    val favorit = favoritSnapshot.getValue(Favorit::class.java)
                    favorit?.let { favoritList.add(it) }
                }
                Log.d("YourTag", "Favorit list size: ${favoritList.size}")

                loadMerchantData(favoritList)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }

    private fun loadMerchantData(favoritList: List<Favorit>) {
        val merchantList = mutableListOf<Merchant>()

        for (favorit in favoritList) {
            merchantRef.child(favorit.id_merchant).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val merchant = snapshot.getValue(Merchant::class.java)
                    merchant?.let { merchantList.add(it) }
                    if (merchantList.size == favoritList.size) {
                        displayData(merchantList)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
        }
    }
    private fun displayData(merchantList: List<Merchant>) {
        val recyclerView: RecyclerView = view?.findViewById(R.id.listKoleksi) ?: return
        val adapter = KoleksiAdapter(merchantList)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
    }
}