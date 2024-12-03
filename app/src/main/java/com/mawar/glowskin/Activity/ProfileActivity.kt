package com.mawar.glowskin.Activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mawar.glowskin.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set text untuk profile dan nama
        binding.textView7.text = "Profile"
        binding.textView9.text = "Mawar Julia"
        binding.textView11.text = "Common Function"

        // Fungsi untuk tombol kembali
        binding.imageView2.setOnClickListener {
            onBackPressed()
        }

        // Fungsi untuk tombol logout
        binding.btnlogout.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Fungsi untuk tombol Cart
        binding.atas.setOnClickListener {
            val intent = Intent(this, CartActivity::class.java)
            startActivity(intent)
        }
    }
}
