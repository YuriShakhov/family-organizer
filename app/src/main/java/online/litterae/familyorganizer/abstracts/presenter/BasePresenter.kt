package online.litterae.familyorganizer.abstracts.presenter

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import online.litterae.familyorganizer.abstracts.view.BaseViewInterface
import online.litterae.familyorganizer.firebase.Email
import online.litterae.familyorganizer.firebase.FirebaseKey
import javax.inject.Inject

abstract class BasePresenter<V: BaseViewInterface> : BasePresenterInterface<V> {
    @Inject lateinit var mAuth : FirebaseAuth
    @Inject lateinit var dbReference : DatabaseReference

    protected var view: V? = null
        private set

    override var isAttached = view != null

    override fun attach(view: V) {
        this.view = view
        init()
    }

    override fun detach() {
        this.view = null
    }
}