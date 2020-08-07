package online.litterae.familyorganizer.implementation.family

import dagger.Module
import dagger.Provides
import online.litterae.familyorganizer.dagger.PageScope

@Module
class FamilyModule {
    @PageScope
    @Provides
    fun provideFamilyPresenter() : FamilyContract.Presenter = FamilyPresenter()

    @PageScope
    @Provides
    fun provideFamilySqliteManager() : FamilyContract.SqliteManager = FamilySqliteManager()

    @PageScope
    @Provides
    fun provideFamilyFirebaseManager() : FamilyContract.FirebaseManager = FamilyFirebaseManager()
}