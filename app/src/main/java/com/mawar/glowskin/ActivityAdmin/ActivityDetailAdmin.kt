package com.mawar.glowskin.ActivityAdmin

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.mawar.glowskin.Activity.BaseActivity
import com.mawar.glowskin.Adapter.SliderAdapter
import com.mawar.glowskin.Model.ItemsModel
import com.mawar.glowskin.Model.SliderModel
import com.mawar.glowskin.databinding.ActivityDetailAdminBinding


class ActivityDetailAdmin : BaseActivity() {

    private lateinit var binding: ActivityDetailAdminBinding
    private lateinit var item: ItemsModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getBundle()
        setupBanners()

        binding.deletebtn.setOnClickListener {
            // Menanyakan konfirmasi sebelum menghapus item
            showConfirmationDialog()
        }
    }

    private fun showConfirmationDialog() {
        // Menampilkan dialog konfirmasi sebelum menghapus item
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Apakah Anda yakin ingin menghapus produk ini?")
            .setCancelable(false)
            .setPositiveButton("Ya") { _, _ ->
                deleteItem()  // Memanggil fungsi deleteItem() jika "Ya" dipilih
            }
            .setNegativeButton("Tidak") { dialog, _ ->
                dialog.dismiss()  // Menutup dialog jika "Tidak" dipilih
            }
        val alert = builder.create()
        alert.show()
    }

    private fun setupBanners() {
        val sliderItems = ArrayList<SliderModel>()
        for (imageUrl in item.picUrl) {
            sliderItems.add(SliderModel(imageUrl))
        }

        binding.img.adapter = SliderAdapter(sliderItems, binding.img)
        binding.img.clipToPadding = false
        binding.img.clipChildren = false
        binding.img.offscreenPageLimit = 1
        binding.img.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER

        val compositePageTransformer = CompositePageTransformer().apply {
            addTransformer(MarginPageTransformer(40))
        }

        binding.img.setPageTransformer(compositePageTransformer)

        if (sliderItems.size > 1) {
            binding.dotsIndicator.visibility = View.VISIBLE
            binding.dotsIndicator.attachTo(binding.img)
        }
    }

    private fun deleteItem() {
        if (item.id.isEmpty()) {
            Toast.makeText(this, "ID produk tidak valid", Toast.LENGTH_SHORT).show()
            return
        }

        // Menghubungkan ke Firebase Realtime Database
        val database: DatabaseReference = FirebaseDatabase.getInstance("https://gloweskinn-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Items")

        // Menghapus item berdasarkan ID
        database.child(item.id).removeValue()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("DeleteItem", "Item berhasil dihapus")
                    Toast.makeText(this, "Produk berhasil dihapus", Toast.LENGTH_SHORT).show()
                    finish()  // Mengembalikan ke halaman sebelumnya setelah penghapusan
                } else {
                    Log.e("DeleteItem", "Gagal menghapus item", task.exception)
                    Toast.makeText(this, "Gagal menghapus produk", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Log.e("ActivityDetailAdmin", "Error saat menghapus item", exception)
                Toast.makeText(this, "Terjadi kesalahan: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun getBundle() {
        item = intent.getParcelableExtra("object")!!

        binding.titleTxt.text = item.title
        binding.descriptionTxt.text = item.description
        binding.priceTxt.text = "$${item.price}"
        binding.ratingTxt.text = "${item.rating} Rating"

        binding.backBtn.setOnClickListener { finish() }
        binding.edit.setOnClickListener {
            val editIntent = Intent(this@ActivityDetailAdmin, EditItemsActivityAdmin::class.java)
            editIntent.putExtra("object", item)  // Kirim data item untuk diedit
            editIntent.putExtra("idItem", item.id) // Kirim idItem ke Activity
            startActivity(editIntent)
        }
    }
}