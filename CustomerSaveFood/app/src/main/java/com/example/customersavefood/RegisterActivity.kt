package com.example.customersavefood

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.customersavefood.R
import com.example.customersavefood.databinding.ActivityRegisterBinding
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : AppCompatActivity() {

    lateinit var etNama: EditText
    lateinit var etEmail: EditText
    lateinit var etNoHP: EditText
    private lateinit var etPassword: EditText
    private lateinit var etKonfirmPassword: EditText
    private lateinit var btnRegister: Button
    private lateinit var binding : ActivityRegisterBinding
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val databaseReference = FirebaseDatabase.getInstance().reference.child("konsumen")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        FirebaseApp.initializeApp(this)

        val loginLink: TextView = findViewById(R.id.tv_punyaakun)
        loginLink.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        etNama = findViewById(R.id.et_nama)
        etEmail = findViewById(R.id.et_email)
        etNoHP = findViewById(R.id.et_nohp)
        etPassword = findViewById(R.id.et_password)
        etKonfirmPassword = findViewById(R.id.et_konfirmpassword)
        btnRegister = findViewById(R.id.buttonRegister)

        btnRegister.setOnClickListener {
            registerMerchant()
        }
    }

    private fun registerMerchant() {
        val nama = etNama.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val noHP = etNoHP.text.toString().trim()
        val password = etPassword.text.toString().trim()
        val konfirmPassword = etKonfirmPassword.text.toString().trim()

        if (nama.isEmpty() || email.isEmpty() || noHP.isEmpty() || password.isEmpty() || konfirmPassword.isEmpty()) {
            Toast.makeText(this, "Isi semua kolom", Toast.LENGTH_SHORT).show()
            return
        }

        if (password != konfirmPassword) {
            Toast.makeText(this, "Password tidak cocok", Toast.LENGTH_SHORT).show()
            return
        }

        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val userId = firebaseAuth.currentUser?.uid ?: ""
                    val defaultPhotoURL = "default"
                    val defaultAlamat = "Belum mengisi alamat"

                    val merchant = Konsumen(userId, nama, email, noHP, defaultPhotoURL, defaultAlamat)
                    databaseReference.child(userId).setValue(merchant)
                        .addOnCompleteListener { databaseTask ->
                            if (databaseTask.isSuccessful) {
                                Toast.makeText(this, "Registrasi berhasil", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this, LoginActivity::class.java)
                                startActivity(intent)
                            } else {
                                Toast.makeText(
                                    this,
                                    "Gagal menyimpan data di database",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                } else {
                    Toast.makeText(this, "Registrasi gagal", Toast.LENGTH_SHORT).show()
                }
            }
    }
}