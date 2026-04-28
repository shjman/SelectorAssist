package com.yahorshymanchyk.selectorassist

import android.app.Application
import com.yahorshymanchyk.selectorassist.di.androidPlatformModule
import com.yahorshymanchyk.selectorassist.di.dataModule
import com.yahorshymanchyk.selectorassist.di.domainModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class SelectorAssistApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@SelectorAssistApp)
            modules(androidPlatformModule, dataModule, domainModule)
        }
    }
}
