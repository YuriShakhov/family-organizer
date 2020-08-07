package online.litterae.familyorganizer.abstracts.presenter

import online.litterae.familyorganizer.abstracts.view.BaseViewInterface

interface BasePresenterInterface<V: BaseViewInterface> {
    var isAttached: Boolean
    fun attach(view: V)
    fun detach()
    fun init()
}