package com.mawar.glowskin.Activity

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.mawar.glowskin.Adapter.ListItemsAdapter
import com.mawar.glowskin.R
import com.mawar.glowskin.ViewModel.MainViewModel
import com.mawar.glowskin.databinding.ActivityListItemsBinding

class ListItemsActivity : BaseActivity() {
    private lateinit var binding: ActivityListItemsBinding
    private var viewModel= MainViewModel()
    private var id:String=""
    private var title:String=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListItemsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getBundle()
        initList()
        initBackButton()
    }

    private fun initList() {
        binding.apply {
            progressBarList.visibility= View.VISIBLE
            viewModel.recommend.observe(this@ListItemsActivity, Observer {
                viewList.layoutManager= GridLayoutManager(this@ListItemsActivity, 2)
                viewList.adapter= ListItemsAdapter(it)
                progressBarList.visibility= View.GONE
            })
            viewModel.loadFiltered(id)
        }
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
}