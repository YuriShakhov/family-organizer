package online.litterae.familyorganizer.abstracts.sqlite

import online.litterae.familyorganizer.abstracts.presenter.BasePresenterInterface
import online.litterae.familyorganizer.abstracts.view.BaseViewInterface
import online.litterae.familyorganizer.sqlite.MyDatabase
import online.litterae.familyorganizer.sqlite.MyGroupDao
import javax.inject.Inject

abstract class BaseSqliteManager<P: BasePresenterInterface<out BaseViewInterface>> : BaseSqliteManagerInterface<P> {
    @Inject
    lateinit var database: MyDatabase

    @Inject
    @JvmField
    var myGroupDao: MyGroupDao? = null

    protected var presenter: P? = null
        private set

    override fun attach(presenter: P) {
        init()
        this.presenter = presenter
    }
}