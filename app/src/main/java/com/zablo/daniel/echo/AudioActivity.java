package com.zablo.daniel.echo;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AudioActivity extends AppCompatActivity {

    private MediaRecorder recorder = null;
    private int opformats[] = {MediaRecorder.OutputFormat.MPEG_4,
                                MediaRecorder.OutputFormat.THREE_GPP};
    private int curformat = 0;
    private String filePath;
    private String fileextn[] = {".mp4", ".3gpp"};
    private View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }


    public void onRecClick(View view) {
        Button RecBtn = (Button)findViewById(R.id.RecBtn);
        Button StopBtn = (Button)findViewById(R.id.StopBtn);
        startRecording();
//        startRecordingWav(view);
        RecBtn.setClickable(false);
        StopBtn.setClickable(true);
    }


    public void onStopClick(View view) {
        Button RecBtn = (Button)findViewById(R.id.RecBtn);
        Button StopBtn = (Button)findViewById(R.id.StopBtn);
        stopRecording();
//      stopRecordingWav(view);
        StopBtn.setClickable(false);
        RecBtn.setClickable(true);
    }

    private void stopRecording() {
        if(recorder != null) {
            recorder.stop();
            recorder.reset();
            recorder.release();

            recorder = null;
        }
    }


    private void startRecording() {

        recorder = new MediaRecorder();

        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(opformats[curformat]);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB);
        recorder.setOutputFile(getFilePath());

        try{
            recorder.prepare();
            recorder.start();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public String getFilePath() {
        String filePath = Environment.getExternalStorageDirectory().getPath();
        File file = new File(filePath, "EchoSample");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String currentDateAndTime = sdf.format(new Date());

        if(!file.exists())
            file.mkdir();

        return (file.getAbsolutePath() + "/" + currentDateAndTime + fileextn[curformat]);
    }

    public void onRadioClick(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.gppButton:
                if (checked)
                    curformat = 1;
                    break;
            case R.id.mp4format:
                if (checked)
                    curformat = 0;
                    break;
        }
    }
}