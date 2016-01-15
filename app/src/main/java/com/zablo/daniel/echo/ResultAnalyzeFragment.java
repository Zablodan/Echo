package com.zablo.daniel.echo;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * Created by dadas on 08.01.2016.
 */
public class ResultAnalyzeFragment extends Fragment{

    View myView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.result_analyze,container,false);

        Filter filter = AnalyzeActivity.get();

                java.text.DecimalFormat df = new java.text.DecimalFormat();
        df.setMinimumIntegerDigits(4);
        df.setMaximumFractionDigits(4);
        df.setMinimumIntegerDigits(1);
        df.setMaximumIntegerDigits(1);
        TextView EDTtv = (TextView) myView.findViewById(R.id.EDTview);
        String tmp = "EDT = " + df.format(filter.meanResult[0]) + " s";
        EDTtv.setText(tmp);

        TextView RT20tv = (TextView) myView.findViewById(R.id.RT20view);
        tmp = "RT20 = " + df.format(filter.meanResult[1]) + " s";
        RT20tv.setText(tmp);

        TextView RT30tv = (TextView) myView.findViewById(R.id.RT30view);
        tmp = "RT30 = " + df.format(filter.meanResult[2]) + " s";
        RT30tv.setText(tmp);

        return myView;
    }
}
