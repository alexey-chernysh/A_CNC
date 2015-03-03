package ru.android_cnc.acnc.Draw;

import android.util.Log;

import ru.android_cnc.acnc.Interpreter.InterpreterException;
import ru.android_cnc.acnc.Interpreter.Motion.CNCPoint;

/**
 * Created by Sales on 03.03.2015.
 */

public class DrawableObjectLimits {

    protected float top;
    protected float bottom;
    protected float left;
    protected float right;

    public DrawableObjectLimits(float l, float r, float b, float t){
        left = l;
        right = r;
        top = t;
        bottom = b;
    }

    public DrawableObjectLimits(CNCPoint point){
        top = (float)point.getY();
        bottom = (float)point.getY();
        left = (float)point.getX();
        right = (float)point.getX();
    }

    public static DrawableObjectLimits combine(DrawableObjectLimits limits1,
                                               DrawableObjectLimits limits2) throws InterpreterException {
        if(limits1 != null){
            if(limits2 != null){
                DrawableObjectLimits result = new DrawableObjectLimits(limits1.left, limits1.right, limits1.bottom, limits1.top);
                result = combine(result, new CNCPoint(limits2.left,limits2.bottom));
                result = combine(result, new CNCPoint(limits2.right,limits2.top));
//                Log.i("Combine, 1 -", object1.toString());
//                Log.i("2 -", object2.toString());
//                Log.i("Result -", result.toString());
                return result;
            } else return limits1;
        }
        else
            if(limits2 != null) return limits2;
            else throw new InterpreterException("Null limits combine");
    }

    public static DrawableObjectLimits combine(DrawableObjectLimits limits, CNCPoint point) throws InterpreterException {
        if(limits != null){
            if(point != null){
                DrawableObjectLimits result = new DrawableObjectLimits(limits.left, limits.right, limits.bottom, limits.top);
                result.top    = Math.max(result.top,    (float)point.getY());
                result.right  = Math.max(result.right,  (float)point.getX());
                result.bottom = Math.min(result.bottom, (float)point.getY());
                result.left   = Math.min(result.left,   (float)point.getX());
//                Log.i("Combine with point", result.toString());
//                Log.i("Point is ", point.toString());
                return result;
            } else return limits;
        }
        else throw new InterpreterException("Null limitss with point combine");
    }

    public float getTop() {
        return top;
    }

    public float getBottom() {
        return bottom;
    }

    public float getLeft() {
        return left;
    }

    public float getRight() {
        return right;
    }

    @Override
    public String toString(){
        return " left = " + left + "; right = " + right + "; botton = " + bottom + "; top = " + top + ";";
    }

}
