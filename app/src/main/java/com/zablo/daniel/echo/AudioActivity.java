package com.zablo.daniel.echo;


import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;



public class AudioActivity extends AppCompatActivity {

    private String filePath = Environment.getExternalStorageDirectory().getPath();
    private File file = new File(filePath, "EchoSample");
    private ArrayList<String> FilesInFolder = GetFiles(filePath + "/EchoSample");

    private ExtAudioRecorder extAudioRecorder = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        if(!file.exists()){
            file.mkdir();
            Log.v("Create Folder", filePath);
        }


        ListView lv;
        lv = (ListView)findViewById(R.id.filelist);

        lv.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, FilesInFolder));

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Log.v("Item on lv clicked", "pos: " + position);
                // Clicking on item
            }
        });

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int position, long id) {

                Log.v("long clicked", "pos: " + position);

                return true;
            }
        });

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
        RecBtn.setClickable(false);
        StopBtn.setClickable(true);
    }


    public void onStopClick(View view) {
        ListView lv;
        lv = (ListView)findViewById(R.id.filelist);
        Button RecBtn = (Button)findViewById(R.id.RecBtn);
        Button StopBtn = (Button)findViewById(R.id.StopBtn);
        stopRecording();
        StopBtn.setClickable(false);
        RecBtn.setClickable(true);
        FilesInFolder = GetFiles(filePath + "/EchoSample");
        lv.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, FilesInFolder));
    }

    private void stopRecording() {
        if(extAudioRecorder != null) {
            extAudioRecorder.stop();
            extAudioRecorder.release();

            extAudioRecorder = null;
        }
    }


    private void startRecording() {

        extAudioRecorder = ExtAudioRecorder.getInstanse(false);
        extAudioRecorder.setOutputFile(getFilePath());

        try{
            extAudioRecorder.prepare();
            extAudioRecorder.start();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public String getFilePath() {
        String filePath = Environment.getExternalStorageDirectory().getPath();
        File file = new File(filePath, "EchoSample");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd_HH:mm:ss");
        String currentDateAndTime = sdf.format(new Date());

        if(!file.exists())
            file.mkdir();

        return (file.getAbsolutePath() + "/" + currentDateAndTime + ".wav");
    }

    public ArrayList<String> GetFiles(String DirectoryPath) {
        ArrayList<String> MyFiles = new ArrayList<String>();
        File f = new File(DirectoryPath);

        if(!f.exists())
            f.mkdir();
        File[] files = f.listFiles();
        if (files.length == 0)
            return null;
        else {
            for (int i=0; i<files.length; i++)
                MyFiles.add(files[i].getName());
        }

        return MyFiles;
    }

}