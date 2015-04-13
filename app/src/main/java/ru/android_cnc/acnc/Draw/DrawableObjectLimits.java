package ru.android_cnc.acnc.Draw;

import ru.android_cnc.acnc.Interpreter.InterpreterException;
import ru.android_cnc.acnc.Geometry.CNCPoint;

/**
 * Created by Sales on 03.03.2015.
 */

public class DrawableObjectLimits {

    protected float top;
    protected float bottom;
    protected float left;
    protected float right;

    public DrawableObjectLimits(){
        this(Float.MAX_VALUE, -Float.MAX_VALUE, Float.MAX_VALUE, -Float.MAX_VALUE);
    }

    public DrawableObjectLimits(float l, float r, float b, float t){
        this.left = l;
        this.right = r;
        this.top = t;
        this.bottom = b;
    }

    public DrawableObjectLimits(CNCPoint point){
        this.top = (float)point.getY();
        this.bottom = (float)point.getY();
        this.left = (float)point.getX();
        this.right = (float)point.getX();
    }

    public static DrawableObjectLimits combine(DrawableObjectLimits limits1,
                                               DrawableObjectLimits limits2) throws InterpreterException {
        if(limits1 != null){
            if(limits2 != null){
                DrawableObjectLimits result = new DrawableObjectLimits(limits1.left, limits1.right, limits1.bottom, limits1.top);
                result = combine(result, new CNCPoint(limits2.left,limits2.bottom));
                result = combine(result, new CNCPoint(limits2.right,limits2.top));
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
        return "Limits: left = "   + left
                   + "; right = "  + right
                   + "; bottom = " + bottom
                   + "; top = "    + top
                   + ";";
    }

}
