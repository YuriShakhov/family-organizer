package online.litterae.familyorganizer.implementation.notifications

import dagger.Module
import dagger.Provides
import online.litterae.familyorganizer.dagger.PageScope

@Module
class NotificationsModule {
    @PageScope
    @Provides
    fun provideNotificationsHolder(): NotificationsHolder = NotificationsHolder()

    @PageScope
    @Provides
    fun provideNotificationsPresenter() : NotificationsContract.Presenter {
        val presenter = NotificationsPresenter()
        presenter.init()
        return presenter
    }

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