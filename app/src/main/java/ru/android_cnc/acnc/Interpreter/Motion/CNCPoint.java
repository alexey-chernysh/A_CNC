/*
 * @author Alexey Chernysh
 */

package ru.android_cnc.acnc.Interpreter.Motion;

public class CNCPoint {
	
	private double x_;
	private double y_;
	
	public CNCPoint(double x, double y){ this.x_ = x; this.y_ = y;	}

	public double getX() { return x_; }

	public void setX(double x) { this.x_ = x; }

	public double getY() { return y_; }

	public void setY(double y) { this.y_ = y; }
	
	public void shift(double dX, double dY){ this.x_ += dX; this.y_ += dY; }
	
	public CNCPoint clone(){ return new CNCPoint(this.x_, this.y_); }

	public double getDistance(CNCPoint p) {
		double dx = this.getX() - p.getX();
		double dy = this.getY() - p.getY();
		return Math.sqrt(dx*dx + dy*dy);
	}

}
