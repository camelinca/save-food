package com.example.savefoodreal

data class Menu(
    val id_menu: String? = "",
    val nama: String? = "",
    val foto: String? = "",
    val deskripsi: String? = "",
    val harga: Double = 0.0,
    val diskon: Double = 0.0,
    val stok: Int? = 0,
    val id_merchant: String? = "",
    val id_kategori: String? = ""
)