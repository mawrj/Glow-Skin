package com.mawar.glowskin.ViewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.mawar.glowskin.Model.CategoryModel
import com.mawar.glowskin.Model.ItemsModel
import com.mawar.glowskin.Model.SliderModel

class MainViewModel : ViewModel() {

    private val firebaseDatabase = FirebaseDatabase.getInstance("https://gloweskinn-default-rtdb.asia-southeast1.firebasedatabase.app/")

    private val _banner = MutableLiveData<List<SliderModel>>()
    private val _category = MutableLiveData<List<CategoryModel>>()
    private val _recommend = MutableLiveData<List<ItemsModel>>()

    private val _isLoading = MutableLiveData<Boolean>(false)
    private val _error = MutableLiveData<String?>()

    val banner: LiveData<List<SliderModel>> get() = _banner
    val category: LiveData<List<CategoryModel>> get() = _category
    val recommend: LiveData<List<ItemsModel>> get() = _recommend
    val isLoading: LiveData<Boolean> get() = _isLoading
    val error: LiveData<String?> get() = _error

    fun loadFiltered(id: String) {
        _isLoading.value = true
        val ref = firebaseDatabase.getReference("Items")
        val query: Query = ref.orderByChild("categoryId").equalTo(id)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lists = mutableListOf<ItemsModel>()
                for (childSnapshot in snapshot.children) {
                    val list = childSnapshot.getValue(ItemsModel::class.java)
                    list?.let { lists.add(it) }
                }
                _recommend.postValue(lists)
                _isLoading.postValue(false)
            }

            override fun onCancelled(error: DatabaseError) {
                _isLoading.postValue(false)
                _error.postValue("Error loading filtered data: ${error.message}")
            }
        })
    }

    fun loadRecommend() {
        val ref = firebaseDatabase.getReference("Items")
        val query: Query = ref.orderByChild("showRecommended").equalTo(true)
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lists = mutableListOf<ItemsModel>()
                for (childSnapshot in snapshot.children) {
                    val list = childSnapshot.getValue(ItemsModel::class.java)
                    list?.let { lists.add(it) }
                }
                Log.d("MainViewModel", "Recommended items count: ${lists.size}")
                _recommend.postValue(lists)  // Pembaruan data langsung
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("MainViewModel", "Error loading recommended data: ${error.message}")
            }
        })
    }



    fun loadCategory() {
        _isLoading.value = true
        val ref = firebaseDatabase.getReference("Category")
        ref.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val categoryList = mutableListOf<CategoryModel>()
                val snapshot = task.result
                for (childSnapshot in snapshot!!.children) {
                    val category = childSnapshot.getValue(CategoryModel::class.java)
                    category?.let { categoryList.add(it) }
                }
                _category.postValue(categoryList)
                _isLoading.postValue(false)
            } else {
                _isLoading.postValue(false)
                _error.postValue("Error loading categories: ${task.exception?.message}")
            }
        }
    }

    fun loadBanner() {
        _isLoading.value = true
        val bannerRef = firebaseDatabase.getReference("Banner")
        bannerRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val bannerList = mutableListOf<SliderModel>()
                val snapshot = task.result
                for (child in snapshot!!.children) {
                    val bannerData = child.getValue(SliderModel::class.java)
                    bannerData?.let { bannerList.add(it) }
                }
                _banner.postValue(bannerList)
                _isLoading.postValue(false)
            } else {
                _isLoading.postValue(false)
                _error.postValue("Error loading banners: ${task.exception?.message}")
            }
        }
    }
}
