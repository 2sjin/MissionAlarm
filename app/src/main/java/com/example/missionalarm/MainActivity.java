package com.example.missionalarm;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import java.util.*;

public class MainActivity extends AppCompatActivity {
    List<String> list = new ArrayList<>();
    ListView listView;
    TextView tv;
    int selectedIndex;

    // 애플리케이션 초기 실행
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 컴포넌트 불러오기
        tv = findViewById(R.id.textViewAlarm);
        listView = findViewById(R.id.listView);

        // 초기 리스트 추가 및 새로고침
        list.add("06:00");
        updateList();

        // 리스트의 항목 클릭 시
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                selectedIndex = position;
                String data = (String) adapterView.getItemAtPosition(selectedIndex);
                tv.setText(data);
                setAlarm();
            }
        });
    }

    // 알람 추가 버튼 클릭 시
    public void addAlarm(View view) {
        Intent intent = new Intent(this, SetAlarmActivity.class);
        startActivityForResult(intent, 0);
    }

    // 알람 수정
    public void setAlarm() {
        Intent intent = new Intent(this, SetAlarmActivity.class);
        startActivityForResult(intent, 1);
    }

    // startActivityForResult 이후 메인 화면으로 돌아올 때
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        String myTimeData = data.getStringExtra("time");
        switch(requestCode) {
            case 0: // 알람 추가 화면에서 복귀 후
                if (resultCode == RESULT_OK)
                    list.add(myTimeData);
                break;
            case 1: // 알람 수정 화면에서 복귀 후
                if (resultCode == RESULT_OK)    // 알람 수정
                    list.set(selectedIndex, myTimeData);
                else if(resultCode == -4)   // 알람 삭제
                    list.remove(selectedIndex);
                break;
        }
        updateList();
    }

    // 리스트 새로고침
    public void updateList() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);
        listView.setAdapter(adapter);
    }

}