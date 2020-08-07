package online.litterae.familyorganizer.dagger

import dagger.Subcomponent
import online.litterae.familyorganizer.implementation.login.LoginActivity
import online.litterae.familyorganizer.implementation.login.LoginModule
import online.litterae.familyorganizer.implementation.login.LoginPresenter

@LoginScope
@Subcomponent(modules = [LoginModule::class])
interface LoginComponent {
    fun inject(target: LoginActivity)
    fun inject(target: LoginPresenter)
}