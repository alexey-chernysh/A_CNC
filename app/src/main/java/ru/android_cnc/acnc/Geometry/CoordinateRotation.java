/*
 * @author Alexey Chernysh
 */

package ru.android_cnc.acnc.Geometry;

import java.util.Stack;

public class CoordinateRotation {

    private static CoordinateRotation ourInstance = new CoordinateRotation();
    private static Stack<Rotation> stack_;

    public static CoordinateRotation getInstance() {
        return ourInstance;
    }

    private CoordinateRotation() {
        stack_ = new Stack<Rotation>();
        Rotation nullRotation = new Rotation(0.0, 0.0, 0.0);
        stack_.push(nullRotation);
    }

    public void add(double x, double y, double a){
        Rotation newRotation = new Rotation(x,y,a);
        stack_.push(newRotation);
    }

    public void replace(double x, double y, double a){
        stack_.pop();
        Rotation newRotation = new Rotation(x,y,a);
        stack_.push(newRotation);
    }

    public void cancel(){
        stack_.pop();
        if(stack_.empty()){
            Rotation newRotation = new Rotation(0.0, 0.0, 0.0);
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

    private class Rotation {
        // reference point coordinates
        double refX_ = 0.0;
        double refY_ = 0.0;
        // rotation angle in degrees
        double angle_ = 0.0;

        public Rotation(double refX, double refY, double angle){
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
