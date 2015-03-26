package ru.android_cnc.acnc.Geometry;

import java.util.Stack;

/**
 * Created by Sales on 26.03.2015.
 */

public class Rotation {

    private static Rotation ourInstance = new Rotation();
    private Stack<CoordinateRotation> stack_;

    public static Rotation getInstance() {
        return ourInstance;
    }

    private Rotation() {
        stack_ = new Stack<CoordinateRotation>();
        CoordinateRotation nullRotation = new CoordinateRotation(0.0, 0.0, 0.0);
        stack_.push(nullRotation);
    }

    public void add(double x, double y, double a){
        CoordinateRotation newRotation = new CoordinateRotation(x,y,a);
        stack_.push(newRotation);
    }

    public void replace(double x, double y, double a){
        stack_.pop();
        CoordinateRotation newRotation = new CoordinateRotation(x,y,a);
        stack_.push(newRotation);
    }

    public void cancel(){
        stack_.pop();
        if(stack_.empty()){
            CoordinateRotation newRotation = new CoordinateRotation(0.0, 0.0, 0.0);
            stack_.push(newRotation);
        }
    }

    public CNCPoint apply(CNCPoint source){
        CNCPoint result = source.clone();
        int size = stack_.size();
        for(int i=0; i<size; i++)
            result = stack_.get(i).rotate(result);
        return result;
    }

    private class CoordinateRotation {
        // reference point coordinates
        double refX_ = 0.0;
        double refY_ = 0.0;
        // rotation angle in degrees
        double angle_ = 0.0;

        public CoordinateRotation(double refX, double refY, double angle){
            this.refX_ = refX;
            this.refY_ = refY;
            this.angle_ = angle;
        }

        public CNCPoint rotate(CNCPoint source){
            CNCPoint result = source.clone();
            double x = result.getX() - refX_;
            double y = result.getY() - refY_;
            double a = Math.toDegrees(angle_);
            double cosA = Math.cos(a);
            double sinA = Math.sin(a);
            result.setX(x*cosA - y*sinA + refX_);
            result.setY(x*sinA + y*cosA + refY_);
            return result;
        }
    }
}
