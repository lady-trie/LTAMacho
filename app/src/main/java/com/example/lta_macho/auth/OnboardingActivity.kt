package com.example.lta_macho.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.lta_macho.R
import com.example.lta_macho.databinding.ActivityOnboardingBinding

class OnboardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding
    private lateinit var onboardingAdapter: OnboardingAdapter
    private val onboardingItems = listOf(
        OnboardingItem(
            imageRes = R.drawable.ic_onboard_wash,
            title = "Operasional Jernih, Bisnis Lebih Terarah",
            description = "Manajemen cucian mobil, motor, dan karpet dalam satu aplikasi terintegrasi."
        ),
        OnboardingItem(
            imageRes = R.drawable.ic_onboard_receipt,
            title = "Catat Transaksi & Struk Digital",
            description = "Input cepat transaksi cucian dan kirim nota langsung via WhatsApp atau PDF."
        ),
        OnboardingItem(
            imageRes = R.drawable.ic_onboard_queue,
            title = "Pantau Antrean Real-Time",
            description = "Lacak status cucian dari Menunggu, Dicuci, hingga Siap Diambil dengan mudah."
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewPager()
        setupDots(0)

        binding.btnSkip.setOnClickListener {
            completeOnboarding()
        }

        binding.btnNext.setOnClickListener {
            val currentItem = binding.viewPagerOnboarding.currentItem
            if (currentItem < onboardingItems.size - 1) {
                binding.viewPagerOnboarding.currentItem = currentItem + 1
            }
        }

        binding.btnGetStarted.setOnClickListener {
            completeOnboarding()
        }
    }

    private fun setupViewPager() {
        onboardingAdapter = OnboardingAdapter(onboardingItems)
        binding.viewPagerOnboarding.adapter = onboardingAdapter

        binding.viewPagerOnboarding.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setupDots(position)
                if (position == onboardingItems.size - 1) {
                    binding.btnNext.visibility = View.GONE
                    binding.btnGetStarted.visibility = View.VISIBLE
                } else {
                    binding.btnNext.visibility = View.VISIBLE
                    binding.btnGetStarted.visibility = View.GONE
                }
            }
        })
    }

    private fun setupDots(position: Int) {
        binding.layoutDots.removeAllViews()
        val dots = arrayOfNulls<ImageView>(onboardingItems.size)

        for (i in dots.indices) {
            dots[i] = ImageView(this)
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(8, 0, 8, 0)
            dots[i]?.layoutParams = params

            if (i == position) {
                dots[i]?.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.dot_active))
            } else {
                dots[i]?.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.dot_inactive))
            }

            binding.layoutDots.addView(dots[i])
        }
    }

    private fun completeOnboarding() {
        val sharedPref = getSharedPreferences("user_pref", MODE_PRIVATE)
        sharedPref.edit().putBoolean("isOnboardingCompleted", true).apply()

        val intent = Intent(this, AuthActivity::class.java)
        startActivity(intent)
        finish()
    }
}