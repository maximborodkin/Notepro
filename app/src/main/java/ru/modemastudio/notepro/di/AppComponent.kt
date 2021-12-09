package ru.modemastudio.notepro.di

import android.content.Context
import dagger.BindsInstance
import dagger.Component

@Component(modules = [DataModule::class, ViewModule::class, UtilModule::class])
interface AppComponent {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun applicationContext(context: Context): Builder

        fun build(): AppComponent
    }
}

