package online.litterae.familyorganizer.implementation.family

import android.util.Log
import dagger.Module
import dagger.Provides
import online.litterae.familyorganizer.dagger.PageScope

@Module
class FamilyModule {
    @PageScope
    @Provides
    fun provideFamilyPresenter() : FamilyContract.Presenter {
        val familyPresenter = FamilyPresenter()
        Log.e("SC*1", "FamilyPresenter: $familyPresenter")
        return familyPresenter
    }

    @PageScope
    @Provides
    fun provideFamilySqliteManager() : FamilyContract.SqliteManager = FamilySqliteManager()

    @PageScope
    @Provides
    fun provideFamilyFirebaseManager() : FamilyContract.FirebaseManager = FamilyFirebaseManager()
}