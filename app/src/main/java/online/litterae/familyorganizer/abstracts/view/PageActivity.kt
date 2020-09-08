package online.litterae.familyorganizer.abstracts.view

import android.content.Intent
import com.google.android.material.bottomnavigation.BottomNavigationView
import online.litterae.familyorganizer.R
import online.litterae.familyorganizer.implementation.family.FamilyActivity
import online.litterae.familyorganizer.implementation.login.LoginActivity
import online.litterae.familyorganizer.implementation.notes.NotesActivity
import online.litterae.familyorganizer.implementation.planner.PlannerActivity
import online.litterae.familyorganizer.implementation.shopping.ShoppingActivity

abstract class PageActivity : BaseCompatActivity() {
    protected fun setBottomMenu () {
        val bottomMenu: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomMenu.setOnNavigationItemSelectedListener { item ->
            when(item.itemId) {
                R.id.page_planner -> {
                    if (this !is PlannerActivity) {
                        val intent = Intent(this, PlannerActivity::class.java)
                        startActivity(intent)
                    }
                    true
                }
                R.id.page_shopping -> {
                    if (this !is ShoppingActivity) {
                        val intent = Intent(this, ShoppingActivity::class.java)
                        startActivity(intent)
                    }
                    true
                }
                R.id.page_family -> {
                    if (this !is FamilyActivity) {
                        val intent = Intent(this, FamilyActivity::class.java)
                        startActivity(intent)
                    }
                    true
                }
                R.id.page_notes -> {
                    if (this !is NotesActivity) {
                        val intent = Intent(this, NotesActivity::class.java)
                        startActivity(intent)
                    }
                    true
                }
                else -> false
            }
        }
        setSelectedItem(bottomMenu)
    }

    protected abstract fun setSelectedItem(bottomMenu: BottomNavigationView)

    fun backToLoginActivity () {
        startActivity(Intent(this, LoginActivity::class.java))
    }
}