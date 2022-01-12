package com.example.missionalarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "알람 시간입니다.", Toast.LENGTH_LONG).show();    // AVD 확인용

      //  Intent localIntent = new Intent(context, OnAlarmActivity.class);
      //  localIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
      //  context.startActivity(localIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//마시멜로우 버전 권한 처리
            if (Settings.canDrawOverlays(context)) {  //다른앱 위에 그리기 권한이 있을 때
                Intent sIntent = new Intent(context, OnAlarmActivity.class);
                sIntent.putExtra("action", "tts");

                //같은창 여러번 띄우지 않고 기존창 띄운다.
                sIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);

                //Activity가 아닌 곳에서 startActivity를 사용하려고 할때
                sIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(sIntent);
            }
        }




    }
}
