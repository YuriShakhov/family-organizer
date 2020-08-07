package online.litterae.familyorganizer.implementation.family

import online.litterae.familyorganizer.abstracts.firebase.BaseFirebaseManager
import online.litterae.familyorganizer.application.MainApplication
import online.litterae.familyorganizer.application.Const
import online.litterae.familyorganizer.firebase.FirebaseGroup

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
}