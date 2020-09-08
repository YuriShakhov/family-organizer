package online.litterae.familyorganizer.abstracts.presenter

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import online.litterae.familyorganizer.abstracts.view.BaseViewInterface
import javax.inject.Inject

abstract class BasePresenter<V: BaseViewInterface> : BasePresenterInterface<V> {
    @Inject lateinit var mAuth : FirebaseAuth
    @Inject lateinit var dbReference : DatabaseReference

    protected var view: V? = null

    override fun attach(view: V) {
        this.view = view
    }

    override fun detach() {
        this.view = null
    }
}