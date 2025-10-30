package com.example.savefoodreal

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.customersavefood.PesananFragment
import com.example.savefoodreal.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(MenuFragment())
        binding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId){
                R.id.menu->replaceFragment(MenuFragment())
                R.id.pesanan->replaceFragment(PesananFragment())
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
                "menu" -> replaceFragment(MenuFragment())
                "pesanan" -> replaceFragment(PesananFragment())
            }
        }

    }

    private fun replaceFragment(fragment : Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout,fragment)
        fragmentTransaction.commit()
    }
}