package online.litterae.familyorganizer.abstracts.presenter

import online.litterae.familyorganizer.abstracts.view.BaseViewInterface

interface BasePresenterInterface<V: BaseViewInterface> {
    fun attach(view: V)
    fun detach()
    fun init()
}