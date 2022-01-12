package com.example.missionalarm;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import java.util.Calendar;

public class MyService extends Service {
    AlarmManager alarmManager;
    int day;
    int hour;
    int minute;
    Intent intentAlarm;

    public MyService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        alarmManager = (AlarmManager)getSystemService (Context.ALARM_SERVICE);
        intentAlarm = new Intent(this, AlarmReceiver.class);
        PendingIntent pIntent = PendingIntent.getBroadcast(this, 0, intentAlarm, 0);

        day = intent.getIntExtra("day", 0);
        hour = intent.getIntExtra("hour", 0);
        minute = intent.getIntExtra("minute", 0);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, day);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pIntent);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(getApplicationContext(), "서비스가 종료되었습니다.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}