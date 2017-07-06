package com.aiminerva.oldpeople.widget;

import android.content.Context;
import android.widget.TextView;

import com.aiminerva.oldpeople.R;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Utils;

/**
 * Created by Administrator on 2017/7/4.
 */

public class MyMarkerView extends MarkerView {
    private TextView tvContent;

    /**
     * Constructor. Sets up the MarkerView with a custom layout resource.
     *
     * @param context
     * @param layoutResource the layout resource to use for the MarkerView
     */
    public MyMarkerView(Context context, int layoutResource) {
        super(context, layoutResource);
        tvContent = findViewById(R.id.tvContent);
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        super.refreshContent(e, highlight);
        tvContent.setText("" + e.getY());
    }

    private MPPointF mOffset;

    @Override
    public MPPointF getOffset() {
        if(mOffset == null) {
            // center the marker horizontally and vertically
            mOffset = new MPPointF(-(getWidth() / 2), -getHeight());
        }
        return mOffset;
    }

//    @Override
//    public float getX() {
//        return -(getWidth() / 2);
//    }
//
//    @Override
//    public float getY() {
//        return -getHeight();
//    }
}
