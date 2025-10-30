package com.example.savefoodreal

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DetailMenuActivity : AppCompatActivity() {

    private lateinit var databaseReference: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_menu)

        databaseReference = FirebaseDatabase.getInstance().getReference("kategori")

        val menuId = intent.getStringExtra("MENU_ID")
        val menuNama = intent.getStringExtra("MENU_NAMA")
        val menuFoto = intent.getStringExtra("MENU_FOTO")
        val menuDeskripsi = intent.getStringExtra("MENU_DESKRIPSI")
        val menuHarga = intent.getDoubleExtra("MENU_HARGA", 0.0)
        val menuDiskon = intent.getDoubleExtra("MENU_DISKON", 0.0)
        val menuStok = intent.getIntExtra("MENU_STOK", 0)
        val menuKategoriId = intent.getStringExtra("MENU_KATEGORI").toString()

        databaseReference.child(menuKategoriId).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val namaKategori = snapshot.child("nama_kategori").getValue(String::class.java)

                // Set data menu dan nama_kategori ke tampilan
                val imageMenu: ImageView = findViewById(R.id.imageMenu)
                val textNamaMenu: TextView = findViewById(R.id.textNamaMenu)
                val tvHargaMenu: TextView = findViewById(R.id.tv_HargaMenu)
                val tvStokMenu: TextView = findViewById(R.id.tv_StokMenu)
                val tvDiskonMenu: TextView = findViewById(R.id.tv_DiskonMenu)
                val tvKategoriMenu: TextView = findViewById(R.id.tv_KategoriMenu)
                val tvDeskripsiMenu: TextView = findViewById(R.id.tv_DeskripsiMenu)

                Glide.with(this@DetailMenuActivity)
                    .load(menuFoto)
                    .error(R.drawable.ic_launcher_background)
                    .into(imageMenu)

                textNamaMenu.text = menuNama
                tvHargaMenu.text = "Rp$menuHarga"
                tvStokMenu.text = "$menuStok"
                tvDiskonMenu.text = "$menuDiskon%"
                tvKategoriMenu.text = namaKategori // Set nama_kategori ke TextView
                tvDeskripsiMenu.text = "$menuDeskripsi"
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error
                Log.e("DetailMenuActivity", "Database error: ${error.message}")
            }
        })

    }
}