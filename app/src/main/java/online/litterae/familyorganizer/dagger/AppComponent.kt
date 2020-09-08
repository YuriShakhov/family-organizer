package online.litterae.familyorganizer.dagger

import dagger.Component
import online.litterae.familyorganizer.firebase.FirebaseModule

@AppScope
@Component(modules = [FirebaseModule::class])

interface AppComponent {
    fun createLoginComponent() : LoginComponent
    fun createPageComponent() : PageComponent
}