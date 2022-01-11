package com.example.missionalarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "★★★★★★★★★★★★★★★★★★★★★", Toast.LENGTH_LONG).show();    // AVD 확인용

        Intent i = new Intent(context, OnAlarmActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("AlarmForActivity", intent.getBooleanExtra("AlarmForReceiver", false));
        context.startActivity(i);
    }
}
