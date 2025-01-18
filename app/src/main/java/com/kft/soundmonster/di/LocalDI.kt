package com.kft.soundmonster.di

import android.content.Context
import com.kft.soundmonster.local.SharePref
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent


@InstallIn(SingletonComponent::class)
@Module
class LocalDI {

    @Provides
    fun getSharePreference(@ApplicationContext context: Context) = SharePref(context)
}