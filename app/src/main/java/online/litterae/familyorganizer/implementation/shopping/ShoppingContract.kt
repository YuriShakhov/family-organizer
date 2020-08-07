package online.litterae.familyorganizer.implementation.shopping

import online.litterae.familyorganizer.abstracts.presenter.BasePresenterInterface
import online.litterae.familyorganizer.abstracts.view.BaseViewInterface

interface ShoppingContract {
    interface Presenter: BasePresenterInterface<ShoppingContract.View>
    interface View: BaseViewInterface
}