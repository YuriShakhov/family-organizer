package online.litterae.familyorganizer.implementation.singlechat

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import online.litterae.familyorganizer.abstracts.sqlite.BaseSqliteManager
import online.litterae.familyorganizer.application.MainApplication
import online.litterae.familyorganizer.sqlite.MyFriend
import java.lang.reflect.Type

class ChatSqliteManager (val myFriend: MyFriend)
    : BaseSqliteManager<ChatContract.Presenter>(), ChatContract.SqliteManager {
    override fun init() {
        val chatModule = ChatModule(myFriend)
        MainApplication.getAppComponent()
            .createPageComponent()
            .createSingleChatComponent(chatModule)
            .inject(this)
    }

    override suspend fun getMessages() : List<Message> {
        val messagesJson : String? = myFriendDao.getMessages(myFriend.userFirebaseKey)
        messagesJson?.let {
            val messagesListType : Type = object : TypeToken<List<Message>>() {}.type
            return Gson().fromJson(messagesJson, messagesListType) ?: emptyList()
        }
        return emptyList()
    }

    override suspend fun updateMessages(messages: List<Message>) {
        val messagesJson : String = Gson().toJson(messages)
        myFriend.chatMessages = messagesJson
        myFriendDao.update(myFriend)
    }
}