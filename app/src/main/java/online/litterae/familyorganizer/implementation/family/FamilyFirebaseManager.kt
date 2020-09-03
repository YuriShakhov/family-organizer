package online.litterae.familyorganizer.implementation.family

import android.util.Log
import com.google.firebase.database.*
import online.litterae.familyorganizer.abstracts.firebase.BaseFirebaseManager
import online.litterae.familyorganizer.application.Const
import online.litterae.familyorganizer.application.Const.Companion.GROUP_NAME
import online.litterae.familyorganizer.application.Const.Companion.INVITATION_STATUS
import online.litterae.familyorganizer.application.Const.Companion.STATUS_NEW
import online.litterae.familyorganizer.application.Const.Companion.STATUS_SEEN
import online.litterae.familyorganizer.application.Const.Companion.INVITED_EMAIL
import online.litterae.familyorganizer.application.Const.Companion.STATUS_DECLINED
import online.litterae.familyorganizer.application.Const.Companion.TABLE_GROUPS
import online.litterae.familyorganizer.application.Const.Companion.TABLE_INVITATIONS
import online.litterae.familyorganizer.application.Const.Companion.TABLE_USERS
import online.litterae.familyorganizer.application.Const.Companion.TAG
import online.litterae.familyorganizer.application.MainApplication
import online.litterae.familyorganizer.firebase.FirebaseGroup
import online.litterae.familyorganizer.firebase.Invitation
import online.litterae.familyorganizer.sqlite.MyGroup
import java.lang.Exception

class FamilyFirebaseManager: BaseFirebaseManager<FamilyContract.Presenter>(), FamilyContract.FirebaseManager {
    lateinit var invitationsRef: DatabaseReference
    lateinit var groupRef: DatabaseReference
    /*
    Variable that keeps firebase key of the last seen invitation and thus helps to avoid processing the same invitation twice
     */
    var seenInvitationKey: String? = null

    override fun init() {
        MainApplication.createPageComponent().inject(this)
        invitationsRef = dbReference.child(TABLE_INVITATIONS)
        invitationsRef.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                val invited = dataSnapshot.child(INVITED_EMAIL).value as String?
                val status = dataSnapshot.child(INVITATION_STATUS).value as String?
                if (invited == null || invited != email.toString()
                    || status == null || status != STATUS_NEW
                ) return

                val invitation = dataSnapshot.getValue(Invitation::class.java)
                val invitationFirebaseKey = dataSnapshot.key
                if (invitation != null && invitationFirebaseKey != seenInvitationKey) {
                    seenInvitationKey = invitationFirebaseKey
                    invitationsRef.updateChildren(
                        mapOf<String, Any>(
                            "/$invitationFirebaseKey/status"
                                    to
                                    STATUS_SEEN
                        )
                    )
                    presenter?.processReceivedInvitation(invitation)
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Failed to read value from table \"Invitations\".", error.toException())
            }
        })


    }

    override fun addGroupToFirebase(name: String): String? {
        val groupFirebaseKey = dbReference.child(Const.TABLE_GROUPS).push().getKey()
        dbReference.updateChildren(
            mapOf<String, Any>(
                "/${Const.TABLE_GROUPS}/$groupFirebaseKey"
                        to
                        FirebaseGroup(name).toMap()
            )
        )
        return groupFirebaseKey
    }

    override fun addMeToFirebaseGroupUsers(groupName: String, groupFirebaseKey: String) {
        dbReference.updateChildren(
            mapOf<String, Any>(
                "/$TABLE_GROUPS/$groupFirebaseKey/users/$firebaseKey"
                        to
                        email.toString()
            )
        )
    }

    override fun addInvitationToFirebase(invitation: Invitation) {
        invitationsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var alreadySent = false
                for (invitationSnapshot in snapshot.children) {
                    val invited = invitationSnapshot.child(INVITED_EMAIL).value as String?
                    Log.d(TAG, "onDataChange: invited: $invited")
                    val groupName = invitationSnapshot.child(GROUP_NAME).value as String?
                    val status = invitationSnapshot.child(INVITATION_STATUS).value as String?
                    Log.d(TAG, "onDataChange: status: $status")
                    if (invited == invitation.invitedEmail && groupName == invitation.groupName && (status != STATUS_DECLINED)) {
                        alreadySent = true
                        break
                    }
                }
                Log.d(TAG, "alreadySent: $alreadySent")
                if (alreadySent) {
                    presenter?.reportError("Invitation already sent.")
                } else {
                    invitation.senderEmail = email.toString()
                    val invitationFirebaseKey = dbReference.child(TABLE_INVITATIONS).push().getKey()
                    invitationFirebaseKey?.let {
                        invitation.invitationFirebaseKey = invitationFirebaseKey
                    }
                    val insertInvitation = mutableMapOf<String, Any>()
                    insertInvitation["/$TABLE_INVITATIONS/$invitationFirebaseKey"] =
                        invitation.toMap()
                    dbReference.updateChildren(insertInvitation)
                    presenter?.reportSuccess("${invitation.invitedEmail} invited to group ${invitation.groupName}")
                    presenter?.onInvitationAddedToFirebase(invitation)
//                        return invitationFirebaseKey

                }

            }

            override fun onCancelled(error: DatabaseError) {
            }

        })

    }

    override fun subscribeToUpdates(myGroup: MyGroup) {
        groupRef = dbReference.child(TABLE_GROUPS).child(myGroup.firebaseKey)
        Log.d(TAG, "subscribeToUpdates. firebaseKey: ${myGroup.firebaseKey}")
        groupRef.child(TABLE_USERS).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d(TAG, "subscribeToUpdates. onDataChange: ${snapshot.toString()}")
                val friends = arrayListOf<Pair<String, String>>()
                for (userSnapshot in snapshot.children) {
                    val userFirebaseKey = userSnapshot.key
                    val userEmail = userSnapshot.value
                    if (userFirebaseKey is String && userEmail is String) {
                        friends.add(Pair(userFirebaseKey, userEmail))
                    }
                }
                presenter?.updateFriends(friends)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Failed to read value from table \"Users\".", error.toException())
            }
        })
    }
}