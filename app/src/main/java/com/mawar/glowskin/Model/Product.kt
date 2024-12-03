package com.mawar.glowskin.Model

data class Product(
    val title: String,
    val description: String,
    val price: String,
    val rating: String,
    val picUrls: List<String>,
    val isRecommended: Boolean
)
