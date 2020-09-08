package online.litterae.familyorganizer.implementation.shopping

import online.litterae.familyorganizer.abstracts.presenter.PagePresenter
import online.litterae.familyorganizer.application.MainApplication

class ShoppingPresenter: PagePresenter<ShoppingContract.View>(), ShoppingContract.Presenter {
    override fun init() {
        MainApplication.createPageComponent().inject(this)
    }

    override fun setNotifications(count: Int) {
    }
}