package online.litterae.familyorganizer.implementation.planner

import dagger.Module
import dagger.Provides
import online.litterae.familyorganizer.dagger.PageScope

@Module
class PlannerModule {
    @PageScope
    @Provides
    fun providePlannerPresenter () : PlannerContract.Presenter {
        val presenter = PlannerPresenter()
        presenter.init()
        return presenter
    }
}