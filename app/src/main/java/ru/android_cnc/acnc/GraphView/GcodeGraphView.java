package ru.android_cnc.acnc.GraphView;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import ru.android_cnc.acnc.Draw.DrawableObjectLimits;
import ru.android_cnc.acnc.Interpreter.ProgramLoader;

/**
 * @author Alexey Chernysh
 */
public class GcodeGraphView extends View {

    private float scale;
    private float offset_x;
    private float offset_y;

    private ScaleGestureDetector mScaleDetector;

    public GcodeGraphView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
        scale = 1.0f;
        offset_x = 0.0f;
        offset_y = 0.0f;
    }

    public void setParams(float p1, float p2){
    }

    @Override
    protected void onDraw(Canvas canvas) {
/*
        canvas.save();
        canvas.scale(scale, -scale);
        canvas.translate(offset_x, offset_y);
        ProgramLoader.command_sequence.draw(canvas);
        canvas.restore();
*/
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        final float padding = 0.1f;
        if(ProgramLoader.command_sequence == null)return;
        DrawableObjectLimits limits = ProgramLoader.command_sequence.getLimits();
        float left   = limits.getLeft();
        float right  = limits.getRight();
        float top    = limits.getTop();
        float bottom = limits.getBottom();
        float width  = right - left;
        float height = top   - bottom;
        left   -= width  * padding;
        right  += width  * padding;
        bottom -= height * padding;
        top    += height * padding;
        float width_with_pad  = right - left;
        float height_with_pad = top   - bottom;

        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        scale = Math.min(widthSize/width_with_pad, heightSize/height_with_pad);
        offset_x = (widthSize /scale  - width )/2.0f;
        offset_y = (heightSize/scale  - height)/4.0f - height_with_pad;

//        Log.i("Part mesure: ", " width = " + width + ", height = " + height);
//        Log.i("View mesure: ", " width = " + widthSize + ", height = " + heightSize);
    }

    private float x_on_down = 0.0f;
    private float y_on_down = 0.0f;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Let the ScaleGestureDetector inspect all events.
        mScaleDetector.onTouchEvent(event);

        float eventX = event.getX();
        float eventY = event.getY();

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                x_on_down = event.getX();
                y_on_down = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                offset_x += (eventX - x_on_down)/scale;
                x_on_down = eventX;
                offset_y -= (eventY - y_on_down)/scale;
                y_on_down = eventY;
                break;
            default:
                return false;
        }
        invalidate();
        return true;
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            scale *= detector.getScaleFactor();

            // Don't let the object get too small or too large.
            scale = Math.max(0.1f, Math.min(scale, 10.0f));

            invalidate();
            return true;
        }
    }}
