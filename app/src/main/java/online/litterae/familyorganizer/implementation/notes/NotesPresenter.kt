package online.litterae.familyorganizer.implementation.notes

import online.litterae.familyorganizer.abstracts.presenter.PagePresenter
import online.litterae.familyorganizer.application.MainApplication

class NotesPresenter: PagePresenter<NotesContract.View>(), NotesContract.Presenter {
    override fun init() {
        MainApplication.createPageComponent().inject(this)
        super.init()
    }

    override fun setNotifications(number: Int) {
    }
}