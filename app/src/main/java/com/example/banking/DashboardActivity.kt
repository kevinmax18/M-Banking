package com.example.banking

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomnavigation.BottomNavigationView

class DashboardActivity : AppCompatActivity() {

    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var sessionManager: SessionManager
    private var currentUsername: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        sessionManager = SessionManager(this)
        if (!sessionManager.isLoggedIn()) {
            navigateToLogin()
            return
        }

        currentUsername = intent.getStringExtra("EXTRA_USERNAME") ?: sessionManager.getUsername() ?: ""

        setContentView(R.layout.activity_dashboard)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        bottomNavigation = findViewById(R.id.bottomNavigation)

        if (savedInstanceState == null) {
            loadTabFragment(R.id.menu_home)
        }

        bottomNavigation.setOnItemSelectedListener { item ->
            if (bottomNavigation.selectedItemId == item.itemId && supportFragmentManager.backStackEntryCount == 0) {
                return@setOnItemSelectedListener false
            }
            loadTabFragment(item.itemId)
            true
        }

        supportFragmentManager.registerFragmentLifecycleCallbacks(object : FragmentManager.FragmentLifecycleCallbacks() {
            override fun onFragmentResumed(fm: FragmentManager, f: Fragment) {
                super.onFragmentResumed(fm, f)
                updateToolbarForFragment(f)
            }
        }, false)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (supportFragmentManager.backStackEntryCount > 0) {
                    supportFragmentManager.popBackStack()
                } else if (bottomNavigation.selectedItemId != R.id.menu_home) {
                    bottomNavigation.selectedItemId = R.id.menu_home
                } else {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                    isEnabled = true
                }
            }
        })
    }

    private fun updateToolbarForFragment(fragment: Fragment) {
        val backStackCount = supportFragmentManager.backStackEntryCount
        val isSecondary = backStackCount > 0
        
        supportActionBar?.setDisplayHomeAsUpEnabled(isSecondary)
        
        when (fragment) {
            is HomeFragment -> {
                supportActionBar?.title = ""
            }
            is HistoryFragment -> {
                supportActionBar?.title = getString(R.string.title_history)
            }
            is QrFragment -> {
                supportActionBar?.title = getString(R.string.title_qr)
            }
            is ProfileFragment -> {
                supportActionBar?.title = getString(R.string.title_profile)
            }
            is NotificationFragment -> {
                supportActionBar?.title = getString(R.string.title_notification)
            }
            is SupportFragment -> {
                supportActionBar?.title = getString(R.string.title_support)
            }
        }
        invalidateOptionsMenu()
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }

    private fun loadTabFragment(itemId: Int) {
        // Clear back stack when switching tabs
        supportFragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)

        val fragment = when (itemId) {
            R.id.menu_home -> HomeFragment()
            R.id.menu_history -> HistoryFragment()
            R.id.menu_qr -> QrFragment()
            R.id.menu_profile -> ProfileFragment()
            else -> HomeFragment()
        }

        // Pass username to fragments if needed
        val args = Bundle()
        args.putString("ARG_USERNAME", currentUsername)
        fragment.arguments = args

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    fun navigateToSecondary(fragment: Fragment) {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer)
        if (currentFragment != null && currentFragment::class == fragment::class) {
            return
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun updateToolbarState() {
        // Obsolete: Handled by FragmentLifecycleCallbacks
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer)
        // Hide overflow on secondary fragments
        if (currentFragment is NotificationFragment || currentFragment is SupportFragment) {
            return false
        }

        menuInflater.inflate(R.menu.dashboard_option_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                supportFragmentManager.popBackStack()
                return true
            }
            R.id.menu_notification -> {
                navigateToSecondary(NotificationFragment())
                return true
            }
            R.id.menu_support -> {
                navigateToSecondary(SupportFragment())
                return true
            }
            R.id.menu_language -> {
                showLanguageDialog()
                return true
            }
            R.id.menu_logout -> {
                performLogout()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun performLogout() {
        AlertDialog.Builder(this)
            .setTitle(R.string.logout_confirm_title)
            .setMessage(R.string.logout_confirm_message)
            .setPositiveButton(R.string.logout_confirm_ok) { _, _ ->
                sessionManager.clearSession()
                navigateToLogin()
            }
            .setNegativeButton(R.string.logout_confirm_cancel, null)
            .show()
    }

    fun showLanguageDialog() {
        val languages = arrayOf("Bahasa Indonesia", "English")
        val localeTags = arrayOf("id", "en")
        
        val currentLocale = androidx.appcompat.app.AppCompatDelegate.getApplicationLocales()[0]?.language ?: "id"
        val checkedItem = if (currentLocale == "en") 1 else 0

        com.google.android.material.dialog.MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.menu_language))
            .setSingleChoiceItems(languages, checkedItem) { dialog, which ->
                val appLocale: androidx.core.os.LocaleListCompat = androidx.core.os.LocaleListCompat.forLanguageTags(localeTags[which])
                androidx.appcompat.app.AppCompatDelegate.setApplicationLocales(appLocale)
                dialog.dismiss()
            }
            .show()
    }
}