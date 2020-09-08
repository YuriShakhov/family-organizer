package online.litterae.familyorganizer.implementation.singlechat

import dagger.Module
import dagger.Provides
import online.litterae.familyorganizer.dagger.SingleChatScope
import online.litterae.familyorganizer.sqlite.MyFriend

@Module
class ChatModule (val myFriend: MyFriend) {
    @SingleChatScope
    @Provides
    fun provideChatPresenter() : ChatContract.Presenter {
        val presenter = ChatPresenter(myFriend)
        presenter.init()
        return presenter
    }

    @SingleChatScope
    @Provides
    fun provideChatSqliteManager() : ChatContract.SqliteManager {
        val manager = ChatSqliteManager(myFriend)
        manager.init()
        return manager
    }

    @SingleChatScope
    @Provides
    fun provideChatFirebaseManager() : ChatContract.FirebaseManager {
        val manager = ChatFirebaseManager(myFriend)
        manager.init()
        return manager
    }
}