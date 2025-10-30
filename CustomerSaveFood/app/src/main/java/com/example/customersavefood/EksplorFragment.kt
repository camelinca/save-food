package com.example.customersavefood

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EksplorFragment : Fragment() {

    private lateinit var databaseReference: DatabaseReference
    private lateinit var recyclerView: RecyclerView
    private lateinit var restaurantRecyclerView: RecyclerView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_eksplor, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.listMenu)

        val currentUser = FirebaseAuth.getInstance().currentUser
        val idKonsumen = currentUser?.uid ?: "1"
        databaseReference = FirebaseDatabase.getInstance().reference.child("menu")
        checkOrders(idKonsumen)

        restaurantRecyclerView = view.findViewById(R.id.listRestoran)
        displayRestaurantsFromDatabase()

        val koleksiButton: ImageButton = view.findViewById(R.id.btnKoleksi)
        koleksiButton.setOnClickListener {
            val intent = Intent(requireContext(), KeranjangActivity::class.java)
            startActivity(intent)
        }

    }

    private fun checkOrders(idKonsumen: String) {
        checkOrdersInFirebase(idKonsumen) { hasOrders ->
            if (hasOrders) {
                fetchRecommendations(idKonsumen)
            } else {
                displayMenuFromDatabase()
            }
        }
    }

    private fun checkOrdersInFirebase(idKonsumen: String, callback: (Boolean) -> Unit) {
        val ordersReference = FirebaseDatabase.getInstance().reference.child("pesanan")
        ordersReference.orderByChild("id_konsumen").equalTo(idKonsumen).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                callback(dataSnapshot.exists())
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("OrderDebug", "Error reading pesanan from the database: ${databaseError.message}")
                callback(false)
            }
        })
    }
    private fun fetchRecommendations(idKonsumen: String) {
        val apiService = ApiClient.create()
        val call = apiService.getRecommendations(mapOf("id_konsumen" to idKonsumen))

        call.enqueue(object : Callback<RecommendationsResponse> {
            override fun onResponse(
                call: Call<RecommendationsResponse>,
                response: Response<RecommendationsResponse>
            ) {
                if (response.isSuccessful) {
                    val recommendedMenuIds = response.body()?.recommended_menu_ids
                    if (!recommendedMenuIds.isNullOrEmpty()) {
                        val menuList = mutableListOf<Menu>()

                        for (menuId in recommendedMenuIds) {
                            getMenuFromFirebase(menuId) { menu ->
                                if (menu != null) {
                                    menuList.add(menu)
                                    displayMenuInRecyclerView(menuList)
                                } else {
                                    Log.e("MenuDebug", "Menu is null for menuId: $menuId")
                                }
                            }
                        }
                    }
                } else {
                    Log.e("API Error", "Failed API call with code: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<RecommendationsResponse>, t: Throwable) {
                Log.e("API Error", "Failed to make API call: ${t.message}")
            }
        })
    }
    private fun getMenuFromFirebase(idMenu: String, callback: (Menu?) -> Unit) {
        databaseReference.child(idMenu).get()
            .addOnSuccessListener { dataSnapshot ->
                if (dataSnapshot.exists()) {
                    val menu = dataSnapshot.getValue(Menu::class.java)
                    callback(menu)
                } else {
                    callback(null)
                }
            }
            .addOnFailureListener { exception ->
                callback(null)
            }
    }
    private fun displayMenuFromDatabase() {
        getAllMenusFromFirebase { menuList ->
            if (menuList.isNotEmpty()) {
                displayMenuInRecyclerView(menuList)
            } else {
                Log.e("MenuDebug", "No menus found in the database.")
            }
        }
    }
    private fun getAllMenusFromFirebase(callback: (List<Menu>) -> Unit) {
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val menuList = mutableListOf<Menu>()
                for (menuSnapshot in dataSnapshot.children) {
                    val menu = menuSnapshot.getValue(Menu::class.java)
                    menu?.let { menuList.add(it) }
                }
                callback(menuList)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle the error when reading from the database
                Log.e("MenuDebug", "Error reading menus from the database: ${databaseError.message}")
                callback(emptyList())
            }
        })
    }
    private fun displayMenuInRecyclerView(menuList: List<Menu>) {
        val recyclerView: RecyclerView = view?.findViewById(R.id.listMenu) ?: return
        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerView.layoutManager = layoutManager
        val adapter = MenuAdapter(menuList)
        recyclerView.adapter = adapter
    }
    private fun displayRestaurantsFromDatabase() {
        getAllRestaurantsFromFirebase { restaurantList ->
            if (restaurantList.isNotEmpty()) {
                displayRestaurantsInRecyclerView(restaurantList)
            } else {
                Log.e("RestaurantDebug", "No restaurants found in the database.")
            }
        }
    }
    private fun getAllRestaurantsFromFirebase(callback: (List<Merchant>) -> Unit) {
        val restaurantReference = FirebaseDatabase.getInstance().reference.child("merchant")
        restaurantReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val restaurantList = mutableListOf<Merchant>()
                for (restaurantSnapshot in dataSnapshot.children) {
                    val restaurant = restaurantSnapshot.getValue(Merchant::class.java)
                    restaurant?.let { restaurantList.add(it) }
                }
                callback(restaurantList)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("RestaurantDebug", "Error reading restaurants from the database: ${databaseError.message}")
                callback(emptyList())
            }
        })
    }
    private fun displayRestaurantsInRecyclerView(restaurantList: List<Merchant>) {
        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        restaurantRecyclerView.layoutManager = layoutManager
        val adapter = MerchantAdapter(restaurantList)
        restaurantRecyclerView.adapter = adapter
    }
}