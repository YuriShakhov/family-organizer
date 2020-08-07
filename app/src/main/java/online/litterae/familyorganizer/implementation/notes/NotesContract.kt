package online.litterae.familyorganizer.implementation.notes

import online.litterae.familyorganizer.abstracts.presenter.BasePresenterInterface
import online.litterae.familyorganizer.abstracts.view.BaseViewInterface

interface NotesContract {
    interface Presenter: BasePresenterInterface<NotesContract.View>
    interface View: BaseViewInterface
}