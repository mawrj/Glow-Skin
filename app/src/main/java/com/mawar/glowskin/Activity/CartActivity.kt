package com.mawar.glowskin.Activity

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.glowskin.Helper.ChangeNumberItemsListener
import com.example.glowskin.Helper.ManagmentCart
import com.mawar.glowskin.Adapter.CartAdapter
import com.mawar.glowskin.databinding.ActivityCartBinding

class CartActivity : BaseActivity() {
    private lateinit var binding: ActivityCartBinding
    private lateinit var managmentCart: ManagmentCart
    private var tax: Double = 0.0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        managmentCart = ManagmentCart(this)

        setVisible()
        calculateCart()
        initCartList()
    }

    private fun initCartList() {
        with(binding) {
            emptyTxt.visibility =
                if (managmentCart.getListCart().isEmpty()) View.VISIBLE else View.GONE
            scrollView3.visibility =
                if (managmentCart.getListCart().isEmpty()) View.GONE else View.VISIBLE
        }

        binding.viewCart.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        binding.viewCart.adapter = CartAdapter(managmentCart.getListCart(), this, object :
            ChangeNumberItemsListener {
            override fun onChanged() {
                calculateCart()
            }
        })
    }

    private fun calculateCart() {
        val percentTax = 0.02
        val delivery = 10.0
        tax = Math.round(managmentCart.getTotalFee() * percentTax * 100) / 100.0
        val total = Math.round((managmentCart.getTotalFee() + tax + delivery) * 100) / 100
        val itemTotal = Math.round(managmentCart.getTotalFee() * 100) / 100

        with(binding) {
            totalFeeTxt.text = "$$itemTotal"
            taxTxt.text = "$$tax"
            deliveryTxt.text = "$$delivery"
            totalTxt.text = "$$total"
        }
    }

    private fun setVisible() {
        binding.backBtn.setOnClickListener { finish() }

        binding.button.setOnClickListener {
            showCheckoutConfirmation()
        }

    }

    private fun showCheckoutConfirmation() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Checkout Successful")
        builder.setMessage("Your checkout was successful. Thank you for your purchase!")
        builder.setPositiveButton("OK") { dialog, _ ->

            managmentCart.clearCart()
            initCartList()
            dialog.dismiss()

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        builder.setCancelable(false)
        builder.show()
    }
}
