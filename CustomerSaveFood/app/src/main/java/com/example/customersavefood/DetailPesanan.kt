package com.example.customersavefood

data class DetailPesanan(
    val id_detail : String,
    val id_order: String,
    val id_menu : String,
    val jumlah: Int,
    val total_harga: Double
){
    constructor() : this("","", "", 0, 0.0)
}
