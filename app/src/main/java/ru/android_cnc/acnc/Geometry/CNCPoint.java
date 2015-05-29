/*
 * @author Alexey Chernysh
 */

package ru.android_cnc.acnc.Geometry;

import ru.android_cnc.acnc.HAL.MotionController.CCommandArcLine;
import ru.android_cnc.acnc.HAL.MotionController.MotionControllerCommand;
import ru.android_cnc.acnc.HAL.MotionController.CCommandStraightLine;

import static java.lang.Math.sqrt;

public class CNCPoint {

    // 3D position
    // at 3 axis
	private double x_ = 0.0;
	private double y_ = 0.0;
    private double z_ = 0.0;
    // at 3 rotations
    private double a_ = 0.0;
    private double b_ = 0.0;
    private double c_ = 0.0;

    public CNCPoint(){}
	public CNCPoint(double x, double y){ this.x_ = x; this.y_ = y; }
    public CNCPoint(double x, double y, double z){ this.x_ = x; this.y_ = y; this.z_ = z; }
    public CNCPoint(double x, double y, double z, double a, double b, double c){ this.x_ = x; this.y_ = y; this.z_ = z; this.a_ = a; this.b_ = b; this.c_ = c;}

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

    public double getZ() {
        return z_;
    }
    public CNCPoint setZ(double z) {
        this.z_ = z;
        return this;
    }

    public double getA() {
        return a_;
    }
    public CNCPoint setA(double a) {
        this.a_ = a;
        return this;
    }

    public double getB() {
        return b_;
    }
    public CNCPoint setB(double b) {
        this.b_ = b;
        return this;
    }

    public double getC() {
        return c_;
    }
    public CNCPoint setC(double c) {
        this.c_ = c;
        return this;
    }

    public void shift(double dX, double dY){
        this.x_ += dX;
        this.y_ += dY;
    }

    @SuppressWarnings("CloneDoesntCallSuperClone")
    @Override
    public CNCPoint clone(){ return new CNCPoint(this.x_, this.y_, this.z_, this.a_, this.b_, this.c_); }

	public static double distance(CNCPoint p1, CNCPoint p2) {
		double dx = p1.getX() - p2.getX();
		double dy = p1.getY() - p2.getY();
        double dz = p1.getZ() - p2.getZ();
		return sqrt(dx * dx + dy * dy + dz * dz);
	}

    @Override
    public String toString(){
        return " X = " + x_ + "; Y = " + y_ + "; Z = " + z_ + ";";
    }

    public static CNCPoint getCrossingPoint(MotionControllerCommand line1, MotionControllerCommand line2){
        if(line1 instanceof CCommandStraightLine){
            if(line2 instanceof CCommandStraightLine)
                return getCrossLineNLine((CCommandStraightLine)line1, (CCommandStraightLine)line2);
            if(line2 instanceof CCommandArcLine)
                return getCrossLineNArc((CCommandStraightLine)line1, (CCommandArcLine)line2, ConnectionType.END_START);
        }
        if(line1 instanceof CCommandArcLine) {
            if(line2 instanceof CCommandStraightLine)
                return getCrossLineNArc((CCommandStraightLine)line2, (CCommandArcLine)line1, ConnectionType.START_END);
            if(line2 instanceof CCommandArcLine)
                return getCrossArcNArc((CCommandArcLine)line1, (CCommandArcLine)line2);
        }
        return null;
    }

    private static CNCPoint getCrossLineNLine(CCommandStraightLine line1, CCommandStraightLine line2) {
        if(line1 == null) return null;
        if(line1.length() <= 0) return null;
        if(line2 == null) return null;
        if(line2.length() <= 0) return null;

        // solve y = a*x + b equation for first line
        double x11 = line1.getStart().getX();
        double y11 = line1.getStart().getY();
        double x12 = line1.getEnd().getX();
        double y12 = line1.getEnd().getY();

        double dx1 = x12 - x11;

        double a1 = 0.0;
        double b1 = 0.0;
        boolean line1_is_vertical = false;
        if(dx1 != 0.0){
            b1 = (y11*x12 - y12*x11)/dx1;
            if(x11 != 0.0) a1 = (y11 - b1)/x11;
            else  a1 = (y12 - b1)/x12;
        } else line1_is_vertical = true;

        // solve y = a*x + b equation for second line
        double x21 = line2.getStart().getX();
        double y21 = line2.getStart().getY();
        double x22 = line2.getEnd().getX();
        double y22 = line2.getEnd().getY();

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

    private static CNCPoint getCrossLineNArc(CCommandStraightLine line,
                                            CCommandArcLine arc,
                                            ConnectionType type){
        // find connection point of line & circle nearest to end of one & start of another
        double rx;
        double ry;

        double arcCenterX = arc.getCenter().getX();
        double arcCenterY = arc.getCenter().getY();
        double arcR = arc.radius();

        double LineStartX = line.getStart().getX();
        double lineStartY = line.getStart().getY();
        double lineEndX = line.getEnd().getX();
        double lineEndY = line.getEnd().getY();

        double lineDX = lineEndX - LineStartX;
        double lineDY = lineEndY - lineStartY;

        CNCPoint solution1;
        CNCPoint solution2;
        if(Math.abs(lineDX)>0){  // line is not vertical
            if(Math.abs(lineDY)>0){ // line is not horizontal
                // solve line canonical equation y = a*x + b for a & b
                double a1;
                double b1 = (lineStartY*lineEndX - lineEndY*LineStartX)/lineDX;
                if(LineStartX != 0.0) a1 = (lineStartY - b1)/LineStartX;
                else  a1 = (lineEndY - b1)/lineEndX;

                // substitute line equation in circle equation
                // (x - xc)^2 + (y - yc)^2 = R^2
                // and solve square equation against x
                double aD = 1.0 + a1*a1;
                double byc = b1 - arcCenterY;
                double bD = 2.d*(byc*a1 - arcCenterX);
                double cD = arcCenterX*arcCenterX + byc*byc - arcR*arcR;
                double Det = bD*bD - 4.0*aD*cD;
                if(Det<0) return null;
                double rx1 = (-bD + Math.sqrt(Det))/2/aD;
                double ry1 = a1*rx1 + b1;
                solution1 = new CNCPoint(rx1,ry1);
                double rx2 = (-bD - Math.sqrt(Det))/2/aD;
                double ry2 = a1*rx2 + b1;
                solution2 = new CNCPoint(rx2,ry2);
            } else {
                // line is horizontal
                // connection is at point with y of line
                ry = lineEndY;
                double t = ry - arcCenterY;
                t = arcR*arcR - t*t;
                if(t>=0){
                    t = Math.sqrt(t);
                    double rx1 = arcCenterX + t;
                    solution1 = new CNCPoint(rx1,ry);
                    double rx2 = arcCenterX - t;
                    solution2 = new CNCPoint(rx2,ry);
                } else return null;
            }
        } else {
            // line is vertical
            // connection is at point with x of line
            rx = lineEndX;
            double t = rx - arcCenterX;
            t = arcR*arcR - t*t;
            if(t>=0){
                t = Math.sqrt(t);
                double ry1 = arcCenterY + t;
                solution1 = new CNCPoint(rx,ry1);
                double ry2 = arcCenterY - t;
                solution2 = new CNCPoint(rx,ry2);
            } else return null;
        }
        double dist1;
        double dist2;
        switch(type){
            case END_START:
                dist1 = distance(line.getEnd(),solution1) + distance(arc.getStart(),solution1);
                dist2 = distance(line.getEnd(),solution2) + distance(arc.getStart(),solution2);
                break;
            case START_END:
                dist1 = distance(line.getStart(),solution1) + distance(arc.getEnd(),solution1);
                dist2 = distance(line.getStart(),solution2) + distance(arc.getEnd(),solution2);
                break;
            default:
                return null;
        }
        if(dist1<dist2){
            return solution1;
        } else {
            return solution2;
        }
    }

    private static CNCPoint getCrossArcNArc(CCommandArcLine A1,
                                           CCommandArcLine A2){
        CNCPoint result;
//        Log.i("Crossing 2 arcs 1- ", A1.toString());
//        Log.i("Crossing 2 arcs 2- ", A2.toString());

        double dxsa1 = A1.getStart().getX() - A1.getCenter().getX();
        double dysa1 = A1.getStart().getY() - A1.getCenter().getY();
        double dxea1 = A1.getEnd().getX() - A1.getCenter().getX();
        double dyea1 = A1.getEnd().getY() - A1.getCenter().getY();
        double r2a1 = dxsa1*dxsa1 + dysa1*dysa1;
        double ra1 = Math.sqrt(r2a1);

        double dxsa2 = A2.getStart().getX() - A2.getCenter().getX();
        double dysa2 = A2.getStart().getY() - A2.getCenter().getY();
        double r2a2 = dxsa2*dxsa2 + dysa2*dysa2;
        double ra2 = Math.sqrt(r2a2);

        double dxc = A2.getCenter().getX() - A1.getCenter().getX();
        double dyc = A2.getCenter().getY() - A1.getCenter().getY();
        double ac = Math.atan2(dyc, dxc);
        double r2c = dxc*dxc + dyc*dyc;
        double rc = Math.sqrt(r2c);

        double overlap = rc - ra1 - ra2;
        double meps = 0.001; //Drawing.DwgConst.masheps;
        if(Math.abs(overlap) < meps){ // centers offseted, one connection point
            double xcp = A2.getStart().getX();
            double ycp = A2.getStart().getY();
            result = new CNCPoint(xcp, ycp);
        } else {
            if(rc < meps){ // centers are equal
                double ae1 = Math.atan2(dyea1, dxea1);
                double as2 = Math.atan2(dysa2, dxsa2);
                double acp = (ae1 + as2)/2d;
                double xcp = A1.getCenter().getX() + ra1*Math.cos(acp);
                double ycp = A1.getCenter().getY() + ra1*Math.sin(acp);
                result = new CNCPoint(xcp, ycp);
            } else {	// different centers, two connection point
                double d1 = (r2a1 - r2a2 + r2c)/(2d*rc);
                double t = r2a1 - d1*d1;
                if(t<0) t = 0;
                double h = Math.sqrt(t);
                double dah = Math.atan2(h, d1);
                // first point
                double xcp1 = A1.getCenter().getX() + ra1*Math.cos(ac-dah);
                double ycp1 = A1.getCenter().getY() + ra1*Math.sin(ac-dah);
                // second point
                double xcp2 = A1.getCenter().getX() + ra1*Math.cos(ac+dah);
                double ycp2 = A1.getCenter().getY() + ra1*Math.sin(ac+dah);
                double dx1 = A1.getEnd().getX() - xcp1;
                double dy1 = A1.getEnd().getY() - ycp1;
                double dx2 = A1.getEnd().getX() - xcp2;
                double dy2 = A1.getEnd().getY() - ycp2;
                if((dx1*dx1 + dy1*dy1)<(dx2*dx2 + dy2*dy2)){ // choose nearest point
                    result = new CNCPoint(xcp1, ycp1);
                }else{
                    result = new CNCPoint(xcp2, ycp2);
                }
            }
        }
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CNCPoint)) return false;

        CNCPoint point = (CNCPoint) o;

        if (Double.compare(point.a_, a_) != 0) return false;
        if (Double.compare(point.b_, b_) != 0) return false;
        if (Double.compare(point.c_, c_) != 0) return false;
        if (Double.compare(point.x_, x_) != 0) return false;
        if (Double.compare(point.y_, y_) != 0) return false;
        if (Double.compare(point.z_, z_) != 0) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(x_);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(y_);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(z_);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(a_);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(b_);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(c_);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    public enum ConnectionType {
        END_START,
        START_END
    }

}
