package online.litterae.familyorganizer.implementation.groupchat

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import online.litterae.familyorganizer.abstracts.sqlite.BaseSqliteManager
import online.litterae.familyorganizer.application.MainApplication
import online.litterae.familyorganizer.implementation.singlechat.Message
import online.litterae.familyorganizer.sqlite.MyGroup
import java.lang.reflect.Type

class GroupChatSqliteManager (val myGroup: MyGroup)
    : BaseSqliteManager<GroupChatContract.Presenter>(), GroupChatContract.SqliteManager {
    val gson = Gson()

    override fun init() {
        val groupChatModule = GroupChatModule(myGroup)
        MainApplication.getAppComponent()
            .createPageComponent()
            .createGroupChatComponent(groupChatModule)
            .inject(this)
    }

    override suspend fun getMessages(): List<Message> {
        val messagesJson : String? = myGroupDao.getMessages(myGroup.groupFirebaseKey)
        messagesJson?.let {
            val messagesListType : Type = object : TypeToken<List<Message>>() {}.type
            return gson.fromJson(messagesJson, messagesListType) ?: emptyList()
        }
        return emptyList()
    }

    override suspend fun updateMessages(messages: List<Message>) {
        val messagesJson : String = gson.toJson(messages)
        myGroup.groupChatMessages = messagesJson
        myGroupDao.update(myGroup)
    }
}