package ru.android_cnc.acnc.GraphView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Sales on 25.02.2015.
 */
public class GcodeGraphView extends View {
    Paint paint = new Paint();
    float startX;
    float startY;
    float stopX;
    float stopY;

    public GcodeGraphView(Context context, AttributeSet attrs) {
        super(context, attrs);

        paint.setAntiAlias(true);
        paint.setStrokeWidth(6f);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
    }

    public void setParams(float p1, float p2){
        startX = startY = p1;
        stopX  = stopY  = p2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawLine(startX, startY, stopX, stopY, paint);
    }
/*
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = event.getX();
                startY = event.getY();
                return true;
            case MotionEvent.ACTION_MOVE:
                stopX = event.getX();
                stopY = event.getY();
                break;
            case MotionEvent.ACTION_UP:
                stopX = event.getX();
                stopY = event.getY();
                break;
            default:
                return false;
        }
        invalidate();
        return true;
    }
*/
}
