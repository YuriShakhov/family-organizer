package online.litterae.familyorganizer.implementation.family

import android.content.Intent
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
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
import online.litterae.familyorganizer.R
import online.litterae.familyorganizer.abstracts.view.PageActivity
import online.litterae.familyorganizer.application.Const.Companion.DIALOG_CANCEL
import online.litterae.familyorganizer.application.Const.Companion.DIALOG_CHOOSE_GROUP
import online.litterae.familyorganizer.application.Const.Companion.DIALOG_CREATE
import online.litterae.familyorganizer.application.Const.Companion.DIALOG_ENTER_EMAIL
import online.litterae.familyorganizer.application.Const.Companion.DIALOG_ENTER_GROUP_NAME
import online.litterae.familyorganizer.application.Const.Companion.KEY_MY_FRIEND
import online.litterae.familyorganizer.application.Const.Companion.KEY_MY_GROUP
import online.litterae.familyorganizer.application.Const.Companion.DIALOG_OK
import online.litterae.familyorganizer.application.Const.Companion.DIALOG_SEND
import online.litterae.familyorganizer.application.Const.Companion.DIALOG_WRITE_MESSAGE
import online.litterae.familyorganizer.implementation.groupchat.GroupChatActivity
import online.litterae.familyorganizer.implementation.notifications.CountDrawable
import online.litterae.familyorganizer.implementation.notifications.NotificationsActivity
import online.litterae.familyorganizer.implementation.singlechat.ChatActivity
import online.litterae.familyorganizer.sqlite.MyFriend
import online.litterae.familyorganizer.sqlite.MyGroup
import javax.inject.Inject

class FamilyActivity : PageActivity(), FamilyContract.View {

    @Inject lateinit var presenter : FamilyContract.Presenter

    private lateinit var adapter: FriendAdapter
    private lateinit var toolBarMenu: Menu

    var friendsList: List<MyFriend> = ArrayList()

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
        usersRecycler.layoutManager = LinearLayoutManager(this)
        adapter = FriendAdapter()
        usersRecycler.adapter = adapter
    }

    override fun setSelectedItem(bottomMenu: BottomNavigationView) {
        bottomMenu.selectedItemId = R.id.page_family
    }

    fun notificationsMenu(item: MenuItem) {
        startActivity(Intent(this, NotificationsActivity::class.java))
    }

    fun chooseGroupMenu(item: MenuItem) {
        presenter.getGroupsList()
    }

    fun createGroupMenu(item: MenuItem) {
        val editGroupName = EditText(this)
        AlertDialog.Builder(this)
            .setTitle(DIALOG_ENTER_GROUP_NAME)
            .setView(editGroupName)
            .setPositiveButton(DIALOG_CREATE) { _, _ ->
                presenter.createGroup(editGroupName.text.toString())
            }
            .setNegativeButton(DIALOG_CANCEL) { _, _ ->}
            .create()
            .show()
    }

    fun inviteMenu(item: MenuItem) {
        CoroutineScope(Dispatchers.Default).launch {
            val currentGroup = presenter.getCurrentGroup().first
            currentGroup?.let {
                val groupName = it.name
                val editEmail = EditText(this@FamilyActivity)
                editEmail.hint = DIALOG_ENTER_EMAIL
                val editMessage = EditText(this@FamilyActivity)
                editMessage.hint = DIALOG_WRITE_MESSAGE
                val layout = LinearLayout(this@FamilyActivity)
                layout.orientation = LinearLayout.VERTICAL
                layout.addView(editEmail)
                layout.addView(editMessage)
                withContext(Dispatchers.Main) {
                    AlertDialog.Builder(this@FamilyActivity)
                        .setTitle("Invite a new member to group $groupName")
                        .setView(layout)
                        .setPositiveButton(DIALOG_SEND) { _, _ ->
                            presenter.sendInvitation(it, editEmail.text.toString(), editMessage.text.toString())
                        }
                        .setNegativeButton(DIALOG_CANCEL, null)
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
            .setTitle(DIALOG_CHOOSE_GROUP)
            .setSingleChoiceItems(groupsNamesList.toTypedArray(), groupIndex) { _, which ->
                groupIndex = which
            }
            .setPositiveButton(DIALOG_OK) { _, _ -> presenter.changeCurrentGroup(groups[groupIndex]) }
            .setNegativeButton(DIALOG_CANCEL, null)
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
        notificationsMenuItem.icon = icon
    }

    override fun showCurrentGroup(group: MyGroup?, isMyModeratedGroup: Boolean) {
        val groupName = group?.name
        groupName?.let {
            tvGroupName.text = groupName
            tvGroupName.setOnClickListener{
                val groupChatIntent = Intent(this@FamilyActivity, GroupChatActivity::class.java)
                val myGroup = Gson().toJson(group)
                groupChatIntent.putExtra(KEY_MY_GROUP, myGroup)
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
            inviteMenuItem.isVisible = true
        }
    }

    override fun showFriends(friends: List<MyFriend>) {
        friendsList = friends
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
            val friendName = friend.name
            holder.userName.text = friendName
            holder.userLayout.setOnClickListener{
                val chatIntent = Intent(this@FamilyActivity, ChatActivity::class.java)
                val myFriendJson = Gson().toJson(friend)
                chatIntent.putExtra(KEY_MY_FRIEND, myFriendJson)
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