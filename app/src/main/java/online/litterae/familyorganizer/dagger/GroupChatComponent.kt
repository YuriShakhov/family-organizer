package online.litterae.familyorganizer.dagger

import dagger.Subcomponent
import online.litterae.familyorganizer.implementation.groupchat.*

@GroupChatScope
@Subcomponent(modules = [GroupChatModule::class])
interface GroupChatComponent {
    fun inject(target: GroupChatActivity)
    fun inject(target: GroupChatPresenter)
    fun inject(target: GroupChatFirebaseManager)
    fun inject(target: GroupChatSqliteManager)
}