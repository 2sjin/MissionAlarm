package com.example.missionalarm;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import java.time.LocalTime;
import java.util.*;

public class SetAlarmActivity extends AppCompatActivity {
    TextView tvSelect;
    TimePicker timePicker;
    Button buttonRemove;
    EditText etName;
    Switch switchVibration;

    int hour, minute;

    long t1, t2;
    static boolean visibleRemoveButton = false;
    static AlarmItem alarm;

    // Activity 초기 실행
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_alarm);

        loadComponentId();
        setVisibleRemoveButton();
        timePicker.setIs24HourView(true);   // 시간 선택기를 24시간제로 설정
        resetComponent();

        // 이벤트: 시간 선택기의 값을 변경했을 때
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int hourChanged, int minuteChanged) {
                hour = hourChanged;
                minute = minuteChanged;
            }
        });
    }

    // 뒤로가기 버튼 두 번 누르면 애플리케이션 종료
    @Override
    public void onBackPressed() {
        t2 = System.currentTimeMillis();
        Toast.makeText(this, "'뒤로' 버튼을 한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show();
        if (t2 - t1 < 2000) {
            super.onBackPressed();
            moveTaskToBack(true);   // 태스크를 백그라운드로 이동
            finishAndRemoveTask();          // 액티비티 종료 + 태스크 리스트에서 지우기
        }
        t1 = System.currentTimeMillis();
    }

    // [취소] 버튼을 눌렀을 때
    public void clickedButtonCancel(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        setResult(RESULT_CANCELED, intent);
        finish();
    }

    // [저장] 버튼을 눌렀을 때
    public void clickedButtonAdd (View view) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("hour", hour);
        intent.putExtra("minute", minute);
        intent.putExtra("name", etName.getText().toString());
        intent.putExtra("vibration", switchVibration.isChecked());
        intent.putExtra("ringtone", true);
        intent.putExtra("mission", "미션");
        intent.putExtra("penalty", "벌칙");
        setResult(RESULT_OK, intent);
        finish();
    }

    // [삭제] 버튼을 눌렀을 때
    public void clickedRemoveCancel(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        setResult(-4, intent);
        finish();
    }

    // 삭제 버튼 활성화/비활성화 여부 설정
    public static void setVisibleRemoveButton(boolean b) {
        visibleRemoveButton = b;
    }

    // AlarmItem 객체 가져오기
    public static void setAlarmItem(AlarmItem a) {
        alarm = a;
    }

    // 레이아웃에서 컴포넌트 ID 가져오기
    public void loadComponentId() {
        tvSelect = findViewById(R.id.textViewAlarm);
        timePicker = findViewById(R.id.timePicker);
        buttonRemove = findViewById(R.id.buttonRemove);
        etName = findViewById(R.id.etName);
        switchVibration = findViewById(R.id.switchVibration);
    }

    // [삭제] 버튼의 표시/숨김 여부를 결정
    public void setVisibleRemoveButton() {
        if(visibleRemoveButton == true) // 알람 수정
            buttonRemove.setVisibility(View.VISIBLE);
        else    // 알람 추가
            buttonRemove.setVisibility(View.GONE);
    }

    // 컴포넌트 초기화
    public void resetComponent() {
        if(alarm == null) {     // 알람 추가: 현재 시각으로 초기화 및 시간 선택기에 출력
            LocalTime now = LocalTime.now();
            hour = now.getHour();
            minute = now.getMinute();
            timePicker.setHour(hour);
            timePicker.setMinute(minute);
        }
        else {    // 알람 수정: 알람 데이터를 가져와서 컴포넌트에 출력
            hour = alarm.hour;
            minute = alarm.minute;
            timePicker.setHour(alarm.hour);
            timePicker.setMinute(alarm.minute);
            etName.setText(alarm.name);
            switchVibration.setChecked(alarm.vibration);
        }
    }


}