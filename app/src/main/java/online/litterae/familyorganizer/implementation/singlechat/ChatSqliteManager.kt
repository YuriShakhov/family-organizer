package online.litterae.familyorganizer.implementation.singlechat

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import online.litterae.familyorganizer.abstracts.sqlite.BaseSqliteManager
import online.litterae.familyorganizer.application.MainApplication
import online.litterae.familyorganizer.dagger.PageComponent
import online.litterae.familyorganizer.dagger.SingleChatComponent
import online.litterae.familyorganizer.implementation.notifications.NotificationsContract
import online.litterae.familyorganizer.sqlite.MyFriend
import java.lang.reflect.Type

class ChatSqliteManager (val friendFirebaseKey: String)
    : BaseSqliteManager<ChatContract.Presenter>(), ChatContract.SqliteManager {
    override fun init() {
        val chatModule = ChatModule(friendFirebaseKey)
        MainApplication.getAppComponent()
            .createPageComponent()
            .createSingleChatComponent(chatModule)
            .inject(this)
    }

    override suspend fun getMessages(myFriend: MyFriend) : List<Message> {
        val messagesJson : String? = myFriendDao.getMessages(myFriend.userFirebaseKey)
        messagesJson?.let {
            val messagesListType : Type = object : TypeToken<List<Message>>() {}.type
            val messages : List<Message> = Gson().fromJson(messagesJson, messagesListType) ?: emptyList()
            return messages
        }
        return emptyList()
    }

    override suspend fun updateMessages(myFriend: MyFriend, messages: List<Message>) {
        val messagesJson : String = Gson().toJson(messages)
        myFriend.chatMessages = messagesJson
        myFriendDao.update(myFriend)
    }
}