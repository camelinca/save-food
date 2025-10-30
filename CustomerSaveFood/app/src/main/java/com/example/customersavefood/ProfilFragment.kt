package com.example.customersavefood

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

        mAuth = FirebaseAuth.getInstance()
        currentUser = mAuth.currentUser!!
        databaseReference = FirebaseDatabase.getInstance().reference.child("konsumen").child(currentUser.uid)

        val tvNama = view.findViewById<TextView>(R.id.tv_nama)
        val tvEmail = view.findViewById<TextView>(R.id.tv_email)
        val tvAlamat = view.findViewById<TextView>(R.id.tv_alamat)
        val tvNoHP = view.findViewById<TextView>(R.id.tv_noHP)
        val ivFoto: ImageView = view.findViewById(R.id.iv_foto)

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val konsumen = snapshot.getValue(Konsumen::class.java)

                    tvNama.text = "${konsumen?.nama}"
                    tvEmail.text= "${konsumen?.email}"
                    tvAlamat.text= "${konsumen?.alamat}"
                    tvNoHP.text ="${konsumen?.noHP}"
                    if (konsumen?.foto == "default") {
                        ivFoto.setImageResource(R.drawable.logo)
                    } else {
                        Glide.with(this@ProfilFragment)
                            .load(konsumen?.foto)
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
        val tvTotal: TextView = view.findViewById(R.id.total)

        val logoutTextView: TextView = view.findViewById(R.id.logout)
        logoutTextView.setOnClickListener {
            logout()
        }
        val pesananRef = FirebaseDatabase.getInstance().reference.child("pesanan")
        pesananRef.orderByChild("id_konsumen").equalTo(currentUser.uid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(pesananSnapshot: DataSnapshot) {
                if (pesananSnapshot.exists()) {
                    var totalJumlah = 0

                    for (pesanan in pesananSnapshot.children) {
                        val idPesanan = pesanan.key

                        // Get the detail_pesanan(s) with id_pesanan = idPesanan
                        val detailPesananRef = FirebaseDatabase.getInstance().reference.child("detail_pesanan").orderByChild("id_order").equalTo(idPesanan)
                        detailPesananRef.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(detailPesananSnapshot: DataSnapshot) {
                                if (detailPesananSnapshot.exists()) {
                                    for (detailPesanan in detailPesananSnapshot.children) {
                                        val jumlah = detailPesanan.child("jumlah").getValue(Int::class.java) ?: 0
                                        totalJumlah += jumlah
                                    }

                                    // Display the totalJumlah in the TextView
                                    tvTotal.text = "$totalJumlah makanan"
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                // Handle errors
                            }
                        })
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle errors
            }
        })
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