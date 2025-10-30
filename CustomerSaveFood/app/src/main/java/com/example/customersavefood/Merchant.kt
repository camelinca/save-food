package com.example.customersavefood

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
}