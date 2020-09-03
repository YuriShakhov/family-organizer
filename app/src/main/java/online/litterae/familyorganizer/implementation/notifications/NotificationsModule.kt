package online.litterae.familyorganizer.implementation.notifications

import android.util.Log
import dagger.Module
import dagger.Provides
import online.litterae.familyorganizer.dagger.PageScope
import online.litterae.familyorganizer.implementation.family.FamilyContract
import online.litterae.familyorganizer.implementation.family.FamilyFirebaseManager
import online.litterae.familyorganizer.implementation.family.FamilyPresenter
import online.litterae.familyorganizer.implementation.family.FamilySqliteManager
import online.litterae.familyorganizer.sqlite.MyGroupDao
import online.litterae.familyorganizer.sqlite.MyNotificationDao
import javax.inject.Inject

@Module
class NotificationsModule {
    @PageScope
    @Provides
    fun provideNotificationsHolder(): NotificationsHolder {
        val notificationsHolder = NotificationsHolder()
        Log.e("SC*1", "provideNotificationsHolder: $notificationsHolder")
        return notificationsHolder
    }

    @PageScope
    @Provides
    fun provideNotificationsPresenter() : NotificationsContract.Presenter = NotificationsPresenter()

    @PageScope
    @Provides
    fun provideNotificationsSqliteManager() : NotificationsContract.SqliteManager {
        val manager = NotificationsSqliteManager()
        manager.init()
        return manager
    }

    @PageScope
    @Provides
    fun provideNotificationsFirebaseManager() : NotificationsContract.FirebaseManager {
        val manager = NotificationsFirebaseManager()
        manager.init()
        return manager
    }
}