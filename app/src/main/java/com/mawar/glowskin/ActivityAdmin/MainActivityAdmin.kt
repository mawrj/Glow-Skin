package com.mawar.glowskin.ActivityAdmin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.mawar.glowskin.Activity.LoginActivity
import com.mawar.glowskin.Adapter.SliderAdapter
import com.mawar.glowskin.Model.SliderModel
import com.mawar.glowskin.ViewModel.MainViewModel
import com.mawar.glowskin.Adapter.RecommendationAdapterAdmin
import com.mawar.glowskin.AdapterAdmin.CategoryAdapterAdmin
import com.mawar.glowskin.R
import com.mawar.glowskin.databinding.ActivityAdminBinding


class MainActivityAdmin : AppCompatActivity() {
    private val viewModel = MainViewModel()
    private lateinit var binding: ActivityAdminBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initBanners()
        initCategory()
        initRecommend()
        initBottomMenu()

        val addItemButton = findViewById<FloatingActionButton>(R.id.AddItem)
        addItemButton.setOnClickListener {
            val intent = Intent(this@MainActivityAdmin, AddItemActivityAdmin::class.java)
            startActivity(intent)
        }

    }

    private fun initBottomMenu() {
        binding.logout.setOnClickListener {
            startActivity(Intent(this@MainActivityAdmin, LoginActivity::class.java))
        }
    }

    private fun initBanners() {
        binding.progressBarBanner.visibility = View.VISIBLE
        viewModel.banner.observe(this, Observer {
            banners(it)
            binding.progressBarBanner.visibility = View.GONE
        })
        viewModel.error.observe(this, Observer { errorMessage ->
            if (errorMessage != null) {
                binding.progressBarBanner.visibility = View.GONE
                showError(errorMessage)
            }
        })
        viewModel.loadBanner()
    }

    private fun banners(it: List<SliderModel>) {
        binding.viewPagerSlider.adapter = SliderAdapter(it, binding.viewPagerSlider)
        binding.viewPagerSlider.clipToPadding = false
        binding.viewPagerSlider.clipChildren = false
        binding.viewPagerSlider.offscreenPageLimit = 3
        binding.viewPagerSlider.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER

        val compositePageTransformer = CompositePageTransformer().apply {
            addTransformer(MarginPageTransformer(40))
        }
        binding.viewPagerSlider.setPageTransformer(compositePageTransformer)

        if (it.size > 1) {
            binding.dotsIndicator.visibility = View.VISIBLE
            binding.dotsIndicator.attachTo(binding.viewPagerSlider)
        }
    }

    private fun initCategory() {
        binding.progressBarCategory.visibility = View.VISIBLE
        viewModel.category.observe(this, Observer {
            binding.ViewCategory.layoutManager =
                LinearLayoutManager(this@MainActivityAdmin, LinearLayoutManager.HORIZONTAL, false)
            binding.ViewCategory.adapter = CategoryAdapterAdmin(it)
            binding.progressBarCategory.visibility = View.GONE
        })
        viewModel.error.observe(this, Observer { errorMessage ->
            if (errorMessage != null) {
                binding.progressBarCategory.visibility = View.GONE
                showError(errorMessage)
            }
        })
        viewModel.loadCategory()
    }

    private fun initRecommend() {
        binding.progressBarRecomendation.visibility = View.VISIBLE
        viewModel.recommend.observe(this, Observer { items ->
            Log.d("MainActivity", "Jumlah rekomendasi: ${items.size}")
            binding.ViewRecommendation.layoutManager = GridLayoutManager(this@MainActivityAdmin, 2)
            binding.ViewRecommendation.adapter = RecommendationAdapterAdmin(items)
            binding.progressBarRecomendation.visibility = View.GONE
        })

        viewModel.error.observe(this, Observer { errorMessage ->
            if (errorMessage != null) {
                binding.progressBarRecomendation.visibility = View.GONE
                showError(errorMessage)
            }
        })
        viewModel.loadRecommend()
    }

    private fun showError(message: String) {
        Toast.makeText(this, "Error: $message", Toast.LENGTH_SHORT).show()
    }
}
