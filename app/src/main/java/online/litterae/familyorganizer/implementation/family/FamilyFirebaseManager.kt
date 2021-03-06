package online.litterae.familyorganizer.implementation.family

import com.google.firebase.database.*
import online.litterae.familyorganizer.abstracts.firebase.BaseFirebaseManager
import online.litterae.familyorganizer.application.Const.Companion.ERROR_INVITATION_ALREADY_SENT
import online.litterae.familyorganizer.application.Const.Companion.GROUP_NAME
import online.litterae.familyorganizer.application.Const.Companion.INVITATION_STATUS
import online.litterae.familyorganizer.application.Const.Companion.STATUS_NEW
import online.litterae.familyorganizer.application.Const.Companion.STATUS_SEEN
import online.litterae.familyorganizer.application.Const.Companion.INVITED_EMAIL
import online.litterae.familyorganizer.application.Const.Companion.STATUS_DECLINED
import online.litterae.familyorganizer.application.Const.Companion.TABLE_GROUPS
import online.litterae.familyorganizer.application.Const.Companion.TABLE_INVITATIONS
import online.litterae.familyorganizer.application.Const.Companion.TABLE_USERS
import online.litterae.familyorganizer.application.MainApplication
import online.litterae.familyorganizer.firebase.FirebaseGroup
import online.litterae.familyorganizer.firebase.Invitation
import online.litterae.familyorganizer.sqlite.MyGroup

class FamilyFirebaseManager: BaseFirebaseManager<FamilyContract.Presenter>(), FamilyContract.FirebaseManager {
    lateinit var invitationsRef: DatabaseReference
    private lateinit var groupRef: DatabaseReference

    /*
    Keeps firebase key of the last seen invitation
    and thus helps to avoid processing the same invitation twice
     */
    var seenInvitationKey: String? = null

    override fun init() {
        MainApplication.createPageComponent().inject(this)
        invitationsRef = dbReference.child(TABLE_INVITATIONS)
        invitationsRef.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                val invited = dataSnapshot.child(INVITED_EMAIL).value as String?
                val status = dataSnapshot.child(INVITATION_STATUS).value as String?
                if (invited == null || invited != myEmail.toString()
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
            }
        })


    }

    override fun addGroupToFirebase(name: String): String? {
        val groupFirebaseKey = dbReference.child(TABLE_GROUPS).push().key
        dbReference.updateChildren(
            mapOf<String, Any>(
                "/${TABLE_GROUPS}/$groupFirebaseKey"
                        to
                        FirebaseGroup(name).toMap()
            )
        )
        return groupFirebaseKey
    }

    override fun addMeToFirebaseGroupUsers(groupName: String, groupFirebaseKey: String) {
        dbReference.updateChildren(
            mapOf<String, Any>(
                "/$TABLE_GROUPS/$groupFirebaseKey/users/$myFirebaseKey"
                        to
                        myEmail.toString()
            )
        )
    }

    override fun addInvitationToFirebase(invitation: Invitation) {
        invitationsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var alreadySent = false
                for (invitationSnapshot in snapshot.children) {
                    val invited = invitationSnapshot.child(INVITED_EMAIL).value as String?
                    val groupName = invitationSnapshot.child(GROUP_NAME).value as String?
                    val status = invitationSnapshot.child(INVITATION_STATUS).value as String?
                    if (invited == invitation.invitedEmail && groupName == invitation.groupName && (status != STATUS_DECLINED)) {
                        alreadySent = true
                        break
                    }
                }
                if (alreadySent) {
                    presenter?.reportError(ERROR_INVITATION_ALREADY_SENT)
                } else {
                    invitation.senderEmail = myEmail.toString()
                    val invitationFirebaseKey = dbReference.child(TABLE_INVITATIONS).push().key
                    invitationFirebaseKey?.let {
                        invitation.invitationFirebaseKey = invitationFirebaseKey
                    }
                    dbReference.updateChildren(
                        mapOf<String, Any>(
                            "/$TABLE_INVITATIONS/$invitationFirebaseKey"
                                    to
                                    invitation.toMap()
                        )
                    )
                    presenter?.reportSuccess("${invitation.invitedEmail} invited to group ${invitation.groupName}")
                    presenter?.onInvitationAddedToFirebase(invitation)
                }

            }

            override fun onCancelled(error: DatabaseError) {
            }
        })

    }

    override fun subscribeToUpdates(myGroup: MyGroup) {
        groupRef = dbReference.child(TABLE_GROUPS).child(myGroup.groupFirebaseKey)
        groupRef.child(TABLE_USERS).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
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
            }
        })
    }
}