package online.litterae.familyorganizer.dagger

import dagger.Subcomponent
import online.litterae.familyorganizer.implementation.singlechat.*

@SingleChatScope
@Subcomponent(modules = [ChatModule::class])
interface SingleChatComponent {
    fun inject(target: ChatActivity)
    fun inject(target: ChatPresenter)
    fun inject(target: ChatFirebaseManager)
    fun inject(target: ChatSqliteManager)
}