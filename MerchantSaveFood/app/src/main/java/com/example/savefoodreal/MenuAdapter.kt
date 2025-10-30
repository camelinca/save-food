package com.example.savefoodreal

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
            val intent = Intent(context, DetailMenuActivity::class.java)

            intent.putExtra("MENU_ID", menu.id_menu)
            intent.putExtra("MENU_NAMA", menu.nama)
            intent.putExtra("MENU_FOTO", menu.foto)
            intent.putExtra("MENU_DESKRIPSI", menu.deskripsi)
            intent.putExtra("MENU_HARGA", menu.harga)
            intent.putExtra("MENU_DISKON", menu.diskon)
            intent.putExtra("MENU_STOK", menu.stok)
            intent.putExtra("MENU_KATEGORI", menu.id_kategori)

            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return menus.size
    }
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val gambarMenu: ImageView = itemView.findViewById(R.id.gambar_menu)
        private val namaMenu: TextView = itemView.findViewById(R.id.nama_menu)
        private val hargaMenu: TextView = itemView.findViewById(R.id.harga_menu)

        fun bind(menu: Menu) {
            namaMenu.text = menu.nama
            hargaMenu.text = "${menu.harga}"
            Glide.with(itemView.context)
                .load(menu.foto)
                .error(R.drawable.ic_launcher_background) // Replace with your error resource
                .into(gambarMenu)
        }
    }
}