package com.example.missionalarm;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import com.google.android.material.textfield.TextInputEditText;

import java.time.LocalTime;
import java.util.*;

public class SetAlarmActivity extends AppCompatActivity {
    List<String> list = new ArrayList<>();

    ListView listView;
    TextView tvSelect;
    TimePicker timePicker;
    Button buttonRemove;
    EditText etName;

    int hour, minute;
    boolean vibration, bell;

    long t1, t2;
    static boolean visibleRemove = false;

    // Activity 초기 실행
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_alarm);

        // 컴포넌트 ID 불러오기
        tvSelect = findViewById(R.id.textViewAlarm);
        listView = findViewById(R.id.listView);
        timePicker = findViewById(R.id.timePicker);
        buttonRemove = findViewById(R.id.buttonRemove);
        etName = findViewById(R.id.etName);

        // 알람 추가 시에는 [삭제] 버튼 숨기기
        if(visibleRemove == true)
            buttonRemove.setVisibility(View.VISIBLE);
        else
            buttonRemove.setVisibility(View.GONE);

        // 시간 선택기를 24시간제로 설정
        timePicker.setIs24HourView(true);

        // 현재 시각 구하기
        LocalTime now = LocalTime.now();
        hour = now.getHour();
        minute = now.getMinute();

        // 리스트 항목 추가
        list.add("알람음");
        list.add("진동");
        list.add("미션");
        list.add("벌칙");

        // 리스트 항목 새로고침
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);
        listView.setAdapter(adapter);

        // 아이템 항목을 클릭했을 때
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String data = (String) adapterView.getItemAtPosition(position);
                tvSelect.setText(data);
            }
        });

        // 시간 선택기의 값을 변경했을 때
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
        intent.putExtra("vibration", vibration);
        intent.putExtra("bell", bell);
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

    public static void setVisibleRemove(boolean b) {
        visibleRemove = b;
    }
}