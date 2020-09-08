package online.litterae.familyorganizer.implementation.groupchat

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import online.litterae.familyorganizer.abstracts.firebase.BaseFirebaseManager
import online.litterae.familyorganizer.application.Const.Companion.TABLE_GROUPS
import online.litterae.familyorganizer.application.Const.Companion.TABLE_MESSAGES
import online.litterae.familyorganizer.application.MainApplication
import online.litterae.familyorganizer.implementation.singlechat.Message
import online.litterae.familyorganizer.sqlite.MyGroup

class GroupChatFirebaseManager (val myGroup: MyGroup)
    : BaseFirebaseManager<GroupChatContract.Presenter>(), GroupChatContract.FirebaseManager {
    private lateinit var groupMessagesRef: DatabaseReference

    override fun init() {
        val groupChatModule = GroupChatModule(myGroup)
        MainApplication.getAppComponent()
            .createPageComponent()
            .createGroupChatComponent(groupChatModule)
            .inject(this)

        groupMessagesRef = dbReference.child(TABLE_GROUPS)
            .child(myGroup.groupFirebaseKey)
            .child(TABLE_MESSAGES)

        groupMessagesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val messagesList = arrayListOf<Message>()
                for (childSnapshot in snapshot.children) {
                    val messageJson = childSnapshot.getValue(String::class.java)
                    val message = Gson().fromJson(messageJson, Message::class.java)
                    messagesList.add(message)
                }
                presenter?.updateMessages(messagesList)
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    override fun addMessageToFirebase(message: Message) {
        val messageJson = Gson().toJson(message)
        val groupMessagesKey = groupMessagesRef.push().key
        groupMessagesKey?.let {
            groupMessagesRef.updateChildren(
                mapOf<String, Any>(groupMessagesKey to messageJson)
            )
        }
    }
}