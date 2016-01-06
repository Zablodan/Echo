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
    double[][] b = {{0.0062572858547160822, 0, -0.0062572858547160822},
                    {0.012437238382855268,	0, -0.012437238382855268},
                    {0.024572709022533383,	0, -0.024572709022533383},
                    {0.047995743377957548,	0, -0.047995743377957548},
                    {0.091807276894903089, 0, -0.091807276894903089},
                    {0.16961665538039128,	0, -0.16961665538039128},
                    { 0.29889181237078871, 0, -0.29889181237078871}};
    double[][] a = {{-1.9871702394723854, 0.98748542829056796},
                    {-1.9738726581032571, 0.97512552323428947},
                    {-1.9459054887310585, 0.95085458195493333},
                    {-1.884699766444327 , 0.90400851324408493},
                    {-1.7428916379327266, 0.81638544621019371},
                    {-1.3946966789565081, 0.66076668923921755},
                    {-0.53961547799956111, 0.40221637525842263}};
    short[] buffY = new short[b[0].length];
    short[] buffX = new short[b[0].length];
    File filePath;

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

        filterOneFreq(0);

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


    public void filterOneFreq(int freq) {
        try {
            DataInputStream inputStream = new DataInputStream(new FileInputStream(filePath));
            DataOutputStream outputStream = new DataOutputStream(new FileOutputStream(filePath.getPath().substring(0, filePath.getPath().length() -4) + "_filtr1.wav"));
            int available;

            int buffSize = (int) filePath.length();
            byte[] buffer = new byte[buffSize];
            short[] buffShort;


            inputStream.read(buffer, 0, 44);
            outputStream.write(buffer, 0, 44);

            inputStream.read(buffer);
            buffShort = toBigEndian(buffer);
            buffShort = fFilter(buffShort, freq);
            buffer = toLittleEndian(buffShort);
            outputStream.write(buffer);

            while((available = inputStream.available()) > 0) {

                inputStream.read(buffer);
                buffShort = toBigEndian(buffer);
                buffShort = Filter(buffShort, freq);
                buffer = toLittleEndian(buffShort);
                if(available > buffSize)
                    outputStream.write(buffer);
                else
                    outputStream.write(buffer,0,available);

            }
            inputStream.close();
            outputStream.close();

        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public short[] fFilter(short[] In, int freq){
        short[] out = new short[In.length];
        double valX;
        double valY;
        int I = b[0].length, k, n;
        short iBuff = 0;

        for (k = 0; k < I; k++)
            out[k] = In[k];
        while (k < In.length - (I-1)){
            valX = b[freq][0] * In[k];
            valY = 0;
            for (n = 1; n < I; n++){
                valX += (b[freq][n] * In[k-n]);
                valY += (a[freq][n-1] * out[k-n]);
            }
            out[k++] = (short) (valX - valY);
        }
        while (k < In.length) {
            buffX[iBuff] = In[k];
            valX = b[freq][0] * In[k];
            valY = 0;
            for (n = 1; n < I; n++) {
                valX += (b[freq][n] * In[k - n]);
                valY += (a[freq][n - 1] * out[k - n]);
            }
            out[k] = (short) (valX - valY);
            buffY[iBuff++] = out[k++];
        }

        return out;
    }

    public short[] Filter(short[] In, int freq){
        short[] out = new short[In.length];
        double valX;
        double valY;
        int I = b[0].length, k, n=0, e,g;
        short iBuff = 0;

        for (k = 0; k < I; k++) {
            valX = b[freq][0] * In[k];
            valY = 0;
            for (g = k - 1; g >= 0; g--) {
                valY += a[freq][n++] *  out[g];
                valX += b[freq][n] *  In[g];
            }
            for (e = I - 2; e >= k; e--) {
                valY += a[freq][n++] * buffY[e];
                valX += b[freq][n] * buffX[e];
            }
            n = 0;
            out[k] = (short) (valX - valY);
        }
        while (k < In.length - (I-1)){
            valX = b[freq][0] *  In[k];
            valY = 0;
            for (n = 1; n < I; n++){
                valX += (b[freq][n] *  In[k-n]);
                valY += (a[freq][n-1] *  out[k-n]);
            }
            out[k++] = (short) (valX - valY);
        }
        while (k < In.length) {
            buffX[iBuff] =  In[k];
            valX = b[freq][0] *  In[k];
            valY = 0;
            for (n = 1; n < I; n++) {
                valX += (b[freq][n] *  In[k - n]);
                valY += (a[freq][n - 1] * out[k - n]);
            }
            out[k] = (short) (valX - valY);
            buffY[iBuff++] = out[k++];
        }

        return out;
    }
}
