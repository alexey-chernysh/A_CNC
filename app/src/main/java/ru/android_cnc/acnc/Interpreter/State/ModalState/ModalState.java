/*
 * @author Alexey Chernysh
 */

package ru.android_cnc.acnc.Interpreter.State.ModalState;

import ru.android_cnc.acnc.Interpreter.Expression.ParamExpressionList;
import ru.android_cnc.acnc.Interpreter.Expression.Tokens.TokenParameter;
import ru.android_cnc.acnc.Interpreter.InterpreterException;
import ru.android_cnc.acnc.Interpreter.Motion.CNCPoint;
import ru.android_cnc.acnc.Interpreter.State.InterpreterState;

public class ModalState {
	
	private GcommandSet[] g_state_;
	private McommandSet[] m_state_;
		
	public ModalState(){
		int i;
		g_state_ = new GcommandSet[GcommandModalGroupSet.G_GROUP0_NON_MODAL.ordinal()];
		for(i=0; i<GcommandModalGroupSet.G_GROUP0_NON_MODAL.ordinal(); i++)
			g_state_[i] = GcommandSet.GDUMMY;
		m_state_ = new McommandSet[McommandModalGroupSet.M_GROUP0_NON_MODAL.ordinal()];
		for(i=0; i<McommandModalGroupSet.M_GROUP0_NON_MODAL.ordinal(); i++)
			m_state_[i] = McommandSet.MDUMMY;
	};
		
	public void initToDefaultState() throws InterpreterException {
		
		set(GcommandModalGroupSet.G_GROUP1_MOTION, GcommandSet.G1);
		set(GcommandModalGroupSet.G_GROUP2_PLANE, GcommandSet.G17);
		set(GcommandModalGroupSet.G_GROUP3_DISTANCE_MODE, GcommandSet.G90);
		set(GcommandModalGroupSet.G_GROUP4_ARC_DISTANCE_MODE, GcommandSet.G91_1);
		set(GcommandModalGroupSet.G_GROUP5_FEED_RATE_MODE, GcommandSet.G94);
		set(GcommandModalGroupSet.G_GROUP6_UNITS, GcommandSet.G21);
		set(GcommandModalGroupSet.G_GROUP7_CUTTER_RADIUS_COMPENSATION, GcommandSet.G40);
		set(GcommandModalGroupSet.G_GROUP8_TOOL_LENGHT_OFFSET, GcommandSet.G49);
		set(GcommandModalGroupSet.G_GROUP9_CANNED_CYCLES, GcommandSet.G80);
		set(GcommandModalGroupSet.G_GROUP10_CANNED_CYCLES_RETURN_MODE, GcommandSet.G98);
		set(GcommandModalGroupSet.G_GROUP12_OFFSET_SELECTION, GcommandSet.G54);
		set(GcommandModalGroupSet.G_GROUP13_PATH_CONTROL_MODE, GcommandSet.G61);
		set(GcommandModalGroupSet.G_GROUP16_COORDINATE_ROTATION, GcommandSet.G69);
		set(GcommandModalGroupSet.G_GROUP17_POLAR_COORDINATES, GcommandSet.G16);
		set(GcommandModalGroupSet.G_GROUP18_SCALING, GcommandSet.G50);
		
		set(McommandModalGroupSet.M_GROUP4_PROGRAM_CONTROL, McommandSet.M98);
		set(McommandModalGroupSet.M_GROUP6_TOOL_CHANGE, McommandSet.M6);
		set(McommandModalGroupSet.M_GROUP7_SPINDLE_TURNING, McommandSet.M5);
		set(McommandModalGroupSet.M_GROUP8_COOLANT, McommandSet.M9);
		set(McommandModalGroupSet.M_GROUP9_OVERRIDES, McommandSet.M48);
		
	};
	
	public void set(GcommandModalGroupSet group, GcommandSet command) throws InterpreterException{
		if(group != GcommandModalGroupSet.G_GROUP0_NON_MODAL){
			if(command.modalGroup == group){
				g_state_[group.ordinal()] = command;
			} else throw new InterpreterException("Changing modal state with command from another modal group");
		} else 
			throw new InterpreterException("Assiment non modal command to modal state");
		
	}
	
	public GcommandSet getGModalState(GcommandModalGroupSet group){
		return g_state_[group.ordinal()];
	}
	
	public void set(McommandModalGroupSet group, McommandSet command) throws InterpreterException{
		if(group != McommandModalGroupSet.M_GROUP0_NON_MODAL){
			if(command.modalGroup == group){
				m_state_[group.ordinal()] = command;
			} else throw new InterpreterException("Changing modal state with command from another modal group");
		} else throw new InterpreterException("Assiment non modal command to modal state");
		
	}
	
	public McommandSet getMModalState(McommandModalGroupSet group){
		return m_state_[group.ordinal()];
	}
	
	public double toMM(double x){
		switch(this.getGModalState(GcommandModalGroupSet.G_GROUP6_UNITS)){
		case G20:
			return 25.4*x;
		case G21:
		default:
			return x;
		}
	}

	public boolean isPolar() {
		return (this.getGModalState(GcommandModalGroupSet.G_GROUP17_POLAR_COORDINATES) == GcommandSet.G15);
	};

	public boolean isAbsolute(){
		// TODO G53 command needed
		return (this.getGModalState(GcommandModalGroupSet.G_GROUP3_DISTANCE_MODE) == GcommandSet.G90);
	}

	private boolean isArcCenterRelative() {
		return (this.getGModalState(GcommandModalGroupSet.G_GROUP4_ARC_DISTANCE_MODE) == GcommandSet.G91_1);
	}

	public CNCPoint getTargetPoint(CNCPoint refCNCPoint, ParamExpressionList words) throws InterpreterException {
		CNCPoint resultCNCPoint = refCNCPoint.clone();
		if(InterpreterState.modalState.isPolar()){
			throw new InterpreterException("Polar coorfimates mode not realized yet!");
		} else {
			// TODO axis rotation needed
			if(words.has(TokenParameter.X)){
				double x_param = 0;
				x_param = words.get(TokenParameter.X);
				x_param = InterpreterState.modalState.toMM(x_param);
				if(!InterpreterState.modalState.isAbsolute()) x_param += refCNCPoint.getX();
				resultCNCPoint.setX(x_param);
			};
			if(words.has(TokenParameter.Y)){
				double y_param = 0;
				y_param = words.get(TokenParameter.Y);
				y_param = InterpreterState.modalState.toMM(y_param);
				if(!InterpreterState.modalState.isAbsolute()) y_param += refCNCPoint.getY();
				resultCNCPoint.setY(y_param);
			};
		}
		return resultCNCPoint;
	}

	public CNCPoint getCenterPoint(CNCPoint refCNCPoint, ParamExpressionList words) throws InterpreterException {
		CNCPoint resultCNCPoint = refCNCPoint.clone();
		if(InterpreterState.modalState.isPolar()){
			throw new InterpreterException("Arc motion incompatible with polar coorfimates mode!");
		} else {
			// TODO axis rotation needed
			if(words.has(TokenParameter.I)){
				double i_param = 0;
				i_param = words.get(TokenParameter.I);
				i_param = InterpreterState.modalState.toMM(i_param);
				if(InterpreterState.modalState.isArcCenterRelative()) i_param += refCNCPoint.getX();
				resultCNCPoint.setX(i_param);
			};
			if(words.has(TokenParameter.J)){
				double j_param = 0;
				j_param = words.get(TokenParameter.J);
				j_param = InterpreterState.modalState.toMM(j_param);
				if(InterpreterState.modalState.isArcCenterRelative()) j_param += refCNCPoint.getY();
				resultCNCPoint.setY(j_param);
			};
		}
		return resultCNCPoint;
	}

}
