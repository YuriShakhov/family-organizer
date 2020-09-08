package online.litterae.familyorganizer.implementation.notifications

import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import online.litterae.familyorganizer.abstracts.presenter.PagePresenter
import online.litterae.familyorganizer.abstracts.view.BaseViewInterface
import online.litterae.familyorganizer.application.Const.Companion.STATUS_ACCEPTED
import online.litterae.familyorganizer.application.Const.Companion.STATUS_DECLINED
import online.litterae.familyorganizer.application.Const.Companion.STATUS_NEW
import online.litterae.familyorganizer.application.MainApplication
import online.litterae.familyorganizer.implementation.family.FamilyPresenter
import online.litterae.familyorganizer.implementation.notes.NotesPresenter
import online.litterae.familyorganizer.implementation.planner.PlannerPresenter
import online.litterae.familyorganizer.implementation.shopping.ShoppingPresenter
import online.litterae.familyorganizer.sqlite.MyNotificationDao
import javax.inject.Inject

const val FAMILY_PRESENTER = "FamilyPresenter"
const val NOTES_PRESENTER = "NotesPresenter"
const val PLANNER_PRESENTER = "PlannerPresenter"
const val SHOPING_PRESENTER = "ShoppingPresenter"

class NotificationsHolder {
    var notificationsList: ArrayList<Notification> = ArrayList()

    private var familyPresenter: FamilyPresenter? = null
    private var notesPresenter: NotesPresenter? = null
    private var plannerPresenter: PlannerPresenter? = null
    private var shoppingPresenter: ShoppingPresenter? = null
    private val presenters = mutableMapOf(
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
        notificationsList[notificationsList.indexOf(notification)] = notification
    }

    fun changeNotificationStatusToDeclined(notification: ReceivedInvitationNotification) {
        notification.status = STATUS_DECLINED
        notificationsList[notificationsList.indexOf(notification)] = notification
    }
}