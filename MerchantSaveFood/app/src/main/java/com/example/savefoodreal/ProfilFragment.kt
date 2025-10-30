package com.example.savefoodreal

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ProfilFragment : Fragment() {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var currentUser: FirebaseUser
    private lateinit var databaseReference: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profil, container, false)
        val btnEditProfil: Button = view.findViewById(R.id.btnEditProfil)

        val logoutTextView: TextView = view.findViewById(R.id.logout)
        logoutTextView.setOnClickListener {
            logout()
        }
        mAuth = FirebaseAuth.getInstance()
        currentUser = mAuth.currentUser!!
        databaseReference = FirebaseDatabase.getInstance().reference.child("merchant").child(currentUser.uid)

        val tvNama = view.findViewById<TextView>(R.id.tv_nama)
        val tvAlamat = view.findViewById<TextView>(R.id.tv_alamat)
        val tvEmail = view.findViewById<TextView>(R.id.tv_email)
        val tvNoHP = view.findViewById<TextView>(R.id.tv_noHP)
        val ivFoto: ImageView = view.findViewById(R.id.iv_foto)

        // Set up ValueEventListener to fetch merchant data
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Check if the data exists for the current user
                if (snapshot.exists()) {
                    // Retrieve Merchant object
                    val merchant = snapshot.getValue(Merchant::class.java)

                    tvNama.text = "${merchant?.nama}"
                    tvEmail.text= "${merchant?.email}"
                    tvAlamat.text= "${merchant?.alamat}"
                    tvNoHP.text ="${merchant?.noHP}"
                    if (merchant?.foto == "default") {
                        // Load default image from drawable
                        ivFoto.setImageResource(R.drawable.logo)
                    } else {
                        Glide.with(this@ProfilFragment)
                            .load(merchant?.foto)
                            .error(R.drawable.ic_launcher_background)
                            .into(ivFoto)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle errors
            }
        })
        btnEditProfil.setOnClickListener {
            val intent = Intent(activity, EditProfilActivity::class.java)
            startActivity(intent)
        }
        return view
    }

    private fun logout() {
        mAuth.signOut()

        // Redirect to login activity or any other desired activity after logout
        val intent = Intent(activity, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        activity?.finish()
    }

}