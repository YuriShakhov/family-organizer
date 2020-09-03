package online.litterae.familyorganizer.firebase

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import dagger.Module
import dagger.Provides
import online.litterae.familyorganizer.application.Const
import online.litterae.familyorganizer.application.MainApplication
import online.litterae.familyorganizer.dagger.AppScope

@Module
class FirebaseModule {
    @Provides
    @AppScope
    fun provideAuth() : FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @AppScope
    fun provideDBReference() : DatabaseReference = FirebaseDatabase.getInstance().getReference()

    @Provides
    fun provideUser(mAuth : FirebaseAuth) : FirebaseUser = mAuth.currentUser!!

    @Provides
    fun provideEmail(user: FirebaseUser) : Email {
        return Email(user.email!!)
    }

    @Provides
    fun provideUserFirebaseKey(email: Email) : FirebaseKey {
        val preferences: SharedPreferences = MainApplication.getApplication().getSharedPreferences(
            Const.APP_PREFERENCES, Context.MODE_PRIVATE)
        val key = preferences.getString(email.toString(), null)
        if (key != null) {
            return FirebaseKey(key)
        } else {
            //the function should retrieve firebase key by email from FRD!!!
            return FirebaseKey("defaultKey")
        }
    }
}