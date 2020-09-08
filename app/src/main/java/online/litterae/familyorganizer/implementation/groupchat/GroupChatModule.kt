package online.litterae.familyorganizer.implementation.groupchat

import dagger.Module
import dagger.Provides
import online.litterae.familyorganizer.dagger.GroupChatScope
import online.litterae.familyorganizer.sqlite.MyGroup

@Module
class GroupChatModule (val myGroup: MyGroup) {
    @GroupChatScope
    @Provides
    fun provideGroupChatPresenter() : GroupChatContract.Presenter {
        val presenter = GroupChatPresenter(myGroup)
        presenter.init()
        return presenter
    }

    @GroupChatScope
    @Provides
    fun provideGroupChatSqliteManager() : GroupChatContract.SqliteManager {
        val manager = GroupChatSqliteManager(myGroup)
        manager.init()
        return manager
    }

    @GroupChatScope
    @Provides
    fun provideGroupChatFirebaseManager() : GroupChatContract.FirebaseManager {
        val manager = GroupChatFirebaseManager(myGroup)
        manager.init()
        return manager
    }
}