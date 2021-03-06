package online.litterae.familyorganizer.implementation.login

import android.content.Context
import android.content.SharedPreferences
import online.litterae.familyorganizer.application.MainApplication
import online.litterae.familyorganizer.abstracts.presenter.BasePresenter
import online.litterae.familyorganizer.application.Const
import online.litterae.familyorganizer.application.Const.Companion.ERROR_AUTHORIZATION_FAILED
import online.litterae.familyorganizer.application.Const.Companion.ERROR_CREATE_PROFILE
import online.litterae.familyorganizer.application.Const.Companion.ERROR_REGISTRATION_FAILED
import online.litterae.familyorganizer.firebase.FirebaseProfile
import java.util.HashMap

class LoginPresenter : BasePresenter<LoginContract.View>(), LoginContract.Presenter {
    override fun init () {
        MainApplication.getAppComponent().createLoginComponent().inject(this)
    }

    override fun checkIfLoggedIn () {
        if (mAuth.currentUser != null) {
            view?.enter()
        }
    }

    override fun signIn (email: String, password: String) {
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    view?.enter()
                } else {
                    view?.showErrorMessage(ERROR_AUTHORIZATION_FAILED)
                }
        }
    }

    override fun register (email: String, password: String) {
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    createProfile(email)
                    view?.enter()
                } else {
                    view?.showErrorMessage(ERROR_REGISTRATION_FAILED)
                }
            }
    }

    private fun createProfile (email: String) {
        val key: String? = dbReference.child(Const.TABLE_PROFILES).push().key
        if (key != null) {
            val profileMap: Map<String, Any> = FirebaseProfile(email).toMap()
            val insertProfile: MutableMap<String, Any> = HashMap()
            insertProfile["/${Const.TABLE_PROFILES}/$key"] = profileMap
            dbReference.updateChildren(insertProfile)
            saveMyFirebaseKeyToSharedPrefs(key, email)
        } else {
            view?.showErrorMessage(ERROR_CREATE_PROFILE)
        }
    }

    private fun saveMyFirebaseKeyToSharedPrefs(key: String, email: String) {
        val preferences: SharedPreferences = MainApplication.getApplication().getSharedPreferences(
            Const.APP_PREFERENCES, Context.MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putString(email, key)
        editor.apply()
    }
}