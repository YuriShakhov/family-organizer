package online.litterae.familyorganizer.implementation.family

import android.content.Intent
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_family.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import online.litterae.familyorganizer.application.MainApplication
import online.litterae.familyorganizer.application.Const.Companion.TAG
import online.litterae.familyorganizer.R
import online.litterae.familyorganizer.abstracts.view.PageActivity
import online.litterae.familyorganizer.application.Const.Companion.CANCEL_DIALOG
import online.litterae.familyorganizer.application.Const.Companion.CHOOSE_GROUP_DIALOG
import online.litterae.familyorganizer.application.Const.Companion.OK_DIALOG
import online.litterae.familyorganizer.implementation.groupchat.GroupChatActivity
import online.litterae.familyorganizer.implementation.notifications.CountDrawable
import online.litterae.familyorganizer.implementation.notifications.NotificationsActivity
import online.litterae.familyorganizer.implementation.singlechat.ChatActivity
import online.litterae.familyorganizer.sqlite.MyFriend
import online.litterae.familyorganizer.sqlite.MyGroup
import javax.inject.Inject

class FamilyActivity : PageActivity(), FamilyContract.View {

    @Inject lateinit var presenter : FamilyContract.Presenter

    var friendsList: List<MyFriend> = ArrayList()
//    var friendsNames: List<String> = ArrayList()
    lateinit var adapter: FriendAdapter
    lateinit var toolBarMenu: Menu

    override fun init(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_family)
        setRecyclerView()
        setBottomMenu()
        toolBarMenu = tb_family.menu
        MainApplication.createPageComponent().inject(this)
    }

    override fun onStart() {
        super.onStart()
        presenter.attach(this)
        presenter.setDataInView()
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

    fun notificationsMenu(menuItem: MenuItem) {
        startActivity(Intent(this, NotificationsActivity::class.java))
    }

    fun chooseGroupMenu(menuItem: MenuItem) {
        presenter.getGroupsList()
    }

    fun createGroupMenu(item: MenuItem) {
        val editGroupName = EditText(this)
        AlertDialog.Builder(this)
            .setTitle("Enter group name:")
            .setView(editGroupName)
            .setPositiveButton("Create") {dialog, which ->
                presenter.createGroup(editGroupName.getText().toString())
            }
            .setNegativeButton("Cancel") {dialog, which ->}
            .create()
            .show()
    }

    fun inviteMenu(item: MenuItem) {
//        Log.d(TAG, "inviteMenu: start")
        CoroutineScope(Dispatchers.Default).launch {
            val currentGroup = presenter.getCurrentGroup().first
            currentGroup?.let {
                Log.d(TAG, "inviteMenu: prepare alertdialog")
                val groupName = it.name
                val editEmail = EditText(this@FamilyActivity)
                editEmail.hint = "Enter email"
                val editMessage = EditText(this@FamilyActivity)
                editMessage.hint = "Write a message"
//                Log.d(TAG, "inviteMenu: groupName: $groupName, editEmail: $editEmail, editMessage: $editMessage")
                val layout = LinearLayout(this@FamilyActivity)
                layout.orientation = LinearLayout.VERTICAL
                layout.addView(editEmail)
                layout.addView(editMessage)
                withContext(Dispatchers.Main) {
                    AlertDialog.Builder(this@FamilyActivity)
                        .setTitle("Invite a new member to group $groupName")
                        .setView(layout)
                        .setPositiveButton("Send") {dialog, which ->
                            presenter.sendInvitation(it, editEmail.text.toString(), editMessage.text.toString())
                        }
                        .setNegativeButton(CANCEL_DIALOG, null)
                        .create()
                        .show()
                }
            }
        }
    }

    fun profileMenu(item: MenuItem) {
        showToast(presenter.getEmail())
    }

    fun logoutMenu(item: MenuItem) {
        presenter.logout()
    }

    override fun showChooseGroupMenu(groups: List<MyGroup>) {
        val groupsNamesList = ArrayList(groups.map{it.name})
        var groupIndex = 0
        for (myGroup: MyGroup in groups) {  //Get index of the current group
            if (myGroup.myCurrentGroup == 1) {
                break
            }
            groupIndex++
        }
        AlertDialog.Builder(this)
            .setTitle(CHOOSE_GROUP_DIALOG)
            .setSingleChoiceItems(groupsNamesList.toTypedArray(), groupIndex, { _, which -> groupIndex = which})
            .setPositiveButton(OK_DIALOG, { _, which -> presenter.changeCurrentGroup(groups[groupIndex])})
            .setNegativeButton(CANCEL_DIALOG, null)
            .create()
            .show()
    }

    override fun showNotifications(count: Int) {
        val notificationsMenuItem = toolBarMenu.findItem(R.id.mi_notifications)
        val icon = notificationsMenuItem.icon as LayerDrawable
        val badge = CountDrawable(this)
        badge.setCount(count.toString())
        icon.mutate()
        icon.setDrawableByLayerId(R.id.ic_notifications_count, badge)
        notificationsMenuItem.setIcon(icon)
    }

    override fun showCurrentGroup(group: MyGroup?, isMyModeratedGroup: Boolean) {
        Log.d(TAG, "showCurrentGroup: ${group?.name}, isMyModeratedGroup: $isMyModeratedGroup")
        val groupName = group?.name
        groupName?.let {
            tvGroupName.setText(groupName)
            tvGroupName.setOnClickListener{
                val groupChatIntent = Intent(this@FamilyActivity, GroupChatActivity::class.java)
                groupChatIntent.putExtra("groupName", groupName)
                startActivity(groupChatIntent)
            }
            tvGroupName.setOnLongClickListener{
                AlertDialog.Builder(this@FamilyActivity)
                    .setMessage("Set group properties ($groupName)")
                    .create()
                    .show()
                true
            }
        }
        if (isMyModeratedGroup) {
            val inviteMenuItem = toolBarMenu.findItem(R.id.mi_invite)
            inviteMenuItem.setVisible(true)
            Log.d(TAG, "showCurrentGroup: menu: $toolBarMenu, inviteMenu: $inviteMenuItem, visible: ${inviteMenuItem.isVisible}")
        }
    }

    override fun showFriends(friends: List<MyFriend>) {
        friendsList = friends
//        friendsNames = friends.map { it.name }
        adapter.notifyDataSetChanged()
    }

    override fun showToast(message: String) {
        Toast.makeText(this, message,Toast.LENGTH_SHORT).show()
    }

    inner class FriendAdapter : RecyclerView.Adapter<FriendViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendViewHolder {
            return FriendViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.user, parent, false))
        }

        override fun onBindViewHolder(holder: FriendViewHolder, position: Int) {
            val friend = friendsList[position]
//            val userName = friendsNames[position]
            val friendName = friend.name
            holder.userName.setText(friendName)
            holder.userLayout.setOnClickListener{
                val chatIntent = Intent(this@FamilyActivity, ChatActivity::class.java)
                val myFriendJson = Gson().toJson(friend)
                Log.d(TAG, "onBindViewHolder. myFriendJson: $myFriendJson")
                chatIntent.putExtra("myFriend", myFriendJson)
//                ChatIntent.putExtra("userName", friendName)
                startActivity(chatIntent)
            }
            holder.userLayout.setOnLongClickListener{
                AlertDialog.Builder(this@FamilyActivity)
                    .setMessage("Set user properties ($friendName)")
                    .create()
                    .show()
                true
            }
        }

        override fun getItemCount(): Int = friendsList.size
    }

    class FriendViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userLayout : LinearLayout = itemView.findViewById(R.id.layout_user)
        val userName : TextView = itemView.findViewById(R.id.tv_user_name)
    }
}