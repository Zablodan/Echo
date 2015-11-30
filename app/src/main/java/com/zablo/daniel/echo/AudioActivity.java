package com.zablo.daniel.echo;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcel;
import android.os.UserHandle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Display;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class AudioActivity extends AppCompatActivity {

    private String filePath = Environment.getExternalStorageDirectory().getPath();
    private File file = new File(filePath, "EchoSample");
    private ArrayList<String> FilesInFolder = GetFiles(filePath + "/EchoSample");
    private Context actContext = this;
    private ExtAudioRecorder extAudioRecorder = null;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(!file.exists())
            file.mkdir();

        ListView lv = (ListView)findViewById(R.id.filelist);

        if(FilesInFolder==null)
            lv.setEmptyView(findViewById(R.id.emptyElement));
        else
        lv.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, FilesInFolder));

        registerForContextMenu(lv);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Log.v("Item on lv clicked", "pos: " + position);
                // Clicking on item

                Uri uri = Uri.parse(file.getPath() + "/" + FilesInFolder.get(position));
                MediaPlayer mPlayer = MediaPlayer.create(actContext, uri);
                mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        // TODO Auto-generated method stub
                        mp.release();
                    }
                });
                mPlayer.start();
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

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.audiolist_menu, menu);

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        ListView lv;
        lv = (ListView)findViewById(R.id.filelist);
        switch (item.getItemId()) {
            case R.id.menuAnalyze:
                //Analyze menu handle
                return true;
            case R.id.menuDelete:
                //Delete menu handle
                File filedel = new File(file.getPath() + "/" + FilesInFolder.get(item.getOrder()));
                filedel.delete();
                FilesInFolder = GetFiles(filePath + "/EchoSample");
                if(FilesInFolder==null) {
                    lv.setAdapter(null);
                    lv.setEmptyView(findViewById(R.id.emptyElement));
                }
                else
                lv.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, FilesInFolder));

            default:
                return super.onOptionsItemSelected(item);
        }
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
        }catch (Exception e) {
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