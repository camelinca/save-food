package com.example.customersavefood

data class Menu(
    val id_menu: String,
    val nama: String,
    val foto: String,
    val harga: Double,
    val stok: Int,
    val diskon : Double,
    val deskripsi: String,
    val id_merchant : String,
    val id_kategori : String

){
    constructor() : this("", "", "", 0.0, 0, 0.0, "", "", "")
}
