package com.example.customersavefood

import android.content.Context
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

class DetailPesananAdapter(private val context: Context, private val detailPesananList: List<DetailPesanan>) :
    RecyclerView.Adapter<DetailPesananAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val gambarMenu: ImageView = itemView.findViewById(R.id.gambar_menu)
        val namaMenu: TextView = itemView.findViewById(R.id.nama_menu)
        val hargaMenu: TextView = itemView.findViewById(R.id.harga_menu)
        val jumlahMenu: TextView = itemView.findViewById(R.id.jumlah_menu)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.keranjang, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val detailPesanan = detailPesananList[position]

        getImageUrl(detailPesanan.id_menu) { imageUrl, menuName ->
            Glide.with(context)
                .load(imageUrl)
                .into(holder.gambarMenu)

            holder.namaMenu.text = menuName
        }
        holder.hargaMenu.text = "Rp${detailPesanan.total_harga}" // Format the price as needed
        holder.jumlahMenu.text = detailPesanan.jumlah.toString()
    }

    override fun getItemCount(): Int {
        return detailPesananList.size
    }

    private fun getImageUrl(idMenu: String, callback: (imageUrl: String, menuName: String) -> Unit) {
        val databaseReference = FirebaseDatabase.getInstance().reference
        val menuRef = databaseReference.child("menu").child(idMenu)

        menuRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val imageUrl = snapshot.child("foto").getValue(String::class.java) ?: ""
                val menuName = snapshot.child("nama").getValue(String::class.java) ?: ""

                callback(imageUrl, menuName)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error
                callback("", "")
            }
        })
    }
}