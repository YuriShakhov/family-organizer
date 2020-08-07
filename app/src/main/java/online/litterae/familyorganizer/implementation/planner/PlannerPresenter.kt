package online.litterae.familyorganizer.implementation.planner

import online.litterae.familyorganizer.abstracts.presenter.PagePresenter
import online.litterae.familyorganizer.application.MainApplication

class PlannerPresenter : PagePresenter<PlannerContract.View>(), PlannerContract.Presenter {
    override fun init() {
        MainApplication.getAppComponent().createPageComponent().inject(this)
    }
}