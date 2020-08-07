package online.litterae.familyorganizer.implementation.login

import dagger.Module
import dagger.Provides
import online.litterae.familyorganizer.dagger.LoginScope

@Module
class LoginModule {
    @LoginScope
    @Provides
    fun provideLoginPresenter() : LoginContract.Presenter {
        val loginPresenter = LoginPresenter()
        loginPresenter.init()
        return loginPresenter
    }
}