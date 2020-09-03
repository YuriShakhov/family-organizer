package online.litterae.familyorganizer.dagger

import dagger.Subcomponent
import online.litterae.familyorganizer.implementation.family.*
import online.litterae.familyorganizer.implementation.notes.NotesActivity
import online.litterae.familyorganizer.implementation.notes.NotesModule
import online.litterae.familyorganizer.implementation.notes.NotesPresenter
import online.litterae.familyorganizer.implementation.notifications.*
import online.litterae.familyorganizer.implementation.planner.PlannerActivity
import online.litterae.familyorganizer.implementation.planner.PlannerModule
import online.litterae.familyorganizer.implementation.planner.PlannerPresenter
import online.litterae.familyorganizer.implementation.shopping.ShoppingActivity
import online.litterae.familyorganizer.implementation.shopping.ShoppingModule
import online.litterae.familyorganizer.implementation.shopping.ShoppingPresenter
import online.litterae.familyorganizer.implementation.singlechat.*
import online.litterae.familyorganizer.sqlite.SQLiteModule

@PageScope
@Subcomponent(modules = [
    SQLiteModule::class,
    FamilyModule::class,
    NotesModule::class,
    PlannerModule::class,
    ShoppingModule::class,
    NotificationsModule::class
])
interface PageComponent {
    fun createSingleChatComponent(chatModule: ChatModule) : SingleChatComponent

    fun inject(target: FamilyActivity)
    fun inject(target: FamilyPresenter)
    fun inject(target: FamilyFirebaseManager)
    fun inject(target: FamilySqliteManager)

    fun inject(target: NotesActivity)
    fun inject(target: NotesPresenter)

    fun inject(target: PlannerActivity)
    fun inject(target: PlannerPresenter)

    fun inject(target: ShoppingActivity)
    fun inject(target: ShoppingPresenter)

    fun inject(target: NotificationsActivity)
    fun inject(target: NotificationsPresenter)
    fun inject(target: NotificationsFirebaseManager)
    fun inject(target: NotificationsSqliteManager)
    fun inject(target: NotificationsHolder)
}