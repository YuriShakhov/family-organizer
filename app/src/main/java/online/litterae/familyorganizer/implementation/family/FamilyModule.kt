package online.litterae.familyorganizer.implementation.family

import dagger.Module
import dagger.Provides
import online.litterae.familyorganizer.dagger.PageScope

@Module
class FamilyModule {
    @PageScope
    @Provides
    fun provideFamilyPresenter() : FamilyContract.Presenter {
        val presenter = FamilyPresenter()
        presenter.init()
        return presenter
    }

    @PageScope
    @Provides
    fun provideFamilySqliteManager() : FamilyContract.SqliteManager {
        val manager = FamilySqliteManager()
        manager.init()
        return manager
    }

    @PageScope
    @Provides
    fun provideFamilyFirebaseManager() : FamilyContract.FirebaseManager {
        val manager = FamilyFirebaseManager()
        manager.init()
        return manager
    }
}