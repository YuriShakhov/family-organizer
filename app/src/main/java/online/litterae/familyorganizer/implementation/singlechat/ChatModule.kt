package online.litterae.familyorganizer.implementation.singlechat

import dagger.Module
import dagger.Provides
import online.litterae.familyorganizer.dagger.PageScope
import online.litterae.familyorganizer.dagger.SingleChatScope
import online.litterae.familyorganizer.implementation.notifications.NotificationsContract
import online.litterae.familyorganizer.implementation.notifications.NotificationsFirebaseManager
import online.litterae.familyorganizer.implementation.notifications.NotificationsPresenter
import online.litterae.familyorganizer.implementation.notifications.NotificationsSqliteManager

@Module
class ChatModule (val friendFirebaseKey: String) {
    @SingleChatScope
    @Provides
    fun provideChatPresenter() : ChatContract.Presenter = ChatPresenter(friendFirebaseKey)

    @SingleChatScope
    @Provides
    fun provideChatSqliteManager() : ChatContract.SqliteManager {
        val manager = ChatSqliteManager(friendFirebaseKey)
        manager.init()
        return manager
    }

    @SingleChatScope
    @Provides
    fun provideChatFirebaseManager() : ChatContract.FirebaseManager {
        val manager = ChatFirebaseManager(friendFirebaseKey)
        manager.init()
        return manager
    }
}