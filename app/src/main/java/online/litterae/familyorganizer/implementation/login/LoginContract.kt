package online.litterae.familyorganizer.implementation.login

import online.litterae.familyorganizer.abstracts.presenter.BasePresenterInterface
import online.litterae.familyorganizer.abstracts.view.BaseViewInterface

interface LoginContract {
    interface Presenter : BasePresenterInterface<LoginContract.View> {
        fun checkIfLoggedIn()
        fun signIn(email: String, password: String)
        fun register(email: String, password: String)
    }
    interface View : BaseViewInterface {
        fun enter()
        fun showErrorMessage(message: String)
    }
}