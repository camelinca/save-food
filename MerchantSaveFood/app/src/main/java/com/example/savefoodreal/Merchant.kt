package com.example.savefoodreal

class Merchant(
    val id_merchant: String,
    val nama: String,
    val email: String,
    val noHP: String,
    var foto: String? = null,
    var alamat: String? = null
){
    // Add a no-argument constructor
    constructor() : this("", "", "", "", "", "")
    fun copy(
        id_merchant: String = this.id_merchant,
        nama: String = this.nama,
        email: String = this.email,
        noHP: String = this.noHP,
        foto: String? = this.foto,
        alamat: String? = this.alamat
    ): Merchant {
        return Merchant(id_merchant, nama, email, noHP, foto, alamat)
    }
}