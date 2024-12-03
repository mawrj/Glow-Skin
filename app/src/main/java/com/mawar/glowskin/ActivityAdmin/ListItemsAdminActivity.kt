package com.mawar.glowskin.ActivityAdmin

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.mawar.glowskin.Activity.BaseActivity
import com.mawar.glowskin.AdapterAdmin.ListItemsAdminAdapter
import com.mawar.glowskin.ViewModel.MainViewModel
import com.mawar.glowskin.databinding.ActivityListItemsAdminBinding


class ListItemsAdminActivity : BaseActivity() {

    private lateinit var binding: ActivityListItemsAdminBinding
    private var viewModel= MainViewModel()
    private var id:String=""
    private var title:String=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListItemsAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getBundle()
        initList()
        initBackButton()

    }

    private fun getBundle() {
        id=intent.getStringExtra("id")!!
        title=intent.getStringExtra("title")!!

        binding.categoriTxt.text=title
    }

    private fun initBackButton() {
        binding.backBtn.setOnClickListener {
            onBackPressed()
        }
    }

    private fun initList() {
        binding.apply {
            progressBarList.visibility= View.VISIBLE
            viewModel.recommend.observe(this@ListItemsAdminActivity, Observer {
                viewList.layoutManager= GridLayoutManager(this@ListItemsAdminActivity, 2)
                viewList.adapter= ListItemsAdminAdapter(it)
                progressBarList.visibility= View.GONE
            })
            viewModel.loadFiltered(id)
        }
    }
}