package com.example.missionalarm;

import androidx.appcompat.app.AppCompatActivity;

import android.media.AudioAttributes;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

public class OnAlarmActivity extends AppCompatActivity {
    TextView tvOnAlarmName;
    TextView tvOnAlarmTime;
    Alarm alarm;
    Ringtone ringtone;
    Vibrator vibrator;
    long[] pattern = {100, 1000};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_alarm);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // 화면 자동꺼짐 방지
        vibrator = (Vibrator)getSystemService(VIBRATOR_SERVICE);    // 진동 권한 획득

        // MainActivity에서 알람 객체 가져오기
        alarm = MainActivity.alarmObjectForOnAlarm;

        // 텍스트뷰 설정
        loadComponentId();
        tvOnAlarmName.setText(alarm.name);
        tvOnAlarmTime.setText(String.format("%02d:%02d", alarm.hour, alarm.minute));

        // 다른 알람음 및 진동 끄기
        vibrator.cancel();
        ringtoneRelease();


        // 알람음 재생
        ringtonePlay();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
            ringtone.setVolume((float)alarm.ringtoneVolume / 100.0f);

        // 진동 재생
        if(alarm.vibration == true)
            vibrator.vibrate(pattern, 0);
    }

    // [알람 끄기] 버튼을 눌렀을 때
    public void clickedButtonOff(View view) {
        vibrator.cancel();
        ringtoneRelease();
        finish();
    }

    // 알람음 재생
    public void ringtonePlay() {
        ringtone = RingtoneManager.getRingtone(this, alarm.ringtoneUri);
        AudioAttributes audioAttributes = new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ALARM).build();
        ringtone.setAudioAttributes(audioAttributes);
        ringtone.play();
    }

    // 알람음 종료
    public void ringtoneRelease() {
        if( ringtone != null ) {
            if( ringtone.isPlaying() ) {
                ringtone.stop();
                ringtone = null;
            }
        }
    }

    // 레이아웃에서 컴포넌트 ID 가져오기
    public void loadComponentId() {
        tvOnAlarmName = findViewById(R.id.tvOnAlarmName);
        tvOnAlarmTime = findViewById(R.id.tvOnAlarmTime);
    }
}