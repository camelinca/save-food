package com.example.customersavefood

data class Pesanan(
    val id_order: String,
    val tanggal_pesanan : String,
    val total_harga: Double,
    val status:String,
    val id_konsumen : String,
    val id_merchant: String,
){
    constructor() : this("","", 0.0, "", "","")
}
