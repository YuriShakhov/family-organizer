package online.litterae.familyorganizer.application

import android.app.Application
import androidx.room.Room
import online.litterae.familyorganizer.dagger.AppComponent
import online.litterae.familyorganizer.dagger.DaggerAppComponent
import online.litterae.familyorganizer.dagger.PageComponent
import online.litterae.familyorganizer.sqlite.MyDatabase

class MainApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        mainApplication = this
        mainComponent = initDagger()
    }

    private fun initDagger(): AppComponent =
        DaggerAppComponent.create()

    companion object {
        lateinit var mainApplication: MainApplication
        lateinit var mainComponent: AppComponent
        private var pageComponent: PageComponent? = null
        private val databases: MutableMap<String, MyDatabase> = HashMap()

        fun getApplication() : MainApplication =
            mainApplication

        fun getAppComponent() : AppComponent =
            mainComponent

        fun createPageComponent() : PageComponent {
            if (pageComponent == null) {
                pageComponent = mainComponent.createPageComponent()
            }
            return pageComponent as PageComponent
        }

        fun releasePageComponent() {
            pageComponent = null
        }

        fun getDatabase(myEmail: String) : MyDatabase {
            val myDatabase = databases[myEmail]
            return if (myDatabase != null) {
                myDatabase
            } else {
                val newDatabase: MyDatabase = Room
                    .databaseBuilder(getApplication(), MyDatabase::class.java, myEmail)
                    .build()
                databases[myEmail] = newDatabase
                newDatabase
            }
        }
    }
}