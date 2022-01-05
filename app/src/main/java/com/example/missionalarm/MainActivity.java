package com.example.missionalarm;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import java.util.*;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TextView tvSelect = findViewById(R.id.tv_select);
        ListView listView = findViewById(R.id.listView);

        List<String> list = new ArrayList<>();
        list.add("06:00");
        list.add("06:05");
        list.add("06:10");
        list.add("06:15");
        list.add("06:20");
        list.add("06:25");
        list.add("06:30");
        list.add("06:35");
        list.add("06:40");
        list.add("06:45");
        list.add("06:50");

        System.out.println("Hello");

        ArrayAdapter<String> adpater = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);
        listView.setAdapter(adpater);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String data = (String) adapterView.getItemAtPosition(position);
                tvSelect.setText(data);
            }
        });
    }
}