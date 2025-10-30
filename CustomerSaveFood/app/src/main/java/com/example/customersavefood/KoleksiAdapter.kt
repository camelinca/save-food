package com.example.customersavefood

import android.content.Intent
import android.os.Parcel
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class KoleksiAdapter(private val merchantList: List<Merchant>) : RecyclerView.Adapter<KoleksiAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val imageViewMerchant: ImageView = itemView.findViewById(R.id.gambar_merchant)
        val textViewNamaMerchant: TextView = itemView.findViewById(R.id.nama_merchant)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                val merchant = merchantList[position]
                // Handle click action here, for example, start RestoranActivity
                val intent = Intent(v?.context, RestoranActivity::class.java)
                intent.putExtra("MERCHANT_ID", merchant.id_merchant)
                v?.context?.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.koleksi_restoran, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val merchant = merchantList[position]

        // Menggunakan Glide untuk menampilkan gambar merchant
        Glide.with(holder.itemView.context)
            .load(merchant.foto)
            .placeholder(R.mipmap.ic_launcher) // Placeholder jika gambar tidak tersedia
            .into(holder.imageViewMerchant)

        holder.textViewNamaMerchant.text = merchant.nama
    }

    override fun getItemCount(): Int {
        return merchantList.size
    }
}