package online.litterae.familyorganizer.abstracts.sqlite

import online.litterae.familyorganizer.abstracts.presenter.BasePresenterInterface
import online.litterae.familyorganizer.abstracts.view.BaseViewInterface
import online.litterae.familyorganizer.sqlite.*
import javax.inject.Inject

abstract class BaseSqliteManager<P: BasePresenterInterface<out BaseViewInterface>> : BaseSqliteManagerInterface<P> {
    @Inject
    lateinit var database: MyDatabase

    @Inject
    lateinit var myGroupDao: MyGroupDao

    @Inject
    lateinit var mySentInvitationDao: MySentInvitationDao

    @Inject
    lateinit var myReceivedInvitationDao: MyReceivedInvitationDao

    @Inject
    lateinit var myFriendDao: MyFriendDao

    protected var presenter: P? = null
        private set

    override fun attach(presenter: P) {
        init()
        this.presenter = presenter
    }
}