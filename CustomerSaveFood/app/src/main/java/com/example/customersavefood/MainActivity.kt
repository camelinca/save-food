package com.example.customersavefood

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.customersavefood.R
import com.example.customersavefood.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(EksplorFragment())
        binding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId){
                R.id.eksplor->replaceFragment(EksplorFragment())
                R.id.pesanan->replaceFragment(PesananFragment())
                R.id.koleksi->replaceFragment(KoleksiFragment())
                R.id.profil->replaceFragment(ProfilFragment())

                else->{

                }
            }
            true
        }
        val fragmentToLoad = intent.getStringExtra("fragmentToLoad")
        if (fragmentToLoad != null) {
            when (fragmentToLoad) {
                "profil" -> replaceFragment(ProfilFragment())
                "eksplor" -> replaceFragment(EksplorFragment())
                "pesanan" -> replaceFragment(PesananFragment())
                "koleksi" -> replaceFragment(KoleksiFragment())
            }
        }

//        val intent = Intent(this@KeranjangActivity, MainActivity::class.java)
//        intent.putExtra("selectedFragment", PesananFragment::class.java.name)
//        startActivity(intent)
//        finish()


    }


    private fun replaceFragment(fragment: Fragment) {
        if (!fragment.isAdded) {
            val fragmentManager = supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.frame_layout, fragment)
            fragmentTransaction.commit()
        }
    }
}