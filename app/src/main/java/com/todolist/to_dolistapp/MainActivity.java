package com.todolist.to_dolistapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Locale;

/*
    To List App adapted from https://www.youtube.com/watch?v=YmRPIGFftp0&t=2s&ab_channel=BradTeachesCode
    Speech to Text Functionality from https://medium.com/voice-tech-podcast/android-speech-to-text-tutorial-8f6fa71606ac
    To Do List Icon: https://www.flaticon.com/free-icon/to-do-list_1440998?term=to%20do&page=1&position=64&page=1&position=64&related_id=1440998&origin=search
 */

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    public static final Integer RecordAudioRequestCode = 1;

    private EditText newTask;
    private Button addNewTask;
    private ListView taskList;
    private ImageView micButton;
    private ArrayList<String> tasks;
    private ArrayAdapter<String> adapter;
    private SpeechRecognizer speechRecognizer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        newTask = findViewById(R.id.taskInput);
        addNewTask = findViewById(R.id.addTaskButton);
        taskList = findViewById(R.id.taskList);

        tasks = FileHelper.readData(this);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, tasks);
        taskList.setAdapter(adapter);

        addNewTask.setOnClickListener(this);
        taskList.setOnItemClickListener(this);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
            checkPermission();
        }

        micButton = findViewById(R.id.speechInputButton);
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        final Intent speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {

            }

            @Override
            public void onBeginningOfSpeech() {
                newTask.setText("");
                newTask.setText("Listening...");
            }

            @Override
            public void onRmsChanged(float rmsdB) {

            }

            @Override
            public void onBufferReceived(byte[] buffer) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int error) {

            }

            @Override
            public void onResults(Bundle results) {
                micButton.setImageResource(R.drawable.ic_mic_black_off);
                ArrayList<String> speechInput = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                newTask.setText(speechInput.get(0));
            }

            @Override
            public void onPartialResults(Bundle partialResults) {

            }

            @Override
            public void onEvent(int eventType, Bundle params) {

            }
        });

        micButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    speechRecognizer.stopListening();
                }
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    micButton.setImageResource(R.drawable.ic_mic_black_24dp);
                    speechRecognizer.startListening(speechRecognizerIntent);
                }
                return false;
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        speechRecognizer.destroy();
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.RECORD_AUDIO},RecordAudioRequestCode);
        }
    }

    // Called when the user taps the Add button
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.addTaskButton) {
            String taskEntered = newTask.getText().toString();
            adapter.add(taskEntered);
            newTask.setText("");
            FileHelper.writeData(tasks, this);
            Toast.makeText(this, "Task Added", Toast.LENGTH_SHORT).show();
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
