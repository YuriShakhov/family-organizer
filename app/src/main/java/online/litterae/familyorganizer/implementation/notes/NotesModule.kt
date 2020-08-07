package online.litterae.familyorganizer.implementation.notes

import dagger.Module
import dagger.Provides
import online.litterae.familyorganizer.dagger.PageScope

@Module
class NotesModule {
    @PageScope
    @Provides
    fun provideNotesPresenter(): NotesContract.Presenter = NotesPresenter()
}