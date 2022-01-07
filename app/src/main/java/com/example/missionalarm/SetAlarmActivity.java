package com.example.missionalarm;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

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

        tvSelect = findViewById(R.id.textViewAlarm);
        listView = findViewById(R.id.listView);
        timePicker = findViewById(R.id.timePicker);

        timePicker.setIs24HourView(true);

        list.add("알람 이름");
        list.add("알람음");
        list.add("진동");
        list.add("미션");
        list.add("벌칙");

        // 현재 시각 구하기
        LocalTime now = LocalTime.now();
        hour = now.getHour();
        min = now.getMinute();

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
        setResult(RESULT_CANCELED);
        finish();
    }

    // 선택 버튼을 눌렀을 때
    public void clickedButtonAdd (View view) {

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("setTime", String.format("%02d:%02d", hour, min));

        setResult(RESULT_OK, intent);
        finish();
    }

}