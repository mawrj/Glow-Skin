package com.mawar.glowskin.ActivityAdmin

import android.os.Bundle
import android.text.InputFilter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.mawar.glowskin.Model.ItemsModel
import com.mawar.glowskin.databinding.ActivityEditItemsAdminBinding

class EditItemsActivityAdmin : AppCompatActivity() {
    private lateinit var database: DatabaseReference

    // Initialize ViewBinding
    private lateinit var binding: ActivityEditItemsAdminBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize ViewBinding
        binding = ActivityEditItemsAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance("https://gloweskinn-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Items")

        // Get the idItem from Intent
        val idItem = intent.getStringExtra("idItem")

        println("ID Item diterima dari Intent: $idItem")

        // Set max length for title input
        binding.etTitle.filters = arrayOf(InputFilter.LengthFilter(30))

        // Load existing item data
        loadItemData(idItem.toString())

        // Save changes when save button is clicked
        binding.btnSave.setOnClickListener {
            if (binding.etTitle.text.toString().length > 50) {
                Toast.makeText(this, "Judul tidak boleh lebih dari 50 karakter", Toast.LENGTH_SHORT).show()
            } else {
                saveItemChanges(idItem.toString())
            }
        }
    }

    private fun loadItemData(idItem: String) {
        database.child(idItem).get().addOnSuccessListener { snapshot ->
            val item = snapshot.getValue(ItemsModel::class.java)
            if (item != null) {
                // Populate form fields
                binding.etTitle.setText(item.title)
                binding.etDescription.setText(item.description)
                binding.etPrice.setText(item.price.toString())
                binding.etRating.setText(item.rating.toString())
                binding.etPicUrl.setText(item.picUrl.joinToString(","))
                binding.etCategoryId.setText(item.categoryId)
                binding.switchRecommended?.isChecked = item.showRecommended
            } else {
                Toast.makeText(this, "Item tidak ditemukan", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener { exception ->
            Toast.makeText(this, "Gagal mengambil data: ${exception.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveItemChanges(idItem: String) {
        // Ambil data dari input
        val updatedTitle = binding.etTitle.text.toString().trim()
        val updatedDescription = binding.etDescription.text.toString().trim()
        val updatedPrice = binding.etPrice.text.toString().toDoubleOrNull() ?: 0.0
        val updatedRating = binding.etRating.text.toString().toDoubleOrNull() ?: 0.0
        val updatedPicUrl = binding.etPicUrl.text.toString().split(",").map { it.trim() } as ArrayList<String>
        val updatedCategoryId = binding.etCategoryId.text.toString().trim()
        val updatedShowRecommended = binding.switchRecommended!!.isChecked

        // Membuat objek baru
        val updatedItem = ItemsModel(
            id = idItem,
            title = updatedTitle,
            description = updatedDescription,
            picUrl = updatedPicUrl,
            price = updatedPrice,
            rating = updatedRating,
            showRecommended = updatedShowRecommended,
            categoryId = updatedCategoryId
        )

        // Update data pada node dengan ID item
        database.child(idItem).setValue(updatedItem).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Data berhasil diperbarui!", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Gagal memperbarui data: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener { exception ->
            Toast.makeText(this, "Gagal memperbarui data: ${exception.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
