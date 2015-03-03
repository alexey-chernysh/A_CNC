/*
 * @author Alexey Chernysh
 */

package ru.android_cnc.acnc.Interpreter.Motion;

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
		return Math.sqrt(dx*dx + dy*dy);
	}

    @Override
    public String toString(){
        return " X = " + x_ + "; Y = " + y_ + ";";
    }

}
