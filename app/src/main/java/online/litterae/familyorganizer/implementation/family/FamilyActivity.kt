package online.litterae.familyorganizer.implementation.family

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_family.*
import online.litterae.familyorganizer.application.MainApplication
import online.litterae.familyorganizer.R
import online.litterae.familyorganizer.abstracts.view.PageActivity
import online.litterae.familyorganizer.application.Const.Companion.CANCEL_DIALOG
import online.litterae.familyorganizer.application.Const.Companion.CHOOSE_GROUP_DIALOG
import online.litterae.familyorganizer.application.Const.Companion.OK_DIALOG
import online.litterae.familyorganizer.sqlite.MyFriend
import online.litterae.familyorganizer.sqlite.MyGroup
import javax.inject.Inject

class FamilyActivity : PageActivity(), FamilyContract.View {

    @Inject lateinit var presenter : FamilyContract.Presenter

    var friendsNames: MutableList<String> = ArrayList()
    lateinit var adapter: FriendAdapter

    override fun init(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_family)
        setRecyclerView()
        setBottomMenu()
        MainApplication.getAppComponent().createPageComponent().inject(this)
        presenter.attach(this)
        presenter.getData()
    }

    private fun setRecyclerView () {
        val usersRecycler : RecyclerView = findViewById(R.id.rv_users)
        usersRecycler.setHasFixedSize(true)
        usersRecycler.setLayoutManager(LinearLayoutManager(this))
        adapter = FriendAdapter()
        usersRecycler.setAdapter(adapter)
    }

    override fun setSelectedItem(bottomMenu: BottomNavigationView) {
        bottomMenu.selectedItemId = R.id.page_family
    }

    fun chooseGroupMenu(menuItem: MenuItem) {
        presenter.getGroupsList()
    }

    fun createGroupMenu(item: MenuItem) {
        val editGroupName: EditText = EditText(this)
        AlertDialog.Builder(this)
            .setTitle("Enter group name:")
            .setView(editGroupName)
            .setPositiveButton("Create") {dialog, which ->
                presenter.createGroup(editGroupName.getText().toString())
                Toast.makeText(this, "Group $editGroupName created", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel") {dialog, which ->}
            .create()
            .show()
    }

    fun showProfileMenu(item: MenuItem) {

    }

    fun logoutMenu(item: MenuItem) {
        presenter.logout()
    }

    override fun showChooseGroupMenu(groups: List<MyGroup?>) {
        val groupsNamesList : MutableList<String> = ArrayList()
        val groupsList : MutableList<MyGroup> = ArrayList()
        for (myGroup: MyGroup? in groups) {
            myGroup?.let{
                groupsNamesList.add(myGroup.name)
                groupsList.add(myGroup)
            }
        }
        var groupIndex = 0
        for (myGroup: MyGroup in groupsList) {  //Get index of the current group
            if (myGroup.myCurrentGroup == 1) {
                break
            }
            groupIndex++
        }
        AlertDialog.Builder(this)
            .setTitle(CHOOSE_GROUP_DIALOG)
            .setSingleChoiceItems(groupsNamesList.toTypedArray(), groupIndex, { _, which -> groupIndex = which})
            .setPositiveButton(OK_DIALOG, { _, which -> presenter.changeCurrentGroup(groupsList[groupIndex])})
            .setNegativeButton(CANCEL_DIALOG, null)
            .create()
            .show()
    }

    override fun showCurrentGroup(group: MyGroup?) {
        tvUserName.setText(group?.name)
    }

    override fun showFriends(friends: List<MyFriend?>?) {
        friends?.let{
            val newFriendsNames: MutableList<String> = ArrayList()
            for (friend in friends) {
                friend?.let {
                    newFriendsNames.add(friend.name)
                }
            }
            friendsNames = newFriendsNames
            adapter.notifyDataSetChanged()
        }
    }

    override fun showErrorMessage(message: String) {
        Toast.makeText(this, message,Toast.LENGTH_SHORT).show()
    }

    inner class FriendAdapter : RecyclerView.Adapter<FriendViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendViewHolder {
            return FriendViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.user, parent, false))
        }

        override fun onBindViewHolder(holder: FriendViewHolder, position: Int) {
            holder.userName.setText(friendsNames[position])
        }

        override fun getItemCount(): Int = friendsNames.size
    }

    class FriendViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userName : TextView = itemView.findViewById(R.id.tv_user_name)
    }
}