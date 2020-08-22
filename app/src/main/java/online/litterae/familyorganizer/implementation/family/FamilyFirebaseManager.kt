package online.litterae.familyorganizer.implementation.family

import online.litterae.familyorganizer.abstracts.firebase.BaseFirebaseManager
import online.litterae.familyorganizer.application.MainApplication
import online.litterae.familyorganizer.application.Const
import online.litterae.familyorganizer.application.Const.Companion.TABLE_INVITATIONS
import online.litterae.familyorganizer.firebase.Email
import online.litterae.familyorganizer.firebase.FirebaseGroup
import online.litterae.familyorganizer.firebase.Invitation
import java.util.*
import kotlin.collections.HashMap

class FamilyFirebaseManager: BaseFirebaseManager<FamilyContract.Presenter>(), FamilyContract.FirebaseManager {
    override fun init() {
        MainApplication.getAppComponent().createPageComponent().inject(this)
    }

    override fun addGroupToFirebase(name: String): String? {
        val groupFirebaseKey = dbReference.child(Const.TABLE_GROUPS).push().getKey()
        val insertGroup: MutableMap<String, Any> = HashMap()
        val newGroupMap: Map<String, String> = FirebaseGroup(name).toMap()
        insertGroup["/${Const.TABLE_GROUPS}/$groupFirebaseKey"] = newGroupMap
        dbReference.updateChildren(insertGroup)
        return groupFirebaseKey
    }

    override fun addMeToFirebaseGroupUsers(groupName: String, groupFirebaseKey: String) {
        val insertUser: MutableMap<String, Any> = HashMap()
        insertUser["/${Const.TABLE_GROUPS}/$groupFirebaseKey/users/$firebaseKey"] = email
        dbReference.updateChildren(insertUser)
    }

    override fun addInvitationToFirebase(invitation: Invitation): String? {
        invitation.senderEmail = email.toString()
        val invitationFirebaseKey = dbReference.child(TABLE_INVITATIONS).push().getKey()
        val insertInvitation = mutableMapOf<String, Any>()
        insertInvitation["/$TABLE_INVITATIONS/$invitationFirebaseKey"] = invitation.toMap()
        dbReference.updateChildren(insertInvitation)
        return invitationFirebaseKey
    }
}