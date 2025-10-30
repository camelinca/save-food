package com.example.customersavefood

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class MenuRestoranAdapter(private val menuList: List<Menu>) :
    RecyclerView.Adapter<MenuRestoranAdapter.MenuViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(menu: Menu)
    }

    var onItemClickListener: OnItemClickListener? = null

    inner class MenuViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val namaMenu: TextView = itemView.findViewById(R.id.nama_menu)
        val deskripsiMenu: TextView = itemView.findViewById(R.id.deskripsi_menu)
        val hargaMenu: TextView = itemView.findViewById(R.id.harga_menu)
        val gambarMenu: ImageView = itemView.findViewById(R.id.gambar_menu)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val clickedMenu = menuList[position]
                    onItemClickListener?.onItemClick(clickedMenu)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.menu_restoran, parent, false)

        return MenuViewHolder(itemView)
    }


    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        val currentItem = menuList[position]

        holder.namaMenu.text = currentItem.nama
        holder.deskripsiMenu.text = currentItem.deskripsi
        holder.hargaMenu.text = "Rp${currentItem.harga * (100 - currentItem.diskon) * 0.01}"


        Glide.with(holder.itemView.context)
            .load(currentItem.foto)
            .into(holder.gambarMenu)

        holder.itemView.setOnClickListener {
            onItemClickListener?.onItemClick(currentItem)
        }

    }

    override fun getItemCount() = menuList.size

}