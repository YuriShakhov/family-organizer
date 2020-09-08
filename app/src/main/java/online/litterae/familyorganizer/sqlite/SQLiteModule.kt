package online.litterae.familyorganizer.sqlite

import dagger.Module
import dagger.Provides
import online.litterae.familyorganizer.application.MainApplication
import online.litterae.familyorganizer.dagger.PageScope
import online.litterae.familyorganizer.firebase.Email

@Module
class SQLiteModule {
    @PageScope
    @Provides
    fun provideDatabase(email: Email) : MyDatabase = MainApplication.getDatabase(email.toString())

    @PageScope
    @Provides
    fun provideMyGroupDao(database: MyDatabase) : MyGroupDao = database.myGroupDao()

    @PageScope
    @Provides
    fun provideMyFriendDao(database: MyDatabase) : MyFriendDao = database.myFriendDao()

    @PageScope
    @Provides
    fun provideMySentInvitationDao(database: MyDatabase) : MySentInvitationDao = database.mySentInvitationDao()

    @PageScope
    @Provides
    fun provideMyReceivedInvitationDao(database: MyDatabase) : MyReceivedInvitationDao = database.myReceivedInvitationDao()

    @PageScope
    @Provides
    fun provideMyNotificationDao(database: MyDatabase) : MyNotificationDao = database.myNotificationDao()
}