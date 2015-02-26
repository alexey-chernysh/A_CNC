package ru.android_cnc.acnc.Drivers.CanonicalCommands;

import android.graphics.Color;
import android.graphics.Paint;

/**
 * Created by Sales on 26.02.2015.
 */
public class DrawableAttributes {

    private Paint paintBefore;
    private Paint paintAfter;

    public DrawableAttributes(){
        paintBefore = new Paint();
        paintBefore.setColor(Color.BLUE);
        paintBefore.setAntiAlias(true);
        paintBefore.setStrokeWidth(6f);
        paintBefore.setStyle(Paint.Style.STROKE);
        paintBefore.setStrokeJoin(Paint.Join.ROUND);

        paintAfter  = new Paint();
        paintAfter.setColor(Color.YELLOW);
        paintBefore.setAntiAlias(true);
        paintBefore.setStrokeWidth(6f);
        paintBefore.setStyle(Paint.Style.STROKE);
        paintBefore.setStrokeJoin(Paint.Join.ROUND);
    }

    public Paint getPaintBefore(){ return paintBefore; }

    public Paint getPaintAfter(){ return paintAfter; }

    public void setScale(){}

    public void setLineWidth(){}
}
