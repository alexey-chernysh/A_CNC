package ru.android_cnc.acnc.GraphView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import ru.android_cnc.acnc.Interpreter.Motion.CNCPoint;
import ru.android_cnc.acnc.Interpreter.ProgramLoader;

/**
 * @author Alexey Chernysh
 */
public class GcodeGraphView extends View {

    private CNCViewContext viewContext;
    private float scale;
    private float offset_x;
    private float offset_y;

    private ScaleGestureDetector mScaleDetector;
    private float mScaleFactor = 1.f;

    public GcodeGraphView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
        viewContext = new CNCViewContext(this, context);
        scale = 1.0f;
        offset_x = 0.0f;
        offset_y = 0.0f;
    }

    public void setParams(float p1, float p2){
    }

    @Override
    protected void onDraw(Canvas canvas) {
        viewContext.refresh();
        canvas.save();
        // TODO
        canvas.scale(mScaleFactor*scale, -mScaleFactor*scale);
        canvas.translate(offset_x, offset_y);
        ProgramLoader.command_sequence.draw(viewContext, canvas);
        canvas.restore();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        final float padding = 0.1f;
        float left   = (float)ProgramLoader.command_sequence.getLeftBorder();
        float right  = (float)ProgramLoader.command_sequence.getRightBorder();
        float top    = (float)ProgramLoader.command_sequence.getTopBorder();
        float bottom = (float)ProgramLoader.command_sequence.getBottomBorder();
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
        offset_y = (heightSize/scale  - height)/2.0f - height_with_pad;

        Log.i("Part mesure: ", " width = " + width + ", height = " + height);
        Log.i("View mesure: ", " width = " + widthSize + ", height = " + heightSize);
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

    private class ScaleListener
            extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            mScaleFactor *= detector.getScaleFactor();

            // Don't let the object get too small or too large.
            mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 5.0f));

            invalidate();
            return true;
        }
    }}
