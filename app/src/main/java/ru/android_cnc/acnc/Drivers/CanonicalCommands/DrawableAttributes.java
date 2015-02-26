package ru.android_cnc.acnc.Drivers.CanonicalCommands;

import android.graphics.Color;
import android.graphics.Paint;

import ru.android_cnc.acnc.Interpreter.State.CutterRadiusCompensation;

/**
 * Created by Sales on 26.02.2015.
 */

public class DrawableAttributes {

    private static Paint paintBeforeWork;
    private static Paint paintBeforeFree;
    private static Paint paintAfterWork;
    private static Paint paintAfterFree;

    static {
        paintBeforeWork = new Paint();
        paintBeforeWork.setColor(Color.BLUE);
        paintBeforeWork.setAntiAlias(true);
        paintBeforeWork.setStrokeWidth(6f);
        paintBeforeWork.setStyle(Paint.Style.STROKE);
        paintBeforeWork.setStrokeJoin(Paint.Join.ROUND);

        paintAfterWork  = new Paint();
        paintAfterWork.setColor(Color.YELLOW);
        paintAfterWork.setAntiAlias(true);
        paintAfterWork.setStrokeWidth(6f);
        paintAfterWork.setStyle(Paint.Style.STROKE);
        paintAfterWork.setStrokeJoin(Paint.Join.ROUND);

        paintBeforeFree = new Paint();
        paintBeforeFree.setColor(Color.LTGRAY);
        paintBeforeFree.setAntiAlias(true);
        paintBeforeFree.setStrokeWidth(1);
        paintBeforeFree.setStyle(Paint.Style.STROKE);
        paintBeforeFree.setStrokeJoin(Paint.Join.ROUND);

        paintAfterFree  = new Paint();
        paintAfterFree.setColor(Color.DKGRAY);
        paintAfterFree.setAntiAlias(true);
        paintAfterFree.setStrokeWidth(1);
        paintAfterFree.setStyle(Paint.Style.STROKE);
        paintAfterFree.setStrokeJoin(Paint.Join.ROUND);
    }

    public static Paint getPaintBefore(CutterRadiusCompensation crc){
        if(crc.getMode() == CutterRadiusCompensation.mode.OFF) return paintBeforeFree;
        else return paintBeforeWork;
    }

    public static Paint getPaintAfter(CutterRadiusCompensation crc){
        if(crc.getMode() == CutterRadiusCompensation.mode.OFF) return paintAfterFree;
        return paintAfterWork;
    }

    public static void setScale(double scale){}

    private static void setLineWidth(int width){
        paintBeforeWork.setStrokeWidth(width);
        paintAfterWork.setStrokeWidth(width);
    }
}
