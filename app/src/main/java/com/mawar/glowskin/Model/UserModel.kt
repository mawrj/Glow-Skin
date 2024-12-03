package com.mawar.glowskin.Model

data class UserModel(
    val userId: String,
    val username: String,
    val password: String
){
    constructor() : this("", "", "")
}
