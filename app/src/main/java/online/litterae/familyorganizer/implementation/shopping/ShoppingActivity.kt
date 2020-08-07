package online.litterae.familyorganizer.implementation.shopping

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import online.litterae.familyorganizer.application.MainApplication
import online.litterae.familyorganizer.R
import online.litterae.familyorganizer.abstracts.view.PageActivity
import javax.inject.Inject

class ShoppingActivity : PageActivity(), ShoppingContract.View {
    @Inject
    lateinit var presenter: ShoppingContract.Presenter

    override fun init(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_shopping)
        setBottomMenu()
        MainApplication.getAppComponent().createPageComponent().inject(this)
        presenter.attach(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detach()
    }
    override fun setSelectedItem(bottomMenu: BottomNavigationView) {
        bottomMenu.selectedItemId = R.id.page_shopping
    }
}