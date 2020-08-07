package online.litterae.familyorganizer.implementation.planner

import online.litterae.familyorganizer.abstracts.presenter.BasePresenterInterface
import online.litterae.familyorganizer.abstracts.view.BaseViewInterface

interface PlannerContract {
    interface Presenter: BasePresenterInterface<PlannerContract.View>
    interface View: BaseViewInterface
}