package online.litterae.familyorganizer.abstracts.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import online.litterae.familyorganizer.abstracts.presenter.BasePresenterInterface
import online.litterae.familyorganizer.abstracts.view.BaseViewInterface
import online.litterae.familyorganizer.firebase.Email
import online.litterae.familyorganizer.firebase.FirebaseKey
import javax.inject.Inject

abstract class BaseFirebaseManager<P: BasePresenterInterface<out BaseViewInterface>>: BaseFirebaseManagerInterface<P> {
    @Inject lateinit var mAuth : FirebaseAuth
    @Inject lateinit var dbReference : DatabaseReference
    @Inject lateinit var email: Email
    @Inject lateinit var firebaseKey: FirebaseKey

    protected var presenter: P? = null
        private set

    override fun attach(presenter: P) {
        init()
        this.presenter = presenter
    }
}