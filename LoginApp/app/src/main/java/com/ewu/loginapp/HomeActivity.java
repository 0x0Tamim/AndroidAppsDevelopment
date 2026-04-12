package com.ewu.loginapp;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

    EditText etTask, etDate;
    Button btnAddTask, btnLogout;
    ListView listView;

    ArrayList<String> taskList = new ArrayList<>();
    ArrayAdapter<String> adapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        etTask = findViewById(R.id.etTask);
        etDate = findViewById(R.id.etDate);
        btnAddTask = findViewById(R.id.btnAddTask);
        btnLogout = findViewById(R.id.btnLogout);
        listView = findViewById(R.id.listView);

        taskList = new ArrayList<>();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, taskList);

        listView.setAdapter(adapter);


        btnAddTask.setOnClickListener(v -> {

            String task = etTask.getText().toString();
            String date = etDate.getText().toString();

            if(task.isEmpty() || date.isEmpty()){
                Toast.makeText(this, "Enter task and date", Toast.LENGTH_SHORT).show();
            } else {

                String fullTask = task + " (" + date + ")";
                taskList.add(fullTask);

                adapter.notifyDataSetChanged();

                etTask.setText("");
                etDate.setText("");

                Toast.makeText(this, "Task Added", Toast.LENGTH_SHORT).show();
            }
        });
    }
}