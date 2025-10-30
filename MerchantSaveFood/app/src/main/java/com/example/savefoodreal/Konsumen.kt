package com.example.savefoodreal


class Konsumen(
    val id_konsumen: String,
    val nama: String,
    val email: String,
    val noHP: String,
    var foto: String? = null,
    var alamat: String? = null
){
    constructor() : this("", "", "", "", "", "")
    fun copy(
        id_konsumen: String = this.id_konsumen,
        nama: String = this.nama,
        email: String = this.email,
        noHP: String = this.noHP,
        foto: String? = this.foto,
        alamat: String? = this.alamat
    ): Konsumen{
        return Konsumen(id_konsumen, nama, email, noHP, foto, alamat)
    }
}