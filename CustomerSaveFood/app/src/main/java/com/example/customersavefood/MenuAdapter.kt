package com.example.customersavefood

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class MenuAdapter(private val menus: List<Menu>) : RecyclerView.Adapter<MenuAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.menu, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val menu = menus[position]
        holder.bind(menu)

        // Tambahkan OnClickListener pada setiap item di RecyclerView
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, RestoranActivity::class.java)

            intent.putExtra("MERCHANT_ID", menu.id_merchant)

            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return menus.size
    }
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val gambarMenu: ImageView = itemView.findViewById(R.id.gambar_menu)
        private val namaMenu: TextView = itemView.findViewById(R.id.nama_menu)

        fun bind(menu: Menu) {
            namaMenu.text = menu.nama
            Glide.with(itemView.context)
                .load(menu.foto)
                .error(R.drawable.ic_launcher_background)
                .into(gambarMenu)
        }
    }
}