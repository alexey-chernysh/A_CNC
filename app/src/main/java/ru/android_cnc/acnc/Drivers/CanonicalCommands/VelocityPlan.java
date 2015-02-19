/*
 * @author Alexey Chernysh
 */

package ru.android_cnc.acnc.Drivers.CanonicalCommands;

public class VelocityPlan {
	
	private double startVel_;
	private double endVel_;
	
	public VelocityPlan(double sv, double ev){
		setStartVel(sv);
		setEndVel(ev);
	}

	public VelocityPlan(double v){
		setStartVel(v);
		setEndVel(v);
	}

	public double getStartVel() { return startVel_;	}

	public void setStartVel(double startVel) { this.startVel_ = startVel; }

	public double getEndVel() {	return endVel_;	}

	public void setEndVel(double endVel) { this.endVel_ = endVel; }

	public double get_dv(){ return (endVel_ - startVel_);}
}
