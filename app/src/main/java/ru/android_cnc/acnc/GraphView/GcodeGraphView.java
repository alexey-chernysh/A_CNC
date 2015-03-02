package ru.android_cnc.acnc.GraphView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import ru.android_cnc.acnc.Interpreter.Motion.CNCPoint;
import ru.android_cnc.acnc.Interpreter.ProgramLoader;

/**
 * @author Alexey Chernysh
 */
public class GcodeGraphView extends View {

    private CNCViewContext viewContext;

    public GcodeGraphView(Context context, AttributeSet attrs) {
        super(context, attrs);
        viewContext = new CNCViewContext(this, context);
    }

    public void setParams(float p1, float p2){
    }

    @Override
    protected void onDraw(Canvas canvas) {
        viewContext.refresh();
        canvas.save();
        // TODO
        canvas.scale(viewContext.getScale(), viewContext.getScale());
        canvas.translate(viewContext.getOffset_x(),viewContext.getOffset_y());
        ProgramLoader.command_sequence.draw(viewContext, canvas);
        canvas.restore();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        final double padding = 0.1;
        double left = ProgramLoader.command_sequence.getLeftBorder();
        double right = ProgramLoader.command_sequence.getRightBorder();
        double top = ProgramLoader.command_sequence.getTopBorder();
        double bottom = ProgramLoader.command_sequence.getBottomBorder();
        double width = right - left;
        double height = top - bottom;
        left   -= width  * padding;
        right  += width  * padding;
        bottom -= height * padding;
        top    += height * padding;

        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        double scale_x = widthSize/(right - left);
        double scale_y = heightSize/(top - bottom);
        float scale = (float)Math.min(scale_x,scale_y);
        viewContext.setScale(scale);
        viewContext.setOffset_x((float)((widthSize  - scale*width )/2.0/scale));
        viewContext.setOffset_y((float)((heightSize - scale*height)/2.0/scale));

//        float[] matrix = viewContext.getMatrix();

        Log.i("Part mesure: ", " width = " + width + ", height = " + height);
        Log.i("View mesure: ", " width = " + widthSize + ", height = " + heightSize);
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
