package com.todolist.to_dolistapp;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private EditText newTask;
    private Button addNewTask;
    private ListView taskList;
    private ArrayList<String> tasks;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        newTask = findViewById(R.id.editText);
        addNewTask = findViewById(R.id.button);
        taskList = findViewById(R.id.listview);

        tasks = FileHelper.readData(this);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, tasks);
        taskList.setAdapter(adapter);

        addNewTask.setOnClickListener(this);
        taskList.setOnItemClickListener(this);
    }

    // Called when the user taps the Add button
    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.button:
                String taskEntered = newTask.getText().toString();
                adapter.add(taskEntered);
                newTask.setText("");
                FileHelper.writeData(tasks, this);
                Toast.makeText(this, "Task Added", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        tasks.remove(position);
        adapter.notifyDataSetChanged();
        FileHelper.writeData(tasks, this);
        Toast.makeText(this, "Task Removed", Toast.LENGTH_SHORT).show();
    }
}
