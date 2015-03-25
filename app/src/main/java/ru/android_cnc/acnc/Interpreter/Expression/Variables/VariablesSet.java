/*
 * @author Alexey Chernysh
 */

package ru.android_cnc.acnc.Interpreter.Expression.Variables;

import ru.android_cnc.acnc.Interpreter.Expression.Tokens.TokenParameter;
import ru.android_cnc.acnc.Interpreter.InterpreterException;
import ru.android_cnc.acnc.Geometry.CNCPoint;

public class VariablesSet {
	
	private static final int G28HomePos_ = 5160;
	private static final int G30HomePos_ = 5180;
	private static final int ScalePos_ = 5190;
	private static final int G92OffsetPos_ = 5210;
	private static final int currentWorkOffsetsNumPos_ = 5220;
	private static final int shift_ = 20;
	private static VarArray va = new VarArray();
	public static final int maxToolNumber = 255;
	
	public VariablesSet() throws InterpreterException {
        setScale(1.0, 1.0, 1.0, 1.0, 1.0, 1.0);
        set(currentWorkOffsetsNumPos_, 1.0);
        for(int i=1; i<=maxToolNumber;i++)
            setWorkingToolOffset(i, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
	}
	
	public double get(int num) throws InterpreterException{
		return this.va.get(num);
	}
	
	public void set(int num, double value) throws InterpreterException{
		this.va.set(num, value);
	}

    private static final int offset_base = 0;
	private static final int offset_X = 1;
	private static final int offset_Y = 2;
	private static final int offset_Z = 3;
	private static final int offset_A = 4;
	private static final int offset_B = 5;
	private static final int offset_C = 6;
    private static final int offset_D = 7;

    private static void setBase(int base, double value) throws InterpreterException{
        va.set(base + offset_base, value);
    }
	private static void setX(int base, double value) throws InterpreterException{
		va.set(base + offset_X, value);
	}
	private static void setY(int base, double value) throws InterpreterException{
		va.set(base + offset_Y, value);
	}
	private static void setZ(int base, double value) throws InterpreterException{
		va.set(base + offset_Z, value);
	}
	private static void setA(int base, double value) throws InterpreterException{
		va.set(base + offset_A, value);
	}
	private static void setB(int base, double value) throws InterpreterException{
		va.set(base + offset_B, value);
	} 
	private static void setC(int base, double value) throws InterpreterException{
		va.set(base + offset_C, value);
	}
    private static void setD(int base, double value) throws InterpreterException{
        va.set(base + offset_D, value);
    }

    private static double getBase(int base) throws InterpreterException{
        return va.get(base + offset_base);
    }
	private static double getX(int base) throws InterpreterException{
		return va.get(base + offset_X);
	}
	private static double getY(int base) throws InterpreterException{
		return va.get(base + offset_Y);
	}
	private static double getZ(int base) throws InterpreterException{
		return va.get(base + offset_Z);
	}
	private static double getA(int base) throws InterpreterException{
		return va.get(base + offset_A);
	}
	private static double getB(int base) throws InterpreterException{
		return va.get(base + offset_B);
	}
	private static double getC(int base) throws InterpreterException{
		return va.get(base + offset_C);
	}
    private static double getD(int base) throws InterpreterException{
        return va.get(base + offset_D);
    }

	public void setToolFixtureOffset(int L, TokenParameter tp, double value) throws InterpreterException{
		int varPosition = currentWorkOffsetsNumPos_ + (L-1)*shift_;
		switch(tp){
		case X:
			this.setX(varPosition, value);
			break;
		case Y:
			this.setY(varPosition, value);
			break;
		case Z:
			this.setZ(varPosition, value);
			break;
		case A:
			this.setA(varPosition, value);
			break;
		case B:
			this.setB(varPosition, value);
			break;
		case C:
			this.setC(varPosition, value);
			break;
		default:
			throw new InterpreterException("Illegal alfa word (" + tp.toString() + ") in G10");
		}
	}

    // G10 implementation
	public void setWorkingToolOffset(int P, double X, double Y, double Z, double A, double B, double C) throws InterpreterException{
		if((P>0)&(P<=VariablesSet.maxToolNumber)){
			int varPosition = currentWorkOffsetsNumPos_ + (P-1)*shift_;
			this.setX(varPosition, X);
			this.setY(varPosition, Y);
			this.setZ(varPosition, Z);
			this.setA(varPosition, A);
			this.setB(varPosition, B);
			this.setC(varPosition, C);
		}
	}
	
	public void setScale(double X, double Y, double Z, double A, double B, double C) throws InterpreterException{
			int varPosition = ScalePos_;
			this.setX(varPosition, X);
			this.setY(varPosition, Y);
			this.setZ(varPosition, Z);
			this.setA(varPosition, A);
			this.setB(varPosition, B);
			this.setC(varPosition, C);
	}
	
	public static double getScaleX() throws InterpreterException{
		return getX(ScalePos_);
	}
	public static double getScaleY() throws InterpreterException{
		return getY(ScalePos_);
	}
	public static double getScaleZ() throws InterpreterException{
		return getZ(ScalePos_);
	}
    public static double getScaleA() throws InterpreterException{
        return getA(ScalePos_);
    }
    public static double getScaleB() throws InterpreterException{
        return getB(ScalePos_);
    }
    public static double getScaleC() throws InterpreterException{
        return getC(ScalePos_);
    }

	public boolean scalesAreEquals() throws InterpreterException{
		double sx = getScaleX();
		double sy = getScaleY();
		double sz = getScaleZ();
		return ((sx==sy)&&(sy==sz)&&(sx==sz));
	}
	
	public void setCoordinateOffset(double X, double Y, double Z, double A, double B, double C) throws InterpreterException{
		this.setX(G92OffsetPos_, X);
		this.setY(G92OffsetPos_, Y);
		this.setZ(G92OffsetPos_, Z);
		this.setA(G92OffsetPos_, A);
		this.setB(G92OffsetPos_, B);
		this.setC(G92OffsetPos_, C);
        this.setBase(G92OffsetPos_, 1.0);
    }

    public CNCPoint getCoordinateOffset() throws InterpreterException{
        CNCPoint result = new CNCPoint();
        if(this.getBase(G92OffsetPos_)>0.0){
            result.setX(this.getX(G92OffsetPos_));
            result.setY(this.getY(G92OffsetPos_));
            result.setZ(this.getZ(G92OffsetPos_));
            result.setA(this.getA(G92OffsetPos_));
            result.setB(this.getB(G92OffsetPos_));
            result.setC(this.getC(G92OffsetPos_));
        }
        return result;
    }

    public void setCurrentWorkOffsetNum(int P) throws InterpreterException{
		this.va.set(VariablesSet.currentWorkOffsetsNumPos_, P);
	}
	
	private int getCurrentWorkOffsetNum() throws InterpreterException{
		return (int)this.va.get(VariablesSet.currentWorkOffsetsNumPos_);
	}
	
	public double getOffsetX(int i) throws InterpreterException{
		if(i>0)	return this.getX(currentWorkOffsetsNumPos_ + (i - 1)*shift_);
		else return 0.0;
	}
	
	public double getWorkOffsetX() throws InterpreterException{
		int i = getCurrentWorkOffsetNum();
		return getOffsetX(i);
	}
	
	public double getOffsetY(int i) throws InterpreterException{
		if(i>0)	return this.getY(currentWorkOffsetsNumPos_ + (i - 1)*shift_);
		else return 0.0;
	}
	
	public double getWorkOffsetY() throws InterpreterException{
		int i = getCurrentWorkOffsetNum();
		return getOffsetY(i);
	}
	
	public double getOffsetZ(int i) throws InterpreterException{
		if(i>0)	return this.getZ(currentWorkOffsetsNumPos_ + (i - 1)*shift_);
		else return 0.0;
	}
	
	public double getWorkOffsetZ() throws InterpreterException{
		int i = getCurrentWorkOffsetNum();
		return getOffsetZ(i);
	}
	
	public double getOffsetA(int i) throws InterpreterException{
		if(i>0)	return this.getA(currentWorkOffsetsNumPos_ + (i - 1)*shift_);
		else return 0.0;
	}
	
	public double getWorkOffsetA() throws InterpreterException{
		int i = getCurrentWorkOffsetNum();
		return getOffsetA(i);
	}
	
	public double getOffsetB(int i) throws InterpreterException{
		if(i>0)	return this.getB(currentWorkOffsetsNumPos_ + (i - 1)*shift_);
		else return 0.0;
	}
	
	public double getWorkOffsetB() throws InterpreterException{
		int i = getCurrentWorkOffsetNum();
		return getOffsetB(i);
	}
	
	public double getOffsetC(int i) throws InterpreterException{
		if(i>0)	return this.getC(currentWorkOffsetsNumPos_ + (i - 1)*shift_);
		else return 0.0;
	}
	
	public double getWorkOffsetC() throws InterpreterException{
		int i = getCurrentWorkOffsetNum();
		return getOffsetC(i);
	}
	
	public double getRadius(int toolNum){
		// TODO real code needed
		return 0.0;
	}
	
	public boolean IsConstant(int num) throws InterpreterException{
		if(num <= 0) throw new InterpreterException("Reference to non initialized variable");
		else
			if(num == 1) return true;
			else return false;
	}
	
	public CNCPoint getHomePointG28() throws InterpreterException {
		CNCPoint homeCNCPoint =  new CNCPoint(this.getX(G28HomePos_), this.getY(G28HomePos_));
		return homeCNCPoint;
	}
	
	public CNCPoint getHomePointG30() throws InterpreterException {
		CNCPoint homeCNCPoint =  new CNCPoint(this.getX(G30HomePos_), this.getY(G30HomePos_));
		return homeCNCPoint;
	}
	
}
