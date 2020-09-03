package online.litterae.familyorganizer.implementation.notifications

import android.util.Log
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import online.litterae.familyorganizer.abstracts.presenter.PagePresenter
import online.litterae.familyorganizer.abstracts.view.BaseViewInterface
import online.litterae.familyorganizer.abstracts.view.PageActivity
import online.litterae.familyorganizer.application.Const
import online.litterae.familyorganizer.application.Const.Companion.STATUS_ACCEPTED
import online.litterae.familyorganizer.application.Const.Companion.STATUS_DECLINED
import online.litterae.familyorganizer.application.Const.Companion.STATUS_NEW
import online.litterae.familyorganizer.application.Const.Companion.TAG
import online.litterae.familyorganizer.application.MainApplication
import online.litterae.familyorganizer.implementation.family.FamilyContract
import online.litterae.familyorganizer.implementation.family.FamilyPresenter
import online.litterae.familyorganizer.implementation.notes.NotesPresenter
import online.litterae.familyorganizer.implementation.planner.PlannerPresenter
import online.litterae.familyorganizer.implementation.shopping.ShoppingPresenter
import online.litterae.familyorganizer.sqlite.MyNotification
import online.litterae.familyorganizer.sqlite.MyNotificationDao
import javax.inject.Inject

const val FAMILY_PRESENTER = "FamilyPresenter"
const val NOTES_PRESENTER = "NotesPresenter"
const val PLANNER_PRESENTER = "PlannerPresenter"
const val SHOPING_PRESENTER = "ShoppingPresenter"

class NotificationsHolder () {
    var notificationsList: ArrayList<Notification> = ArrayList()
    var familyPresenter: FamilyPresenter? = null
    var notesPresenter: NotesPresenter? = null
    var plannerPresenter: PlannerPresenter? = null
    var shoppingPresenter: ShoppingPresenter? = null
    val presenters = mutableMapOf(
        FAMILY_PRESENTER to familyPresenter,
        NOTES_PRESENTER to notesPresenter,
        PLANNER_PRESENTER to plannerPresenter,
        SHOPING_PRESENTER to shoppingPresenter
    )

    @Inject
    lateinit var myNotificationDao: MyNotificationDao

    init {
        MainApplication.createPageComponent().inject(this)
        CoroutineScope(Dispatchers.Default).launch {
            val myNotificationsFromSqlite: List<Notification>? = myNotificationDao
                .getAllNotifications()?.mapNotNull {
                                when (it?.type) {
                                        "ReceivedInvitation" -> Gson().fromJson(it.body, ReceivedInvitationNotification::class.java)
                                        "ReplyToInvitation" -> Gson().fromJson(it.body, ReplyToInvitationNotification::class.java)
                                        "ReceivedMessage" -> Gson().fromJson(it.body, ReceivedMessageNotification::class.java)
                                }
                                null
                            }
            myNotificationsFromSqlite?.let {
                notificationsList = ArrayList(myNotificationsFromSqlite)
            }
            for ((_, presenter) in presenters) {
                presenter?.setNotifications(notificationsList.size)
            }
        }
    }

    fun attach(presenter: PagePresenter<out BaseViewInterface>) {
        when (presenter) {
            is FamilyPresenter -> presenters[FAMILY_PRESENTER] = presenter
            is NotesPresenter -> presenters[NOTES_PRESENTER] = presenter
            is PlannerPresenter -> presenters[PLANNER_PRESENTER] = presenter
            is ShoppingPresenter -> presenters[SHOPING_PRESENTER] = presenter
        }
    }

    fun getNewNotificationsCount()
            = notificationsList.filter{it.status == STATUS_NEW}.count()

    fun addNotification(notification: Notification){
        notificationsList.add(notification)
        for ((_, presenter) in presenters) {
            presenter?.setNotifications(notificationsList.size)
        }
    }

    fun changeNotificationStatusToAccepted(notification: ReceivedInvitationNotification) {
        notification.status = STATUS_ACCEPTED
        notificationsList.set(notificationsList.indexOf(notification), notification)
    }

    fun changeNotificationStatusToDeclined(notification: ReceivedInvitationNotification) {
        notification.status = STATUS_DECLINED
        notificationsList.set(notificationsList.indexOf(notification), notification)
    }
}