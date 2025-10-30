package com.example.customersavefood

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PesananAdapter(private val pesananList: List<Pesanan>) : RecyclerView.Adapter<PesananAdapter.PesananViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PesananViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.pesanan_proses, parent, false)
        return PesananViewHolder(view)
    }

    override fun onBindViewHolder(holder: PesananViewHolder, position: Int) {
        val pesanan = pesananList[position]

        // Mendapatkan data merchant dari Firebase berdasarkan id_merchant
        val databaseReference = FirebaseDatabase.getInstance().getReference("merchant")
        databaseReference.child(pesanan.id_merchant).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val merchant = dataSnapshot.getValue(Merchant::class.java)

                holder.namaMerchant.text = merchant?.nama
                holder.tanggal.text = pesanan.tanggal_pesanan
                holder.total.text = "Rp ${pesanan.total_harga}"
                if (pesanan.status == "diproses") {
                    holder.status.text = "diterima merchant, silahkan ambil"
                } else {
                    holder.status.text = pesanan.status
                }

                if (merchant?.foto == "default") {
                    Glide.with(holder.itemView.context).load(merchant.foto).into(holder.gambarMerchant)
                } else if (merchant?.foto != null) {
                    Glide.with(holder.itemView.context).load(merchant.foto).into(holder.gambarMerchant)
                } else {
                    holder.gambarMerchant.setImageResource(R.drawable.logo)
                }

                if (pesanan.status == "diproses") {
                    holder.doneButton.visibility = View.VISIBLE
                    holder.doneButton.setOnClickListener {
                        updateStatusPesanan(pesanan.id_order)
                    }
                } else {
                    holder.doneButton.visibility = View.GONE
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle error
            }
        })
        val context = holder.itemView.context
        holder.itemView.setOnClickListener {
            // Open the detail activity and pass the id_order
            val intent = Intent(context, DetailPesananActivity::class.java)
            intent.putExtra("id_order", pesanan.id_order)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return pesananList.size
    }

    class PesananViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val namaMerchant: TextView = itemView.findViewById(R.id.nama_merchant)
        val tanggal: TextView = itemView.findViewById(R.id.tanggal)
        val total: TextView = itemView.findViewById(R.id.total)
        val gambarMerchant: ImageView = itemView.findViewById(R.id.gambar_merchant)
        val status: TextView = itemView.findViewById(R.id.status)
        val doneButton: Button = itemView.findViewById(R.id.done)
    }

    private fun updateStatusPesanan(idOrder: String) {
        val pesananReference = FirebaseDatabase.getInstance().getReference("pesanan")

        val updateStatus = HashMap<String, Any>()
        updateStatus["status"] = "selesai"

        pesananReference.child(idOrder).updateChildren(updateStatus)
            .addOnSuccessListener {
            }
            .addOnFailureListener {
                // Gagal memperbarui status pesanan, handle kesalahan di sini
            }
    }
}