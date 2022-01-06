package com.example.missionalarm;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class SetAlarmActivity extends AppCompatActivity {
    List<String> list = new ArrayList<>();
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_alarm);

        final TextView tvSelect = findViewById(R.id.textViewAlarm);
        listView = findViewById(R.id.listView);

        list.add("알람 이름");
        list.add("요일");
        list.add("알람음");
        list.add("진동");
        list.add("미션");
        list.add("벌칙");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);
        listView.setAdapter(adapter);
        
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String data = (String) adapterView.getItemAtPosition(position);
                tvSelect.setText(data);
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
        setResult(RESULT_OK);
        finish();
    }

}