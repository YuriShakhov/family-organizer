package online.litterae.familyorganizer.abstracts.firebase

import online.litterae.familyorganizer.abstracts.presenter.BasePresenterInterface
import online.litterae.familyorganizer.abstracts.view.BaseViewInterface

interface BaseFirebaseManagerInterface<P: BasePresenterInterface<out BaseViewInterface>> {
    fun init()
    fun attach(presenter: P)
}