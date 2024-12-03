package com.mawar.glowskin.ActivityAdmin

import android.os.Bundle
import android.text.InputFilter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.mawar.glowskin.Model.ItemsModel
import com.mawar.glowskin.databinding.ActivityAddItemBinding
import kotlinx.coroutines.tasks.await

class AddItemActivityAdmin : AppCompatActivity() {

    private lateinit var binding: ActivityAddItemBinding
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inisialisasi binding
        binding = ActivityAddItemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inisialisasi Firebase Database reference
        database = FirebaseDatabase.getInstance("https://gloweskinn-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Items")

        // Batasi jumlah karakter maksimal pada judul
        val maxTitleLength = 30
        binding.etTitle.filters = arrayOf(InputFilter.LengthFilter(maxTitleLength))

        // Menyambungkan event listener untuk tombol Simpan
        binding.btnSave.setOnClickListener {
            val title = binding.etTitle.text.toString()
            val description = binding.etDescription.text.toString()
            val price = binding.etPrice.text.toString().toDoubleOrNull() ?: 0.0
            val rating = binding.etRating.text.toString().toDoubleOrNull() ?: 0.0
            val picUrl = binding.etPicUrl.text.toString().split(",").map { it.trim() }.toList()
            val categoryId = binding.etCategoryId.text.toString() // Menambahkan kategori ID
            val showRecommended = binding.switchRecommended.isChecked

            // Validasi input
            if (title.isNotEmpty() && description.isNotEmpty() && picUrl.isNotEmpty() && categoryId.isNotEmpty()) {
                if (title.length > maxTitleLength) {
                    Toast.makeText(this, "Judul tidak boleh lebih dari $maxTitleLength karakter", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                // Generate ID secara otomatis menggunakan Firebase push
                val itemId = database.push().key ?: ""

                // Membuat objek ItemsModel dengan data yang diinputkan
                val item = ItemsModel(
                    id = itemId,  // Menggunakan ID yang dihasilkan secara otomatis
                    title = title,
                    description = description,
                    picUrl = ArrayList(picUrl),
                    price = price,
                    rating = rating,
                    showRecommended = showRecommended,
                    categoryId = categoryId // Menggunakan ID kategori yang diinputkan
                )

                // Menyimpan data ke Firebase Realtime Database
                lifecycleScope.launchWhenResumed {
                    try {
                        // Menambahkan data ke Firebase menggunakan ID yang dihasilkan
                        database.child(item.id).setValue(item).await()

                        // Menampilkan pesan sukses
                        Toast.makeText(this@AddItemActivityAdmin, "Item berhasil ditambahkan", Toast.LENGTH_SHORT).show()

                        // Reset form setelah berhasil
                        binding.etTitle.text.clear()
                        binding.etDescription.text.clear()
                        binding.etPrice.text.clear()
                        binding.etRating.text.clear()
                        binding.etPicUrl.text.clear()
                        binding.etCategoryId.text.clear()
                        binding.switchRecommended.isChecked = false

                    } catch (e: Exception) {
                        // Menampilkan pesan error
                        Toast.makeText(this@AddItemActivityAdmin, "Terjadi kesalahan: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                // Validasi jika ada field yang kosong
                Toast.makeText(this, "Harap lengkapi semua field", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
