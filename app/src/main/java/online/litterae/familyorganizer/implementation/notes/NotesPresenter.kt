package online.litterae.familyorganizer.implementation.notes

import online.litterae.familyorganizer.abstracts.presenter.PagePresenter
import online.litterae.familyorganizer.application.MainApplication

class NotesPresenter: PagePresenter<NotesContract.View>(), NotesContract.Presenter {
    override fun init() {
        MainApplication.getAppComponent().createPageComponent().inject(this)
    }
}