package com.example.customersavefood

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.customersavefood.databinding.ActivityEditProfilBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference


class EditProfilActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditProfilBinding
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val databaseReference = FirebaseDatabase.getInstance().reference.child("konsumen")

    private val PICK_IMAGE_REQUEST = 1
    private var imageUri: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfilBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivProfile.setOnClickListener {
            pickImageFromGallery()
        }

        binding.btnSimpan.setOnClickListener {
            saveProfileData()
        }
        fetchMerchantData()

    }
    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    private fun fetchMerchantData() {
        val userId = firebaseAuth.currentUser?.uid
        if (userId != null) {
            val merchantRef = databaseReference.child(userId)
            merchantRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val merchant = snapshot.getValue(Konsumen::class.java)
                        if (merchant != null) {
                            populateUI(merchant)
                        }
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    Log.e("EditProfilActivity", "Error fetching merchant data", error.toException())
                    // Handle error as needed
                }
            })
        }
    }

    private fun populateUI(konsumen: Konsumen) {
        // Populate the UI elements with merchant data
        binding.etNama.setText(konsumen.nama)
        binding.etNoHP.setText(konsumen.noHP)
        binding.etAlamat.setText(konsumen.alamat)

        if (konsumen.foto == "default") {
            binding.ivProfile.setImageResource(com.example.customersavefood.R.drawable.logo)
        }else if(konsumen.foto !=null){
            Glide.with(this)
                .load(konsumen.foto)
                .into(binding.ivProfile)
        }else{
            binding.ivProfile.setImageResource(com.example.customersavefood.R.drawable.logo)
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            imageUri = data.data
            val inputStream = contentResolver.openInputStream(imageUri!!)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            binding.ivProfile.setImageBitmap(bitmap)
        }
    }
    private fun saveProfileData() {
        val userId = firebaseAuth.currentUser?.uid
        if (userId != null) {
            val konsumenRef = databaseReference.child(userId)

            // Get the new data from UI elements
            val newName = binding.etNama.text.toString()
            val newNoHP = binding.etNoHP.text.toString()
            val newAlamat = binding.etAlamat.text.toString()

            konsumenRef.child("nama").setValue(newName)
            konsumenRef.child("noHP").setValue(newNoHP)
            konsumenRef.child("alamat").setValue(newAlamat)


            if (imageUri != null) {
                uploadImageAndSetURL(userId, imageUri!!)
            }

            val newPassword = binding.etPassword.text.toString()
            if (newPassword.isNotEmpty()) {
                updatePassword(newPassword)
            }
            finish()
//            val intent = Intent(this, MainActivity::class.java)
//            intent.putExtra("fragmentToLoad", "profil")
//            startActivity(intent)
//            Toast.makeText(this, "Profil berhasil diupdate", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updatePassword(newPassword: String) {
        // Update the password using Firebase Authentication
        firebaseAuth.currentUser?.updatePassword(newPassword)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("EditProfilActivity", "Password updated successfully")
                } else {
                    Log.e("EditProfilActivity", "Error updating password", task.exception)
                    // Handle error as needed
                }
            }
    }
    private fun uploadImageAndSetURL(userId: String, imageUri: Uri) {
        val storageRef: StorageReference = FirebaseStorage.getInstance().reference
        val imageRef: StorageReference = storageRef.child("konsumen_images/$userId.jpg")

        imageRef.putFile(imageUri)
            .addOnSuccessListener { taskSnapshot ->
                // Get the download URL of the uploaded image
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    databaseReference.child(userId).child("foto").setValue(uri.toString())
                        .addOnSuccessListener {
                            Log.d("EditProfilActivity", "Image URL updated successfully")
                        }
                        .addOnFailureListener { e ->
                            Log.e("EditProfilActivity", "Error updating image URL", e)
                        }
                }
            }
            .addOnFailureListener { e ->
                Log.e("EditProfilActivity", "Error uploading image", e)
            }
    }

}