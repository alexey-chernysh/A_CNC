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

    public CNCViewContext(View p, Context cntxt){
        parentView = p;
        context_ = cntxt;
    }

    public void refresh() {
        context_ = parentView.getContext();
    }

    public Context getViewContext(){
        return context_;
    }

}
