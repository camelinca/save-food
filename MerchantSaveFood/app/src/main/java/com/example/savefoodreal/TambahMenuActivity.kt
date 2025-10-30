package com.example.savefoodreal

import android.R
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.savefoodreal.databinding.ActivityTambahMenuBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.UUID

class TambahMenuActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTambahMenuBinding
    private val database = FirebaseDatabase.getInstance()
    private val menuRef = database.getReference("menu")
    private val storage = FirebaseStorage.getInstance()
    private val storageRef: StorageReference = storage.reference.child("menu_images")
    private var selectedImageUri: Uri? = null
    private lateinit var categorySpinner: Spinner
    private var categoriesList: MutableList<Kategori> = mutableListOf()

    private val getContent =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                selectedImageUri = it
                Glide.with(this)
                    .load(selectedImageUri)
                    .into(binding.imgMenu)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTambahMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnUploadGambar.setOnClickListener {
            openGallery()
        }

        binding.btnAddMenu.setOnClickListener {
            tambahMenu()
        }
        categorySpinner = binding.spinnerCategory
        loadCategories()
    }

    private fun openGallery() {
        getContent.launch("image/*")
    }
    private fun loadCategories() {
        // Assume you have a reference to the "kategori" node in your database
        val categoryRef = database.getReference("kategori")

        // Add a listener to fetch categories from the database
        categoryRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                categoriesList.clear()
                for (categorySnapshot in snapshot.children) {
                    val category = categorySnapshot.getValue(Kategori::class.java)
                    category?.let {
                        categoriesList.add(it)
                    }
                }
                setupCategorySpinner()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("TambahMenuActivity", "Error fetching categories: ${error.message}")
                // Handle error
            }
        })
    }

    private fun setupCategorySpinner() {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categoriesList.map { it.nama_kategori })
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        val spinnerDropdown = adapter.getView(0, null, categorySpinner) as TextView
        spinnerDropdown.setTextColor(ContextCompat.getColor(this, R.color.black))

        categorySpinner.adapter = adapter
    }

    private fun tambahMenu() {
        val idMenu = menuRef.push().key
        val nama = binding.edtNamaMenu.text.toString()
        val deskripsi = binding.edtDeskripsi.text.toString()
        val harga = binding.edtHarga.text.toString().toDouble()
        val diskon = binding.edtDiskon.text.toString().toDouble()
        val stok = binding.edtStok.text.toString().toInt()

        val currentUserID = FirebaseAuth.getInstance().currentUser?.uid
        val namaKategoriTerpilih = categorySpinner.selectedItem as String

        // Mendapatkan ID kategori berdasarkan nama kategori
        val categoryRef = database.getReference("kategori")
        categoryRef.orderByChild("nama_kategori").equalTo(namaKategoriTerpilih)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (snapshot in dataSnapshot.children) {
                        val idKategori = snapshot.key
                        if (idMenu != null) {
                            if (selectedImageUri != null) {
                                val imageRef: StorageReference = storageRef.child("${UUID.randomUUID()}.jpg")
                                imageRef.putFile(selectedImageUri!!)
                                    .addOnSuccessListener {
                                        imageRef.downloadUrl.addOnSuccessListener { uri ->
                                            val menu = Menu(idMenu, nama, uri.toString(), deskripsi, harga, diskon, stok, currentUserID, idKategori)
                                            menuRef.child(idMenu).setValue(menu)
                                            Toast.makeText(this@TambahMenuActivity, "Berhasil menambah menu", Toast.LENGTH_SHORT).show()
                                            finish()
                                        }
                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(this@TambahMenuActivity, "Gagal mengunggah gambar", Toast.LENGTH_SHORT).show()
                                    }
                            } else {
                                val menu = Menu(idMenu, nama, "", deskripsi, harga, diskon, stok, currentUserID, idKategori)
                                menuRef.child(idMenu).setValue(menu)

                                Toast.makeText(this@TambahMenuActivity, "Berhasil menambah menu", Toast.LENGTH_SHORT).show()
                                finish()
                            }
                        } else {
                            Toast.makeText(this@TambahMenuActivity, "Gagal menambahkan menu", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e("TambahMenuActivity", "Error fetching category ID: ${databaseError.message}")
                    Toast.makeText(this@TambahMenuActivity, "Gagal mendapatkan ID kategori", Toast.LENGTH_SHORT).show()
                }
            })
    }
}