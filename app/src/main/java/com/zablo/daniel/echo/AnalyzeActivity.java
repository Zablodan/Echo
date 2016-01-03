package com.zablo.daniel.echo;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;


public class AnalyzeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    double[] b = {0.00886138484287286, 0, -0.0177227696857457, 0, 0.00886138484287286};
    double[] a = {-3.56623390500889, 4.91027011761000, -3.09041699553028, 0.752059482463646};
    int buffSize = 64;
    byte[] buffer = new byte[buffSize];
    short[] buffShort = new short[buffSize/2];
    File filePath;
    short[] buffY = new short[5];
    short[] buffX = new short[5];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analyze);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //parsing input intent
        Intent intentInput = getIntent();
        Bundle bundleInputData = intentInput.getExtras();
        filePath = new File(bundleInputData.getString(AudioActivity.KEY_NAME));

        filterOneFreq();

        TextView tv = (TextView) findViewById(R.id.testview);
        int i;
        String tmp = new String();
//        for(i=0;i<buffSize/2;i++)
//        tmp += Double.toString(buffShort[i]) + " ";
        tmp = Long.toString(filePath.length());
        tv.setText(tmp);




        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_about) {
            Intent intent = new Intent(this, AboutActivity.class);

            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camara) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public static String bytesToHex(byte[] in) {
        final StringBuilder builder = new StringBuilder();
        for(byte b : in) {
            builder.append(String.format("%02x", b));
        }
        return builder.toString();
    }

    public static short[] toBigEndian(byte[] In) {
        short[] out = new short[In.length/2];
        int i;

        ByteBuffer bb =ByteBuffer.allocate(2);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        for(i=0;i<In.length;i+=2) {
            bb.put(0,In[i]);
            bb.put(1,In[i+1]);
            out[i/2] = bb.getShort(0);
        }
        return out;
    }

    public static byte[] toLittleEndian(short[] In) {
        byte[] out = new byte[In.length*2];
        int i;
        for(i = 0; i < In.length; i++) {
            out[2*i] = (byte) (In[i] & 0xff);
            out[2*i+1] = (byte)((In[i] >> 8) & 0xff);
        }
        return out;
    }


    public void filterOneFreq() {
        try {
            DataInputStream inputStream = new DataInputStream(new FileInputStream(filePath));
            DataOutputStream outputStream = new DataOutputStream(new FileOutputStream(filePath.getPath().substring(0, filePath.getPath().length() -4) + "_fitr.wav"));

            inputStream.read(buffer,0,44);
            outputStream.write(buffer,0,44);//,0,44

            buffShort = toBigEndian(buffer);
            buffShort = fFilter(buffShort);
            buffer = toLittleEndian(buffShort);
            outputStream.write(buffer);

            while(inputStream.read(buffer) != -1) {

                buffShort = toBigEndian(buffer);
                buffShort = Filter(buffShort);
                buffer = toLittleEndian(buffShort);
                outputStream.write(buffer);
            }

        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public short[] fFilter(short[] In){
        short[] out = new short[In.length];
        double valX;
        double valY;
        int I = 5, k, n;
        short iBuff = 0;

        for (k = 0; k < I; k++)
            out[k] = In[k];
        while (k < In.length - (I-1)){
            valX = b[0] * In[k];
            valY = 0;
            for (n = 1; n < I; n++){
                valX += (b[n] * In[k-n]);
                valY += (a[n-1] * In[k-n]);
            }
            out[k++] = (short) (valX - valY);
        }
        while (k < In.length) {
            buffX[iBuff] = In[k];
            valX = b[0] * In[k];
            valY = 0;
            for (n = 1; n < I; n++) {
                valX += (b[n] * In[k - n]);
                valY += (a[n - 1] * In[k - n]);
            }
            out[k] = (short) (valX - valY);
            buffY[iBuff++] = out[k++];
        }

        return out;
    }

    public short[] Filter(short[] In){
        short[] out = new short[In.length];
        double valX=0;
        double valY;
        int I = 5, k, n=0, e=0,g=0;
        short iBuff = 0;

        for (k = 0; k < I; k++) {
            valX = b[0] * In[k];
            valY = 0;
            for (g = k - 1; g >= 0; g--) {
                valY += a[n++] * In[g];
                valX += b[n] * In[g];
            }
            for (e = I - 2; e >= k; e--) {
                valY += a[n++] * buffY[e];
                valX += b[n] * buffX[e];
            }
            n = 0;
            out[k] = (short) (valX - valY);
        }
        while (k < In.length - (I-1)){
            valX = b[0] * In[k];
            valY = 0;
            for (n = 1; n < I; n++){
                valX += (b[n] * In[k-n]);
                valY += (a[n-1] * In[k-n]);
            }
            out[k++] = (short) (valX - valY);
        }
        while (k < In.length) {
            buffX[iBuff] = In[k];
            valX = b[0] * In[k];
            valY = 0;
            for (n = 1; n < I; n++) {
                valX += (b[n] * In[k - n]);
                valY += (a[n - 1] * In[k - n]);
            }
            out[k] = (short) (valX - valY);
            buffY[iBuff++] = out[k++];
        }

        return out;
    }
}
