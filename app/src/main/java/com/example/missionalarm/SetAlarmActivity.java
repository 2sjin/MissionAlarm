package com.example.missionalarm;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import java.time.LocalTime;
import java.util.*;

public class SetAlarmActivity extends AppCompatActivity {
    List<String> list = new ArrayList<>();
    ListView listView;
    TextView tvSelect;
    TimePicker timePicker;
    int hour, min;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_alarm);

        // 컴포넌트 불러오기
        tvSelect = findViewById(R.id.textViewAlarm);
        listView = findViewById(R.id.listView);
        timePicker = findViewById(R.id.timePicker);

        // 시간 선택기를 24시간제로 설정
        timePicker.setIs24HourView(true);

        // 현재 시각 구하기
        LocalTime now = LocalTime.now();
        hour = now.getHour();
        min = now.getMinute();

        // 리스트 항목 추가
        list.add("알람 이름");
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
            public void onTimeChanged(TimePicker timePicker, int hourChanged, int minChanged) {
                hour = hourChanged;
                min = minChanged;
            }
        });
    }

    // 취소 버튼을 눌렀을 때
    public void clickedButtonCancel(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        setResult(RESULT_CANCELED, intent);
        finish();
    }

    // 선택 버튼을 눌렀을 때
    public void clickedButtonAdd (View view) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("time", String.format("%02d:%02d", hour, min));
        setResult(RESULT_OK, intent);
        finish();
    }

    // 삭제 버튼을 눌렀을 때
    public void clickedRemoveCancel(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        setResult(-4, intent);
        finish();
    }

}