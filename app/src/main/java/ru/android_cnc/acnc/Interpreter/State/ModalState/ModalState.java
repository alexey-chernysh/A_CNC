/*
 * @author Alexey Chernysh
 */

package ru.android_cnc.acnc.Interpreter.State.ModalState;

import ru.android_cnc.acnc.Interpreter.Expression.ParamExpressionList;
import ru.android_cnc.acnc.Interpreter.Expression.Tokens.TokenParameter;
import ru.android_cnc.acnc.Interpreter.Expression.Variables.VariablesSet;
import ru.android_cnc.acnc.Interpreter.InterpreterException;
import ru.android_cnc.acnc.Geometry.CNCPoint;
import ru.android_cnc.acnc.Interpreter.State.InterpreterState;

public class ModalState {
	
	private static GCommandSet[] g_state_;
	private static MCommandSet[] m_state_;

    static {
        int i;
        g_state_ = new GCommandSet[GCommandModalGroupSet.G_GROUP0_NON_MODAL.ordinal()];
        for(i=0; i< GCommandModalGroupSet.G_GROUP0_NON_MODAL.ordinal(); i++)
            g_state_[i] = GCommandSet.GDUMMY;
        m_state_ = new MCommandSet[MCommandModalGroupSet.M_GROUP0_NON_MODAL.ordinal()];
        for(i=0; i< MCommandModalGroupSet.M_GROUP0_NON_MODAL.ordinal(); i++)
            m_state_[i] = MCommandSet.MDUMMY;
    }
		
	public ModalState(){
	};
		
	public void initToDefaultState() throws InterpreterException {
		
		set(GCommandModalGroupSet.G_GROUP1_MOTION, GCommandSet.G1);
		set(GCommandModalGroupSet.G_GROUP2_PLANE, GCommandSet.G17);
		set(GCommandModalGroupSet.G_GROUP3_DISTANCE_MODE, GCommandSet.G90);
		set(GCommandModalGroupSet.G_GROUP4_ARC_DISTANCE_MODE, GCommandSet.G91_1);
		set(GCommandModalGroupSet.G_GROUP5_FEED_RATE_MODE, GCommandSet.G94);
		set(GCommandModalGroupSet.G_GROUP6_UNITS, GCommandSet.G21);
		set(GCommandModalGroupSet.G_GROUP7_CUTTER_RADIUS_COMPENSATION, GCommandSet.G40);
		set(GCommandModalGroupSet.G_GROUP8_TOOL_LENGHT_OFFSET, GCommandSet.G49);
		set(GCommandModalGroupSet.G_GROUP9_CANNED_CYCLES, GCommandSet.G80);
		set(GCommandModalGroupSet.G_GROUP10_CANNED_CYCLES_RETURN_MODE, GCommandSet.G98);
		set(GCommandModalGroupSet.G_GROUP12_OFFSET_SELECTION, GCommandSet.G54);
		set(GCommandModalGroupSet.G_GROUP13_PATH_CONTROL_MODE, GCommandSet.G61);
		set(GCommandModalGroupSet.G_GROUP16_COORDINATE_ROTATION, GCommandSet.G69);
		set(GCommandModalGroupSet.G_GROUP17_POLAR_COORDINATES, GCommandSet.G16);
		set(GCommandModalGroupSet.G_GROUP18_SCALING, GCommandSet.G50);
		
		set(MCommandModalGroupSet.M_GROUP4_PROGRAM_CONTROL, MCommandSet.M98);
		set(MCommandModalGroupSet.M_GROUP6_TOOL_CHANGE, MCommandSet.M6);
		set(MCommandModalGroupSet.M_GROUP7_SPINDLE_TURNING, MCommandSet.M5);
		set(MCommandModalGroupSet.M_GROUP8_COOLANT, MCommandSet.M9);
		set(MCommandModalGroupSet.M_GROUP9_OVERRIDES, MCommandSet.M48);
		
	};
	
	public static void set(GCommandModalGroupSet group, GCommandSet command) throws InterpreterException{
		if(group != GCommandModalGroupSet.G_GROUP0_NON_MODAL){
			if(command.modalGroup == group){
				g_state_[group.ordinal()] = command;
			} else throw new InterpreterException("Changing modal state with command from another modal group");
		} else 
			throw new InterpreterException("Assiment non modal command to modal state");
		
	}
	
	public static GCommandSet getGModalState(GCommandModalGroupSet group){
		return g_state_[group.ordinal()];
	}
	
	public void set(MCommandModalGroupSet group, MCommandSet command) throws InterpreterException{
		if(group != MCommandModalGroupSet.M_GROUP0_NON_MODAL){
			if(command.modalGroup == group){
				m_state_[group.ordinal()] = command;
			} else throw new InterpreterException("Changing modal state with command from another modal group");
		} else throw new InterpreterException("Assiment non modal command to modal state");
		
	}
	
	public MCommandSet getMModalState(MCommandModalGroupSet group){
		return m_state_[group.ordinal()];
	}
	
	public double toMM(double x){
		switch(this.getGModalState(GCommandModalGroupSet.G_GROUP6_UNITS)){
		case G20:
			return 25.4*x;
		case G21:
		default:
			return x;
		}
	}

	public static boolean isPolar() {
		return (getGModalState(GCommandModalGroupSet.G_GROUP17_POLAR_COORDINATES) == GCommandSet.G15);
	};

	public static boolean isAbsolute(){
		// TODO G53 command needed
		return (getGModalState(GCommandModalGroupSet.G_GROUP3_DISTANCE_MODE) == GCommandSet.G90);
	}

	private static boolean isArcCenterRelative() {
		return (getGModalState(GCommandModalGroupSet.G_GROUP4_ARC_DISTANCE_MODE) == GCommandSet.G91_1);
	}

	public CNCPoint getTargetPoint(CNCPoint refCNCPoint, ParamExpressionList words) throws InterpreterException {
		CNCPoint result = null;
        if((!words.hasXYZ())&&(!words.hasXYZ())) return result;

        if(InterpreterState.modalState.isAbsolute()) result = refCNCPoint.clone();
        else result = new CNCPoint(); // new null point

		if(InterpreterState.modalState.isPolar()){
			throw new InterpreterException("Polar coordinates mode not realized yet!");
            // TODO polar coordinates mode should be implemented
		} else {
			// TODO axis rotation needed

            if(words.has(TokenParameter.X)){
                double x_param = words.get(TokenParameter.X);
                x_param = toMM(x_param);
                x_param *= VariablesSet.getScaleX();
                if(!InterpreterState.modalState.isAbsolute()) x_param += refCNCPoint.getX();
                result.setX(x_param);
            };

            if(words.has(TokenParameter.Y)){
				double y_param = words.get(TokenParameter.Y);
				y_param = toMM(y_param);
                y_param *= VariablesSet.getScaleX();
				if(!InterpreterState.modalState.isAbsolute()) y_param += refCNCPoint.getY();
				result.setY(y_param);
			};

            if(words.has(TokenParameter.Z)){
                double z_param = words.get(TokenParameter.Z);
                z_param = toMM(z_param);
                z_param *= VariablesSet.getScaleZ();
                if(!InterpreterState.modalState.isAbsolute()) z_param += refCNCPoint.getZ();
                result.setZ(z_param);
            };

            if(words.has(TokenParameter.A)){
                double a_param = words.get(TokenParameter.A);
                a_param = toMM(a_param);
                a_param *= VariablesSet.getScaleA();
                if(!InterpreterState.modalState.isAbsolute()) a_param += refCNCPoint.getA();
                result.setZ(a_param);
            };

            if(words.has(TokenParameter.B)){
                double b_param = words.get(TokenParameter.B);
                b_param = toMM(b_param);
                b_param *= VariablesSet.getScaleB();
                if(!InterpreterState.modalState.isAbsolute()) b_param += refCNCPoint.getB();
                result.setZ(b_param);
            };

            if(words.has(TokenParameter.C)){
                double c_param = words.get(TokenParameter.C);
                c_param = toMM(c_param);
                c_param *= VariablesSet.getScaleC();
                if(!InterpreterState.modalState.isAbsolute()) c_param += refCNCPoint.getC();
                result.setZ(c_param);
            };
		}
		return result;
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
				i_param = toMM(i_param);
				if(InterpreterState.modalState.isArcCenterRelative()) i_param += refCNCPoint.getX();
				resultCNCPoint.setX(i_param);
			};
			if(words.has(TokenParameter.J)){
				double j_param = 0;
				j_param = words.get(TokenParameter.J);
				j_param = toMM(j_param);
				if(InterpreterState.modalState.isArcCenterRelative()) j_param += refCNCPoint.getY();
				resultCNCPoint.setY(j_param);
			};
		}
		return resultCNCPoint;
	}

    public double getR(ParamExpressionList words) throws InterpreterException {
        if(words.has(TokenParameter.R)){
            return toMM(words.get(TokenParameter.R));
        } else return 0;
    }
}
