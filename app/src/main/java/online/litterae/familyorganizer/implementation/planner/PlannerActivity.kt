package online.litterae.familyorganizer.implementation.planner

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import online.litterae.familyorganizer.application.MainApplication
import online.litterae.familyorganizer.R
import online.litterae.familyorganizer.abstracts.view.PageActivity
import javax.inject.Inject

class PlannerActivity : PageActivity(), PlannerContract.View {
    @Inject lateinit var presenter: PlannerContract.Presenter

    override fun init(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_planner)
        setBottomMenu()
        MainApplication.createPageComponent().inject(this)
        presenter.attach(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detach()
    }

    override fun setSelectedItem(bottomMenu: BottomNavigationView) {
        bottomMenu.selectedItemId = R.id.page_planner
    }
}