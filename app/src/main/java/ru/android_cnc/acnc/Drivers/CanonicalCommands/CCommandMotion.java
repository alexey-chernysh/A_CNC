package ru.android_cnc.acnc.Drivers.CanonicalCommands;

import android.graphics.Canvas;

import ru.android_cnc.acnc.Draw.DrawableObjectLimits;
import ru.android_cnc.acnc.Interpreter.InterpreterException;

/**
 * Created by Sales on 05.03.2015.
 */
public class CCommandMotion extends CanonCommand {

    private MotionType motionType;
    public DrawableObjectLimits limits = null;

    public CCommandMotion(MotionType mt) {
        super(CanonCommand.type.MOTION);
        motionType = mt;
    }

    @Override
    public void execute() {

    }

    @Override
    public void draw(Canvas canvas) {

    }

    public void checkLimits() throws InterpreterException {

    }

    public MotionType getMotionType() {
        return motionType;
    }

    public void setMotionType(MotionType motionType) {
        this.motionType = motionType;
    }

    public enum MotionType{
        STRAIGHT,
        ARC
    }
}
