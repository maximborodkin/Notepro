package ru.modemastudio.notepro

import android.app.Application
import ru.modemastudio.notepro.di.AppComponent
import ru.modemastudio.notepro.di.DaggerAppComponent
import timber.log.Timber

class App : Application() {

    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        if(BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        appComponent = DaggerAppComponent.builder()
            .applicationContext(applicationContext)
            .build()
    }
}