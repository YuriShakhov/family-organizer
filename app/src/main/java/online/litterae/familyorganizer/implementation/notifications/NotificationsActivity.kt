package online.litterae.familyorganizer.implementation.notifications

import android.content.res.Resources
import android.graphics.drawable.Icon
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import online.litterae.familyorganizer.R
import online.litterae.familyorganizer.abstracts.view.BaseCompatActivity
import online.litterae.familyorganizer.abstracts.view.PageActivity
import online.litterae.familyorganizer.application.Const.Companion.STATUS_ACCEPTED
import online.litterae.familyorganizer.application.Const.Companion.STATUS_DECLINED
import online.litterae.familyorganizer.application.Const.Companion.STATUS_NEW
import online.litterae.familyorganizer.application.MainApplication
import online.litterae.familyorganizer.implementation.family.FamilyContract
import online.litterae.familyorganizer.application.Const.Companion.TAG
import javax.inject.Inject

class NotificationsActivity : BaseCompatActivity(), NotificationsContract.View {
    @Inject
    lateinit var presenter : NotificationsContract.Presenter

    lateinit var adapter: NotificationsAdapter
    var notifications: ArrayList<Notification> = ArrayList()

    override fun init(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_notifications)
        setRecyclerView()
        MainApplication.createPageComponent().inject(this)
    }

    override fun onStart() {
        super.onStart()
        presenter.attach(this)
        notifications = presenter.getNotifications()
    }

    private fun setRecyclerView () {
        val usersRecycler : RecyclerView = findViewById(R.id.rv_notifications)
        usersRecycler.setHasFixedSize(true)
        usersRecycler.setLayoutManager(LinearLayoutManager(this))
        adapter = NotificationsAdapter()
        usersRecycler.setAdapter(adapter)
    }

    inner class NotificationsAdapter : RecyclerView.Adapter<NotificationsViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationsViewHolder {
            return NotificationsViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.notification, parent, false))
        }

        override fun onBindViewHolder(holder: NotificationsViewHolder, position: Int) {
            val notification = notifications[position]
            when (notification) {
                is ReceivedInvitationNotification -> {
                    holder.notification.setText(notification.shortMessage)
                    when (notification.status) {
                        STATUS_NEW -> {
                            holder.image.setImageResource(R.drawable.ic_notifications)
                            holder.layoutButtons.visibility = View.VISIBLE
                            holder.acceptButton.setOnClickListener {
                                presenter.acceptInvitation(notification)
                                holder.image.setImageResource(R.drawable.ic_accepted)
                                holder.layoutButtons.visibility = View.GONE
                            }
                            holder.declineButton.setOnClickListener {
                                presenter.declineInvitation(notification)
                                holder.image.setImageResource(R.drawable.ic_declined)
                                holder.layoutButtons.visibility = View.GONE
                            }
                        }
                        STATUS_ACCEPTED -> {
                            holder.image.setImageResource(R.drawable.ic_accepted)
                            holder.layoutButtons.visibility = View.GONE
                        }
                        STATUS_DECLINED -> {
                            holder.image.setImageResource(R.drawable.ic_declined)
                            holder.layoutButtons.visibility = View.GONE
                        }
                    }
                }
            }
        }

        override fun getItemCount(): Int = notifications.size
    }

    class NotificationsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val notification : TextView = itemView.findViewById(R.id.tv_notification)
        val image : ImageView = itemView.findViewById(R.id.iv_notification)
        val layoutButtons : LinearLayout = itemView.findViewById(R.id.layout_notification_buttons)
        val acceptButton : Button = itemView.findViewById(R.id.bt_accept_invitation)
        val declineButton : Button = itemView.findViewById(R.id.bt_decline_invitation)
    }
}