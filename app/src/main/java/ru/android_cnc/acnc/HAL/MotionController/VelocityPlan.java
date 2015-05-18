/*
 * @author Alexey Chernysh
 */

package ru.android_cnc.acnc.HAL.MotionController;

public class VelocityPlan {

    public static final VelocityPlanMode mode = VelocityPlanMode.CONSTANT_VELOCITY;

	private double startVel_;
	private double endVel_;

    private double[] x_step_plan = null;
    private int nx = 0;
    private Move x_move = Move.NONE;
    private double min_x_step = 0.0;

    private double[] y_step_plan;
    private int ny = 0;
    private Move y_move = Move.NONE;
    private double min_y_step = 0.0;

    public VelocityPlan(double v){
        setStartVel(v);
        setEndVel(v);
    }

    public VelocityPlan(double sv, double ev){
		setStartVel(sv);
		setEndVel(ev);
	}

    public void buildSteps(CCommandStraightLine line){
        double length = line.length();
        double dx = line.getDX();
        if(Math.abs(dx) > MotionControllerService.getX_mm_in_step()) {
            if(dx > 0.0) x_move = Move.FORWARD;
            else x_move = Move.BACKWARD;

            nx = (int)Math.round(dx/MotionControllerService.getX_mm_in_step());
            double x_step = length/nx;
            x_step_plan = new double[nx];
            x_step_plan[0] = x_step/2.0;
            for (int i=1; i<nx; i++)
                x_step_plan[i] = x_step_plan[i-1] + x_step;
            min_x_step = x_step;
        };

        double dy = line.getDY();
        if(Math.abs(dy) > MotionControllerService.getY_mm_in_step()) {
            if(dy > 0.0) y_move = Move.FORWARD;
            else y_move = Move.BACKWARD;

            ny = (int)Math.round(dy/MotionControllerService.getY_mm_in_step());
            double y_step = length/ny;
            y_step_plan = new double[ny];
            y_step_plan[0] = y_step/2.0;
            for (int i=1; i<ny; i++)
                y_step_plan[i] = y_step_plan[i-1] + y_step;
            min_y_step = y_step;
        };
    }

    public void buildSteps(CCommandArcLine arc){

        double R = arc.radius();
        double length = arc.length();
        double alfaStart = arc.getStartRadialAngle();
        double alfaEnd = arc.getEndRadialAngle();

        double dx = arc.getDX();
        if(Math.abs(dx) > MotionControllerService.getX_mm_in_step()) {
            if(dx > 0.0) x_move = Move.FORWARD;
            else x_move = Move.BACKWARD;

            nx = (int)(dx/MotionControllerService.getX_mm_in_step());
            x_step_plan = new double[nx];
            // find x coordinate of step points
            double x_step = MotionControllerService.getX_mm_in_step();
            if(x_move == Move.BACKWARD) x_step = - x_step;
            x_step_plan[0] = arc.getStart().getX() - arc.getCenter().getX() + x_step/2.0;
            for (int i=1; i<nx; i++)
                x_step_plan[i] = x_step_plan[i-1] + x_step;
            // find angle[i] for x[i]
            for (int i=0; i<nx; i++) {
                double alfa = Math.acos(x_step_plan[i] / R);
                if ((alfa >= alfaStart) && (alfa <= alfaEnd)) x_step_plan[i] = alfa;
                else x_step_plan[i] = -alfa;
            }
            // normalize to current arc
            double alfa = arc.angle();
            for (int i=0; i<nx; i++)
                x_step_plan[i] = length*(x_step_plan[i] - alfaStart)/alfa;
            min_y_step = Math.min(2*x_step_plan[0],2*(1.0-x_step_plan[nx-1]));
        };

        double dy = arc.getDY();
        if(Math.abs(dy) > MotionControllerService.getY_mm_in_step()) {
            if(dy > 0.0) y_move = Move.FORWARD;
            else y_move = Move.BACKWARD;

            ny = (int)(dy/MotionControllerService.getY_mm_in_step());
            y_step_plan = new double[ny];
            // find y coordinate of step points
            double y_step = MotionControllerService.getY_mm_in_step();
            if(y_move == Move.BACKWARD) y_step = - y_step;
            y_step_plan[0] = arc.getStart().getX() - arc.getCenter().getY() + y_step/2.0;
            for (int i=1; i<ny; i++)
                y_step_plan[i] = y_step_plan[i-1] + y_step;
            // find angle[i] for y[i]
            for (int i=0; i<ny; i++) {
                double alfa = Math.acos(y_step_plan[i] / R);
                if ((alfa >= alfaStart) && (alfa <= alfaEnd)) y_step_plan[i] = alfa;
                else y_step_plan[i] = -alfa;
            }
            // normalize to current arc
            double alfa = arc.angle();
            for (int i=0; i<ny; i++)
                y_step_plan[i] = length*(y_step_plan[i] - alfaStart)/alfa;
            min_y_step = Math.min(2.0*y_step_plan[0],2.0*(1.0-y_step_plan[ny-1]));
        };

    }

    public double getStartVel() { return startVel_;	}

	public void setStartVel(double startVel) { this.startVel_ = startVel; }

	public double getEndVel() {	return endVel_;	}

	public void setEndVel(double endVel) { this.endVel_ = endVel; }

    public enum VelocityPlanMode{
        CONSTANT_VELOCITY,
        TRAPEZOIDAL,
        S_CURVE
    }

    public enum Move{
        NONE,
        FORWARD,
        BACKWARD
    }
}