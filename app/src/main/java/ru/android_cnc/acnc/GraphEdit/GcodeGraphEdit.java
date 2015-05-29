/*
 * @author Alexey Chernysh
 */

package ru.android_cnc.acnc.GraphEdit;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class GcodeGraphEdit extends View {
    Paint paint = new Paint();
    float startX;
    float startY;
    float stopX;
    float stopY;

    public GcodeGraphEdit(Context context, AttributeSet attrs) {
        super(context, attrs);

        paint.setAntiAlias(true);
        paint.setStrokeWidth(6f);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawLine(startX, startY, stopX, stopY, paint);
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {

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
}
