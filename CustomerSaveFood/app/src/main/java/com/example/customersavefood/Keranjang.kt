package com.example.customersavefood

data class Keranjang(
    val id_keranjang: String,
    val jumlah: Int,
    val total: Double,
    val id_menu : String,
    val id_konsumen : String,
    val id_merchant : String,
    val nama_menu: String,
    val gambar_menu : String

){
    constructor() : this("", 0, 0.0, "", "", "","","")
}
