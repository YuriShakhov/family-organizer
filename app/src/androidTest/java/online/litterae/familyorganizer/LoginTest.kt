package online.litterae.familyorganizer

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.delay
import online.litterae.familyorganizer.application.Const
import online.litterae.familyorganizer.application.Const.Companion.TAG
import online.litterae.familyorganizer.application.MainApplication
import online.litterae.familyorganizer.implementation.login.LoginPresenter
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import java.time.Duration

@RunWith(AndroidJUnit4::class)
class LoginTest {
    val appContext = InstrumentationRegistry.getInstrumentation().targetContext
    val loginPresenter = LoginPresenter()

    fun saveMyFirebaseKeyToSharedPrefs(key: String, email: String) {
        val preferences: SharedPreferences = appContext.getSharedPreferences(
            Const.APP_PREFERENCES, Context.MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putString(email, key)
        editor.apply()
    }

//    @Test
    fun saveMyFirebaseKeyToSharedPrefsTest() {
        val testKey = "testKey"
        val testEmail = "testEmail"
//        loginPresenter.saveMyFirebaseKeyToSharedPrefs(testKey, testEmail)
        saveMyFirebaseKeyToSharedPrefs(testKey, testEmail)
        Thread.sleep(500)
        val preferences: SharedPreferences = appContext.getSharedPreferences(
            Const.APP_PREFERENCES, Context.MODE_PRIVATE)
        val getTestEmail = preferences.getString(testKey, "defValue")
        assertEquals(getTestEmail, testEmail)
        Log.e("MyTest", "getTestEmail: $getTestEmail, testEmail: $testEmail")
//        assertEquals("a", "a")
    }


}