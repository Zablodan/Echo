package com.zablo.daniel.echo;

import android.os.Environment;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by dadas on 08.01.2016.
 */
public class Filter {

    double[] meanResult = {0, 0, 0};
//    short[] buffY = new short[b[0].length];
//    short[] buffX = new short[b[0].length];
    double[][] wynikAll = new double[7][];
    File filePath;


    public Filter(File file) {

        filePath = file;

        int i;
        for (i = 0; i < 7; i++) {
            wynikAll[i] = filterOneFreq(i);
            meanResult[0] += wynikAll[i][0];
            meanResult[1] += wynikAll[i][1];
            meanResult[2] += wynikAll[i][2];
        }
        for (i = 0; i < 3; i++)
            meanResult[i] /= 7;

        Log.d("EDT", Double.toString(meanResult[0]));
        Log.d("RT20", Double.toString(meanResult[1]));
        Log.d("RT30", Double.toString(meanResult[2]));
    }

    public short[] toBigEndian(byte[] In) {
        short[] out = new short[In.length / 2];
        int i;

        ByteBuffer bb = ByteBuffer.allocate(2);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        for (i = 0; i < In.length; i += 2) {
            bb.put(0, In[i]);
            bb.put(1, In[i + 1]);
            out[i / 2] = bb.getShort(0);
        }
        return out;
    }

    public byte[] toLittleEndian(short[] In) {
        byte[] out = new byte[In.length * 2];
        int i;
        for (i = 0; i < In.length; i++) {
            out[2 * i] = (byte) (In[i] & 0xff);
            out[2 * i + 1] = (byte) ((In[i] >> 8) & 0xff);
        }
        return out;
    }

    public double[] toDouble(short[] In) {
        double[] out = new double[In.length];
        int i;
        for (i = 0; i < In.length; i++) {
            out[i] = ((double) In[i]) / 32768;
            out[i] = out[i] * out[i];
        }
        return out;
    }

    public double[] zanikf(double[] In) {
        int Last = In.length - 1;
        double[] out = new double[Last + 1];
        out[Last] = In[Last];
        int i;
        for (i = 0; i < Last; i++) {
            out[Last - 1 - i] = out[Last - i] + In[Last - 1 - i];
            out[Last - i] = 10 * Math.log10(out[Last - i]);
        }
        out[0] = 10 * Math.log10(out[0]);
        return out;
    }

    public int[] fMarkery(double[] In) {
        int[] out = new int[5];
        out[0] = 0;
        int i;
        for (i = 0; i < In.length; i++) {
            if (In[0] - 5 < In[i])
                out[0] = i + 1;

            if (In[0] - 15 < In[i])
                out[1] = i + 1;

            if (In[0] - 25 < In[i])
                out[2] = i + 1;

            if (In[0] - 35 < In[i])
                out[3] = i + 1;

            if (In[0] - 65 < In[i])
                out[4] = i + 1;
        }
        return out;
    }

    public double coeff(int m1, int m2, double[] zanik) {
        double out;
        double sumX = 0, tempX;
        double sumY = 0, sumXY = 0, sumXX = 0;

        int i, FS = 44100, N = m2 - m1 + 1;
        for (i = 0; i < N; i++) {

            tempX = ((double) (m1 + i)) / FS;
            sumX += tempX;
            sumY += zanik[m1 + i];
            sumXY += tempX * zanik[m1 + i];
            sumXX += tempX * tempX;
        }
        out = (N * sumXY - sumX * sumY) / (N * sumXX - sumX * sumX);
        return out;
    }


    public double[] filterOneFreq(int freq) {
        double[] wynik = new double[3];
        int buffSize = (int) filePath.length();
        int[] markery;
        byte[] buffer = new byte[buffSize];
        short[] buffShort;
        double[] dValue, zanik;
        double[] interpol = new double[3];

        try {
            DataInputStream inputStream = new DataInputStream(new FileInputStream(filePath));
//            DataOutputStream outputStream = new DataOutputStream(new FileOutputStream(filePath.getPath().substring(0,
//                    filePath.getPath().length() - 4) + "_filtr" + Integer.toString(freq + 1) + ".wav"));

            inputStream.read(buffer, 0, 44);
//            outputStream.write(buffer, 0, 44);

            inputStream.read(buffer);
            buffShort = toBigEndian(buffer);
            buffShort = fFilter(buffShort, freq);
//            buffer = toLittleEndian(buffShort);
//            outputStream.write(buffer);
            dValue = toDouble(buffShort);
            zanik = zanikf(dValue);
            markery = fMarkery(zanik);
            interpol[0] = coeff(markery[0], markery[1], zanik);
            interpol[1] = coeff(markery[0], markery[2], zanik);
            interpol[2] = coeff(markery[0], markery[3], zanik);

            wynik[0] = -60 / interpol[0];
            wynik[1] = -60 / interpol[1];
            wynik[2] = -60 / interpol[2];

            Log.d("wynik0", Double.toString(wynik[0]));
            Log.d("wynik1", Double.toString(wynik[1]));
            Log.d("wynik2", Double.toString(wynik[2]));

//            while((available = inputStream.available()) > 0) {
//
//                inputStream.read(buffer);
//                buffShort = toBigEndian(buffer);
//                buffShort = Filter(buffShort, freq);
//                buffer = toLittleEndian(buffShort);
//                if(available > buffSize)
//                    outputStream.write(buffer);
//                else
//                    outputStream.write(buffer,0,available);
//
//            }
            inputStream.close();
//            outputStream.close();


        } catch (IOException e) {
            e.printStackTrace();
        }
        return wynik;
    }

    public short[] fFilter(short[] In, int freq) {
        short[] buffY = new short[3];
        short[] buffX = new short[3];
        double[][] b = {{0.0062572858547160822, 0, -0.0062572858547160822},
                {0.012437238382855268, 0, -0.012437238382855268},
                {0.024572709022533383, 0, -0.024572709022533383},
                {0.047995743377957548, 0, -0.047995743377957548},
                {0.091807276894903089, 0, -0.091807276894903089},
                {0.16961665538039128, 0, -0.16961665538039128},
                {0.29889181237078871, 0, -0.29889181237078871}};
        double[][] a = {{-1.9871702394723854, 0.98748542829056796},
                {-1.9738726581032571, 0.97512552323428947},
                {-1.9459054887310585, 0.95085458195493333},
                {-1.884699766444327, 0.90400851324408493},
                {-1.7428916379327266, 0.81638544621019371},
                {-1.3946966789565081, 0.66076668923921755},
                {-0.53961547799956111, 0.40221637525842263}};
        short[] out = new short[In.length];
        double valX;
        double valY;
        int I = b[0].length, k, n;
        short iBuff = 0;

        for (k = 0; k < I; k++)
            out[k] = In[k];
        while (k < In.length - (I - 1)) {
            valX = b[freq][0] * In[k];
            valY = 0;
            for (n = 1; n < I; n++) {
                valX += (b[freq][n] * In[k - n]);
                valY += (a[freq][n - 1] * out[k - n]);
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

//    public short[] Filtr(short[] In, int freq) {
//        short[] buffY = new short[b[0].length];
//        short[] buffX = new short[b[0].length];
//        short[] out = new short[In.length];
//        double valX;
//        double valY;
//        int I = b[0].length, k, n = 0, e, g;
//        short iBuff = 0;
//
//        for (k = 0; k < I; k++) {
//            valX = b[freq][0] * In[k];
//            valY = 0;
//            for (g = k - 1; g >= 0; g--) {
//                valY += a[freq][n++] * out[g];
//                valX += b[freq][n] * In[g];
//            }
//            for (e = I - 2; e >= k; e--) {
//                valY += a[freq][n++] * buffY[e];
//                valX += b[freq][n] * buffX[e];
//            }
//            n = 0;
//            out[k] = (short) (valX - valY);
//        }
//        while (k < In.length - (I - 1)) {
//            valX = b[freq][0] * In[k];
//            valY = 0;
//            for (n = 1; n < I; n++) {
//                valX += (b[freq][n] * In[k - n]);
//                valY += (a[freq][n - 1] * out[k - n]);
//            }
//            out[k++] = (short) (valX - valY);
//        }
//        while (k < In.length) {
//            buffX[iBuff] = In[k];
//            valX = b[freq][0] * In[k];
//            valY = 0;
//            for (n = 1; n < I; n++) {
//                valX += (b[freq][n] * In[k - n]);
//                valY += (a[freq][n - 1] * out[k - n]);
//            }
//            out[k] = (short) (valX - valY);
//            buffY[iBuff++] = out[k++];
//        }
//
//        return out;
//    }
}

