package com.mawar.glowskin.Activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.mawar.glowskin.Model.UserModel
import com.mawar.glowskin.databinding.ActivitySignupBinding

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.signupButton.setOnClickListener {
            val username = binding.signupUsername.text.toString()
            val email = binding.signupEmail.text.toString()
            val password = binding.signupPassword.text.toString()

            if (username.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            val userId = FirebaseAuth.getInstance().currentUser?.uid

                            // Membuat objek ModelUser dengan password
                            val user = UserModel(userId!!, username, password)

                            // Simpan data pengguna ke Realtime Database
                            val database = FirebaseDatabase.getInstance("https://gloweskinn-default-rtdb.asia-southeast1.firebasedatabase.app/")
                            val userRef = database.getReference("Pengguna")
                            userRef.child(userId).setValue(user)
                                .addOnSuccessListener {
                                    // Kirim email verifikasi (hanya jika akun berhasil dibuat)
                                    val user = FirebaseAuth.getInstance().currentUser
                                    user?.sendEmailVerification()
                                        ?.addOnCompleteListener { task ->
                                            if (task.isSuccessful) {
                                                Toast.makeText(this,
                                                    "Registrasi berhasil!", Toast.LENGTH_LONG).show()
                                                val intent = Intent(this, MainActivity::class.java)
                                                startActivity(intent)
                                                finish()
                                            } else {
                                                Toast.makeText(this, "Gagal mengirim email verifikasi", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                }
                                .addOnFailureListener {
                                    Toast.makeText(this, "Gagal menyimpan data pengguna", Toast.LENGTH_SHORT).show()
                                }
                        } else {
                            // Menampilkan pesan error dengan detail
                            task.exception?.localizedMessage?.let { Toast.makeText(this, it, Toast.LENGTH_SHORT).show() }
                        }
                    }
            } else {
                Toast.makeText(this, "Data tidak boleh ada yang kosong", Toast.LENGTH_SHORT).show()
            }
        }


        binding.loginRedirectText.setOnClickListener {
            startActivity(Intent(this,LoginActivity::class.java))
            finish()
        }
    }

}