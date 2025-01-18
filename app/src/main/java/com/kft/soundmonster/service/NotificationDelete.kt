package com.kft.soundmonster.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.kft.soundmonster.SoundMonsterApp


class NotificationDeletedReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {


        context?.unbindService((context.applicationContext as SoundMonsterApp).connection)
        context?.stopService(Intent(context, MusicService::class.java))
    }
}