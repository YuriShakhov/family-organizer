package online.litterae.familyorganizer.implementation.shopping

import dagger.Module
import dagger.Provides
import online.litterae.familyorganizer.dagger.PageScope

@Module
class ShoppingModule {
    @PageScope
    @Provides
    fun provideShoppingPresenter() : ShoppingContract.Presenter = ShoppingPresenter()
}