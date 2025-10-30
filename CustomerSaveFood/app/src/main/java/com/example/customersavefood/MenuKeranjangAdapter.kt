package com.example.customersavefood

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MenuKeranjangAdapter(private val keranjangList: List<Keranjang>) :
    RecyclerView.Adapter<MenuKeranjangAdapter.MenuViewHolder>() {

    inner class MenuViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val namaMenu: TextView = itemView.findViewById(R.id.nama_menu)
        val hargaMenu: TextView = itemView.findViewById(R.id.harga_menu)
        val jumlahMenu: TextView = itemView.findViewById(R.id.jumlah_menu)
        val gambarMenu: ImageView = itemView.findViewById(R.id.gambar_menu)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.keranjang, parent, false)

        return MenuViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        val currentItem = keranjangList[position]
        holder.namaMenu.text = currentItem.nama_menu
        holder.hargaMenu.text = "Rp${currentItem.total}"
        holder.jumlahMenu.text = "Stok: ${currentItem.jumlah}"

        Glide.with(holder.itemView.context)
            .load(currentItem.gambar_menu)
            .into(holder.gambarMenu)
    }
    private fun fetchMerchantData(merchantId: String, callback: (String?) -> Unit) {
        val merchantReference = FirebaseDatabase.getInstance().getReference("merchant").child(merchantId)

        merchantReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val namaMerchant = dataSnapshot.child("nama_merchant").getValue(String::class.java)
                callback(namaMerchant)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("MerchantDebug", "Error reading merchant data from the database: ${databaseError.message}")
                callback(null)
            }
        })
    }

    override fun getItemCount() = keranjangList.size

}