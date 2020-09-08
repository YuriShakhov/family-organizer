package online.litterae.familyorganizer.implementation.singlechat

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import online.litterae.familyorganizer.abstracts.firebase.BaseFirebaseManager
import online.litterae.familyorganizer.application.Const.Companion.TABLE_PROFILES
import online.litterae.familyorganizer.application.MainApplication
import online.litterae.familyorganizer.sqlite.MyFriend

class ChatFirebaseManager(val myFriend: MyFriend)
    : BaseFirebaseManager<ChatContract.Presenter>(), ChatContract.FirebaseManager {
    private lateinit var myMessagesRef: DatabaseReference
    private lateinit var friendMessagesRef: DatabaseReference

    override fun init() {
        val chatModule = ChatModule(myFriend)
        MainApplication.getAppComponent()
            .createPageComponent()
            .createSingleChatComponent(chatModule)
            .inject(this)

        myMessagesRef = dbReference.child(TABLE_PROFILES)
            .child(myFirebaseKey.toString())
            .child(myFriend.userFirebaseKey)

        friendMessagesRef = dbReference.child(TABLE_PROFILES)
            .child(myFriend.userFirebaseKey)
            .child(myFirebaseKey.toString())

        myMessagesRef.addValueEventListener(object : ValueEventListener {
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
        val myMessagesKey = myMessagesRef.push().key
        myMessagesKey?.let {
            myMessagesRef.updateChildren(
                mapOf<String, Any>(myMessagesKey to messageJson)
            )
        }
        val friendMessagesKey: String? = friendMessagesRef.push().key
        friendMessagesKey?.let {
            friendMessagesRef.updateChildren(
                mapOf<String, Any>(friendMessagesKey to messageJson)
            )
        }
    }
}