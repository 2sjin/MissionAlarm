package com.example.missionalarm;

import static androidx.core.app.ActivityCompat.startActivityForResult;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;

public class CustomListItemView extends BaseAdapter {
    View bodyView;
    Context context;
    LayoutInflater layoutInflater;
    ArrayList<String> data;

    public CustomListItemView(Context context, ArrayList<String> data) {
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = layoutInflater.inflate(R.layout.activity_custom_list_item_view, null);

        TextView textView = view.findViewById(R.id.title);
        textView.setText(data.get(position));

        View bodyView = view.findViewById(R.id.body);
        Switch switchView = view.findViewById(R.id.exSwitch);

        bodyView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(context, "알람 수정", Toast.LENGTH_SHORT).show();

            }
        });

        switchView.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Toast.makeText(context, "click switch view", Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }
}