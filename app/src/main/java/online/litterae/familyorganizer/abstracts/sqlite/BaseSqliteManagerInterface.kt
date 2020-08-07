package online.litterae.familyorganizer.abstracts.sqlite

import online.litterae.familyorganizer.abstracts.presenter.BasePresenterInterface
import online.litterae.familyorganizer.abstracts.view.BaseViewInterface

interface BaseSqliteManagerInterface<P: BasePresenterInterface<out BaseViewInterface>> {
    fun init()
    fun attach(presenter: P)
}