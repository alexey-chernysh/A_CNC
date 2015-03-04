/*
 * @author Alexey Chernysh
 */

package ru.android_cnc.acnc.Geometry;

import android.util.Log;

import ru.android_cnc.acnc.Drivers.CanonicalCommands.CCommandArcLine;
import ru.android_cnc.acnc.Drivers.CanonicalCommands.CCommandStraightLine;

import static java.lang.Math.sqrt;

public class CNCPoint {
	
	private double x_;
	private double y_;
	
	public CNCPoint(double x, double y){ this.x_ = x; this.y_ = y;	}

	public double getX() { return x_; }

	public CNCPoint setX(double x) {
        this.x_ = x;
        return this;
    }

	public double getY() {
        return y_;
    }

	public CNCPoint setY(double y) {
        this.y_ = y;
        return this;
    }
	
	public void shift(double dX, double dY){
        this.x_ += dX;
        this.y_ += dY;
    }
	
	public CNCPoint clone(){ return new CNCPoint(this.x_, this.y_); }

	public static double distance(CNCPoint p1, CNCPoint p2) {
		double dx = p1.getX() - p2.getX();
		double dy = p1.getY() - p2.getY();
		return sqrt(dx * dx + dy * dy);
	}

    @Override
    public String toString(){
        return " X = " + x_ + "; Y = " + y_ + ";";
    }

    public static CNCPoint getCrossingPoint(CCommandStraightLine line1, CCommandStraightLine line2) {
        if(line1 == null) return null;
        if(line1.length() <= 0) return null;
        if(line2 == null) return null;
        if(line2.length() <= 0) return null;

        // solve y = a*x + b equation for first line
        double x11 = line1.getStart().getX();
        double y11 = line1.getStart().getY();
        double x12 = line1.getEnd().getX();
        double y12 = line1.getEnd().getY();
        Log.i("Crossing", "Line 1 x1 " + x11 + " y1 " + y11 + " x2 " + x12 + " y2 " + y12);

        double dx1 = x12 - x11;

        double a1 = 0.0;
        double b1 = 0.0;
        boolean line1_is_vertical = false;
        if(dx1 != 0.0){
            b1 = (y11*x12 - y12*x11)/dx1;
            if(x11 != 0.0) a1 = (y11 - b1)/x11;
            else  a1 = (y12 - b1)/x12;
        } else line1_is_vertical = true;
        Log.i("Crossing", "Line 1 a " + a1 + " b " + b1 + " v " + line1_is_vertical);

        // solve y = a*x + b equation for second line
        double x21 = line2.getStart().getX();
        double y21 = line2.getStart().getY();
        double x22 = line2.getEnd().getX();
        double y22 = line2.getEnd().getY();
        Log.i("Crossing", "Line 2 x1 " + x21 + " y1 " + y21 + " x2 " + x22 + " y2 " + y22);

        double dx2 = x22 - x21;

        double a2 = 0.0;
        double b2 = 0.0;
        boolean line2_is_vertical = false;
        if(dx2 != 0.0){
            b2 = (y21*x22 - y22*x21)/dx2;
            if(x21 != 0.0) a2 = (y21 - b2)/x21;
            else  a2 = (y22 - b2)/x22;
        }
        else line2_is_vertical = true;
        Log.i("Crossing", "Line 2 a " + a2 + " b " + b2 + " v " + line2_is_vertical);

        if(line1_is_vertical){
            if(line2_is_vertical){
                if(x11 == x21){
                    return new CNCPoint(x11, (y12+y21)/2);
                } else return null;
            } else {
                return new CNCPoint(x11, a2*x11 + b2);
            }
        } else {
            if(line2_is_vertical){
                return new CNCPoint(x21, a1*x21 + b1);
            } else {
                if(a1 == a2){
                    if(b1 == b2) {
                        return new CNCPoint((x12+x21)/2, (y12+y21)/2);
                    } else return null;
                } else {
                    double Y = (a2*b1 - a1*b2)/(a2-a1);
                    double X = a1*Y + b1;
                    return new CNCPoint(X, Y);
                }
            }
        }
    }
}
