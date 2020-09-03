package online.litterae.familyorganizer.abstracts.presenter

import online.litterae.familyorganizer.abstracts.view.BaseViewInterface
import online.litterae.familyorganizer.abstracts.view.PageActivity
import online.litterae.familyorganizer.application.MainApplication
import online.litterae.familyorganizer.firebase.Email
import online.litterae.familyorganizer.firebase.FirebaseKey
import online.litterae.familyorganizer.implementation.notifications.NotificationsHolder
import javax.inject.Inject

abstract class PagePresenter<V: BaseViewInterface> : BasePresenter<V>() {
    @Inject
    lateinit var email: Email
    @Inject
    lateinit var firebaseKey: FirebaseKey
    @Inject
    lateinit var notificationsHolder: NotificationsHolder

    override fun init() {}

    abstract fun setNotifications(number: Int)

    fun logout () {
        MainApplication.releasePageComponent()
        mAuth.signOut()
        (view as PageActivity).backToLoginActivity()
    }
}