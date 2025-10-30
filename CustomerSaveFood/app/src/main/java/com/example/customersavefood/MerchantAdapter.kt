package com.example.customersavefood

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class MerchantAdapter(private val merchants: List<Merchant>) : RecyclerView.Adapter<MerchantAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.restoran, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val merchant = merchants[position]
        holder.bind(merchant)

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, RestoranActivity::class.java)

            intent.putExtra("MERCHANT_ID", merchant.id_merchant)

            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return merchants.size
    }
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val gambarRestoran: ImageView = itemView.findViewById(R.id.gambar_restoran)
        private val namaRestoran: TextView = itemView.findViewById(R.id.nama_restoran)

        fun bind(mer: Merchant) {
            namaRestoran.text = mer.nama
            Glide.with(itemView.context)
                .load(mer.foto)
                .error(R.drawable.ic_launcher_background) // Replace with your error resource
                .into(gambarRestoran)
        }
    }
}