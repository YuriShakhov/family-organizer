package online.litterae.familyorganizer.implementation.singlechat

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import online.litterae.familyorganizer.abstracts.firebase.BaseFirebaseManager
import online.litterae.familyorganizer.application.Const.Companion.TABLE_PROFILES
import online.litterae.familyorganizer.application.MainApplication

class ChatFirebaseManager(val friendFirebaseKey: String)
    : BaseFirebaseManager<ChatContract.Presenter>(), ChatContract.FirebaseManager {
    lateinit var myMessagesRef: DatabaseReference
    lateinit var friendMessagesRef: DatabaseReference

    override fun init() {
        val chatModule = ChatModule(friendFirebaseKey)
        MainApplication.getAppComponent()
            .createPageComponent()
            .createSingleChatComponent(chatModule)
            .inject(this)

        myMessagesRef = dbReference.child(TABLE_PROFILES)
            .child(firebaseKey.toString())
            .child(friendFirebaseKey)

        friendMessagesRef = dbReference.child(TABLE_PROFILES)
            .child(friendFirebaseKey)
            .child(firebaseKey.toString())

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
        val myMessagesKey = myMessagesRef.push().getKey()
        myMessagesKey?.let {
            myMessagesRef.updateChildren(
                mapOf<String, Any>(myMessagesKey to messageJson)
            )
        }
        val friendMessagesKey: String? = friendMessagesRef.push().getKey()
        friendMessagesKey?.let {
            friendMessagesRef.updateChildren(
                mapOf<String, Any>(friendMessagesKey to messageJson)
            )
        }
    }
}