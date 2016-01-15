package com.zablo.daniel.echo;

import android.app.Fragment;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.FloatMath;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.androidplot.Plot;
import com.androidplot.util.PixelUtils;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.CatmullRomInterpolator;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.PointLabelFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;
import java.util.Arrays;


/**
 * Created by dadas on 08.01.2016.
 */
public class PlotAnalyzeFragment extends Fragment {

    private XYPlot plot;
    private View myView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.simple_xy_plot_example, container, false);
        Filter filter = AnalyzeActivity.get();
        plot = (XYPlot) myView.findViewById(R.id.plot);


        // create a couple arrays of y-values to plot:
        Number[] EDTNumbers = new Number[14];
        Number[] RT20Numbers = new Number[14];
        Number[] RT30Numbers = new Number[14];


        EDTNumbers[0] = 125;
        EDTNumbers[2] = 250;
        EDTNumbers[4] = 500;
        EDTNumbers[6] = 1000;
        EDTNumbers[8] = 2000;
        EDTNumbers[10] = 4000;
        EDTNumbers[12] = 8000;
        RT20Numbers[0] = 125;
        RT20Numbers[2] = 250;
        RT20Numbers[4] = 500;
        RT20Numbers[6] = 1000;
        RT20Numbers[8] = 2000;
        RT20Numbers[10] = 4000;
        RT20Numbers[12] = 8000;
        RT30Numbers[0] = 125;
        RT30Numbers[2] = 250;
        RT30Numbers[4] = 500;
        RT30Numbers[6] = 1000;
        RT30Numbers[8] = 2000;
        RT30Numbers[10] = 4000;
        RT30Numbers[12] = 8000;

        for (int i = 1; i < 14; i += 2) {
            EDTNumbers[i] = filter.wynikAll[i / 2][0];
            RT20Numbers[i] = filter.wynikAll[i / 2][1];
            RT30Numbers[i] = filter.wynikAll[i / 2][2];
        }


        // turn the above arrays into XYSeries':
        // (Y_VALS_ONLY means use the element index as the x value)
        XYSeries series1 = new SimpleXYSeries(Arrays.asList(EDTNumbers),
                SimpleXYSeries.ArrayFormat.XY_VALS_INTERLEAVED, "EDT");

        XYSeries series2 = new SimpleXYSeries(Arrays.asList(RT20Numbers),
                SimpleXYSeries.ArrayFormat.XY_VALS_INTERLEAVED, "RT20");

        XYSeries series3 = new SimpleXYSeries(Arrays.asList(RT30Numbers),
                SimpleXYSeries.ArrayFormat.XY_VALS_INTERLEAVED, "RT30");

        // create formatters to use for drawing a series using LineAndPointRenderer
        // and configure them from xml:
        LineAndPointFormatter series1Format = new LineAndPointFormatter();
        series1Format.setPointLabelFormatter(new PointLabelFormatter());
        series1Format.configure(getActivity().getApplicationContext(),
                R.xml.line_point_formatter_with_labels);

        LineAndPointFormatter series2Format = new LineAndPointFormatter();
        series2Format.setPointLabelFormatter(new PointLabelFormatter());
        series2Format.configure(getActivity().getApplicationContext(),
                R.xml.line_point_formatter_with_labels_2);

        LineAndPointFormatter series3Format = new LineAndPointFormatter();
        series3Format.setPointLabelFormatter(new PointLabelFormatter());
        series3Format.configure(getActivity().getApplicationContext(),
                R.xml.line_point_formatter_with_labels_3);

        // just for fun, add some smoothing to the lines:
        // see: http://androidplot.com/smooth-curves-and-androidplot/
        series1Format.setInterpolationParams(
                new CatmullRomInterpolator.Params(10, CatmullRomInterpolator.Type.Centripetal));

        series2Format.setInterpolationParams(
                new CatmullRomInterpolator.Params(10, CatmullRomInterpolator.Type.Centripetal));

        series3Format.setInterpolationParams(
                new CatmullRomInterpolator.Params(10, CatmullRomInterpolator.Type.Centripetal));

        // add a new series' to the xyplot:
        plot.addSeries(series1, new LineAndPointFormatter(
                Color.rgb(255, 0, 0), Color.rgb(155, 0, 0), null, null));
        plot.addSeries(series2, new LineAndPointFormatter(
                Color.rgb(0, 0, 200), Color.rgb(0, 0, 150), null, null));
        plot.addSeries(series3, new LineAndPointFormatter(
                Color.rgb(0, 170, 0), Color.rgb(0, 120, 0), null, null));

        // reduce the number of range labels
        plot.setTicksPerRangeLabel(3);


        // rotate domain labels 45 degrees to make them more compact horizontally:
        plot.getGraphWidget().setDomainLabelOrientation(-45);


        return myView;
    }

}

