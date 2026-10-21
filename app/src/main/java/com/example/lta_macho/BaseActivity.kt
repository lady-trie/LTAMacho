package com.example.lta_macho

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.lta_macho.databinding.ActivityBaseBinding
import com.example.lta_macho.pertemuan_7.HelpFragment
import com.example.lta_macho.pertemuan_7.HomeFragment
import com.example.lta_macho.pertemuan_7.OrdersFragment
import com.example.lta_macho.pertemuan_7.ProfileFragment

open class BaseActivity : AppCompatActivity() {

    protected lateinit var baseBinding: ActivityBaseBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        baseBinding = ActivityBaseBinding.inflate(layoutInflater)
        setContentView(baseBinding.root)

        ViewCompat.setOnApplyWindowInsetsListener(baseBinding.mainBase) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupBottomNavigation()

        if (savedInstanceState == null) {
            loadFragment(HomeFragment())
        }
    }

    private fun setupBottomNavigation() {
        baseBinding.bottomNavigation.setOnItemSelectedListener { item ->
            val fragment: Fragment = when (item.itemId) {
                R.id.navigation_home -> HomeFragment()
                R.id.navigation_orders -> OrdersFragment()
                R.id.navigation_help -> HelpFragment()
                R.id.navigation_profile -> ProfileFragment()
                else -> HomeFragment()
            }
            loadFragment(fragment)
            true
        }
    }

    protected fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    fun setToolbarTitle(title: String) {
        baseBinding.toolbar.title = title
    }
}