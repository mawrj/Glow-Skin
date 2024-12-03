package com.mawar.glowskin.Activity

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.mawar.glowskin.ActivityAdmin.MainActivityAdmin
import com.mawar.glowskin.R
import com.mawar.glowskin.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        // Login button click listener
        binding.loginButton.setOnClickListener {
            val email = binding.loginEmail.text.toString()
            val password = binding.loginPassword.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {

                if (email == "mawarjuliaku@gmail.com" && password == "1234567890") {
                    // Admin login successful, redirect to MainActivityAdmin
                    Toast.makeText(this, "Login Admin Berhasil", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@LoginActivity, MainActivityAdmin::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    // Firebase login for regular user
                    firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                        if (it.isSuccessful) {
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            finish() // Ensure the user cannot return to the login screen
                        } else {
                            // Handle login failure
                            Toast.makeText(this, "Login gagal: ${it.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } else {
                // If email or password is empty
                Toast.makeText(this, "Kolom tidak boleh kosong", Toast.LENGTH_SHORT).show()
            }
        }

        // Forget password click listener
        binding.forgetpassword.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            val view = layoutInflater.inflate(R.layout.alert_forget, null)
            val userEmail = view.findViewById<EditText>(R.id.editBox)

            builder.setView(view)
            val dialog = builder.create()

            view.findViewById<Button>(R.id.btnReset).setOnClickListener {
                compareEmail(userEmail)
                dialog.dismiss()
            }

            view.findViewById<Button>(R.id.btnCancel).setOnClickListener {
                dialog.dismiss()
            }

            if (dialog.window != null) {
                dialog.window!!.setBackgroundDrawable(ColorDrawable(0))
            }
            dialog.show()
        }

        // Redirect to SignupActivity
        binding.signupRedirectText.setOnClickListener {
            val signupIntent = Intent(this, SignupActivity::class.java)
            startActivity(signupIntent)
        }
    }

    // Function to compare email and send reset link
    private fun compareEmail(email: EditText) {
        val emailText = email.text.toString()
        if (emailText.isEmpty()) {
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
            Toast.makeText(this, "Email tidak valid", Toast.LENGTH_SHORT).show()
            return
        }

        firebaseAuth.sendPasswordResetEmail(emailText).addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(this, "Periksa Email Anda", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Gagal mengirim email reset: ${it.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
