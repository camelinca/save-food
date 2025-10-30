package com.example.customersavefood

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class RestoranActivity : AppCompatActivity() {

    private lateinit var menuRestoranAdapter: MenuRestoranAdapter
    private lateinit var menuRecyclerView: RecyclerView
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restoran)

        val merchantId = intent.getStringExtra(MERCHANT_ID) ?: ""
        val btnKoleksi = findViewById<ImageButton>(R.id.btnKoleksi)
        val konsumenId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        checkFavoriteStatus(konsumenId, merchantId, btnKoleksi)

        btnKoleksi.setOnClickListener {
            toggleFavoriteStatus(konsumenId, merchantId, btnKoleksi)
        }

        menuRecyclerView = findViewById(R.id.listMenu)
        menuRestoranAdapter = MenuRestoranAdapter(emptyList())

        val layoutManager = LinearLayoutManager(this)
        menuRecyclerView.layoutManager = layoutManager
        menuRecyclerView.adapter = menuRestoranAdapter

        // Initialize Firebase Realtime Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("menu")

        // Fetch menu data based on merchantId
        fetchMenuData(merchantId)
        fetchMerchantData(merchantId)

        menuRestoranAdapter = MenuRestoranAdapter(emptyList())
        menuRestoranAdapter.onItemClickListener = object : MenuRestoranAdapter.OnItemClickListener {
            override fun onItemClick(menu: Menu) {
                val currentUser = FirebaseAuth.getInstance().currentUser
                val idKonsumen = currentUser?.uid ?: "1"
                addToCart(menu,idKonsumen)
            }
        }
    }

    private fun fetchMenuData(merchantId: String) {
        val query = databaseReference.orderByChild("id_merchant").equalTo(merchantId)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val menuList = mutableListOf<Menu>()

                for (dataSnapshot in snapshot.children) {
                    val menu = dataSnapshot.getValue(Menu::class.java)
                    menu?.let { menuList.add(it) }
                }

                Log.d("RestoranActivity", "Fetched ${menuList.size} menu items")

                menuRestoranAdapter = MenuRestoranAdapter(menuList)
                menuRestoranAdapter.onItemClickListener = object : MenuRestoranAdapter.OnItemClickListener {
                    override fun onItemClick(menu: Menu) {
                        val currentUser = FirebaseAuth.getInstance().currentUser
                        val idKonsumen = currentUser?.uid ?: "1"
                        addToCart(menu, idKonsumen)
                    }
                }
                menuRecyclerView.adapter = menuRestoranAdapter
                menuRestoranAdapter.notifyDataSetChanged()

            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("RestoranActivity", "Database fetch cancelled: ${error.message}")
            }
        })
    }
    private fun fetchMerchantData(merchantId: String) {
        val merchantReference = FirebaseDatabase.getInstance().getReference("merchant").child(merchantId)

        merchantReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val merchant = snapshot.getValue(Merchant::class.java)

                    // Set merchant information in TextViews
                    val tvNamaMerchant = findViewById<TextView>(R.id.tv_namaMerchant)
                    val tvAlamatMerchant = findViewById<TextView>(R.id.tv_alamatMerchant)
                    val tvNoHPMerchant = findViewById<TextView>(R.id.tv_noHPMerchant)

                    tvNamaMerchant.text = merchant?.nama
                    tvAlamatMerchant.text = merchant?.alamat
                    tvNoHPMerchant.text = merchant?.noHP
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("RestoranActivity", "Merchant data fetch cancelled: ${error.message}")
            }
        })
    }
    fun addToCart(menu: Menu, konsumenId: String) {
        val cartReference = FirebaseDatabase.getInstance().getReference("keranjang")
        val cartQuery = cartReference.orderByChild("id_konsumen").equalTo(konsumenId)

        cartQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(cartSnapshot: DataSnapshot) {
                if (cartSnapshot.exists()) {
                    // Cart is not empty
                    val firstCartItem = cartSnapshot.children.first().getValue(Keranjang::class.java)

                    if (firstCartItem?.id_merchant == menu.id_merchant) {
                        // Same merchant, add menu directly to the cart
                        addMenuToCart(menu, konsumenId)
                        Toast.makeText(this@RestoranActivity, "Berhasil menambahkan menu ke keranjang", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@RestoranActivity, "Mengganti merchant, membersihkan keranjang dan menambah menu baru", Toast.LENGTH_SHORT).show()

                        // Merchant changed, clear the entire cart and then add the menu
                        clearCartAndAddMenu(menu, konsumenId)
                    }
                } else {
                    // Cart is empty, add menu directly
                    addMenuToCart(menu, konsumenId)
                    Toast.makeText(this@RestoranActivity, "Berhasil menambahkan menu ke keranjang", Toast.LENGTH_SHORT).show()

                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("RestoranActivity", "Cart data fetch cancelled: ${error.message}")
            }
        })
    }
    private fun clearCartAndAddMenu(menu: Menu, konsumenId: String) {
        val cartReference = FirebaseDatabase.getInstance().getReference("keranjang")
        val cartQuery = cartReference.orderByChild("id_konsumen").equalTo(konsumenId)

        cartQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(cartSnapshot: DataSnapshot) {
                for (cartItemSnapshot in cartSnapshot.children) {
                    cartItemSnapshot.ref.removeValue()
                }

                addMenuToCart(menu, konsumenId)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("RestoranActivity", "Clear cart cancelled: ${error.message}")
            }
        })
    }
    private fun addMenuToCart(menu: Menu, userId: String) {
        val menuId = menu.id_menu
        val cartReference = FirebaseDatabase.getInstance().getReference("keranjang")
        val userCartReference = cartReference.orderByChild("id_konsumen").equalTo(userId)

        userCartReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(userCartSnapshot: DataSnapshot) {
                var menuAlreadyInCart = false

                for (cartItemSnapshot in userCartSnapshot.children) {
                    val cartItem = cartItemSnapshot.getValue(Keranjang::class.java)

                    if (cartItem?.id_menu == menuId) {
                        // Menu is already in the cart for the user
                        menuAlreadyInCart = true

                        // Update existing item in the cart
                        cartItemSnapshot.ref.child("jumlah").setValue(cartItem.jumlah + 1)
                        cartItemSnapshot.ref.child("total").setValue(
                            (cartItem.total / cartItem.jumlah) * (cartItem.jumlah + 1)
                        )

                        // You can display a message or take other actions here
                        Log.d("RestoranActivity", "Menu quantity updated in the cart")
                        break
                    }
                }

                if (!menuAlreadyInCart) {
                    // Menu is not in the cart for the user, add it
                    val harga = menu.harga ?: 0.0
                    val diskon = menu.diskon ?: 0.0
                    val total = harga * (100 - diskon) * 0.01
                    val newCartItem = Keranjang(
                        id_keranjang = menuId,
                        jumlah = 1,
                        total = total,
                        id_menu = menu.id_menu ?: "",
                        id_konsumen = userId,
                        id_merchant = menu.id_merchant ?: "",
                        nama_menu = menu.nama ?: "",
                        gambar_menu = menu.foto ?: ""
                    )
                    cartReference.push().setValue(newCartItem)

                    // You can display a message or take other actions here
                    Log.d("RestoranActivity", "Menu added to the cart")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("RestoranActivity", "User cart fetch cancelled: ${error.message}")
            }
        })
    }

    private fun checkFavoriteStatus(konsumenId: String, merchantId: String, btnKoleksi: ImageButton) {
        val favoriteReference = FirebaseDatabase.getInstance().getReference("favorit")
        val query = favoriteReference.orderByChild("id_konsumen").equalTo(konsumenId)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("RestoranActivity", "Snapshot exists: ${snapshot.exists()}")
                if (snapshot.exists()) {
                    for (favoriteSnapshot in snapshot.children) {
                        val idMerchantInFavorite = favoriteSnapshot.child("id_merchant").getValue(String::class.java)
                        if (idMerchantInFavorite == merchantId) {
                            // Data favorit ditemukan untuk konsumen dan merchant tertentu
                            btnKoleksi.setBackgroundResource(R.drawable.circle)
                            btnKoleksi.setColorFilter(Color.WHITE)
                            return
                        }
                    }
                }
                // Data favorit tidak ditemukan untuk konsumen dan merchant tertentu
                btnKoleksi.setBackgroundResource(R.drawable.circle_white)
                btnKoleksi.setColorFilter(getColor(R.color.primary))
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("RestoranActivity", "Favorite data fetch cancelled: ${error.message}")
            }
        })
    }
    private fun toggleFavoriteStatus(konsumenId: String, merchantId: String, btnKoleksi: ImageButton) {
        val favoriteReference = FirebaseDatabase.getInstance().getReference("favorit")
        val query = favoriteReference.orderByChild("id_konsumen").equalTo(konsumenId)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (favoriteSnapshot in snapshot.children) {
                        val idMerchantInFavorite = favoriteSnapshot.child("id_merchant").getValue(String::class.java)
                        if (idMerchantInFavorite == merchantId) {
                            // Data favorit ditemukan untuk konsumen dan merchant tertentu
                            // Hapus data favorit
                            favoriteSnapshot.ref.removeValue()
                            Toast.makeText(this@RestoranActivity, "Dihapus dari koleksi", Toast.LENGTH_SHORT).show()
                            btnKoleksi.setBackgroundResource(R.drawable.circle_white)
                            btnKoleksi.setColorFilter(getColor(R.color.primary))
                            return
                        }
                    }
                }

                // Data favorit tidak ditemukan untuk konsumen dan merchant tertentu
                // Tambahkan data favorit baru
                val newFavoriteKey = favoriteReference.push().key
                val favoriteData = HashMap<String, Any>()
                favoriteData["id_konsumen"] = konsumenId
                favoriteData["id_merchant"] = merchantId
                favoriteReference.child(newFavoriteKey ?: "").setValue(favoriteData)
                    .addOnSuccessListener {
                        Toast.makeText(this@RestoranActivity, "Disimpan di koleksi", Toast.LENGTH_SHORT).show()
                        btnKoleksi.setBackgroundResource(R.drawable.circle)
                        btnKoleksi.setColorFilter(Color.WHITE)
                    }
                    .addOnFailureListener {
                        Log.e("RestoranActivity", "Failed to toggle favorite status: ${it.message}")
                    }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("RestoranActivity", "Favorite data fetch cancelled: ${error.message}")
            }
        })
    }

    companion object {
        const val MERCHANT_ID = "MERCHANT_ID"
    }
    data class Favorit(val id_konsumen: String, val id_merchant: String)

}