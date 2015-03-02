package ru.android_cnc.acnc.GraphView;

import android.content.Context;
import android.graphics.Point;
import android.view.View;

import ru.android_cnc.acnc.Interpreter.Motion.CNCPoint;

/**
 * @author Alexey Chernysh
 */
public class CNCViewContext {

    private View parentView;
    private Context context_;
    private float scale;
    private float offset_x;
    private float offset_y;

    public CNCViewContext(View p, Context cntxt){
        parentView = p;
        context_ = cntxt;
        scale = 1.0f;
        offset_x = 0.0f;
        offset_y = 0.0f;
    }

    public Point toScreen(CNCPoint p){
        Point result = new Point();
        result.set((int) p.getX(), (int) p.getY());
        return result;
    }

    public void refresh() {
        context_ = parentView.getContext();
    }

    public Context getViewContext(){
        return context_;
    }

    public void setScale(float s) {
        scale = s;
    }

    public float getScale() {
        return scale;
    }

    public float getOffset_x() {
        return offset_x;
    }

    public void setOffset_x(float offset_x) {
        this.offset_x = offset_x;
    }

    public float getOffset_y() {
        return offset_y;
    }

    public void setOffset_y(float offset_y) {
        this.offset_y = offset_y;
    }

}
