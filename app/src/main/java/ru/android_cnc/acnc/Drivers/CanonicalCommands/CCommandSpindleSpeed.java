package ru.android_cnc.acnc.Drivers.CanonicalCommands;

import android.graphics.Canvas;


/**
 * Created by Sales on 27.02.2015.
 */
public class CCommandSpindleSpeed extends CanonCommand {

    double speed_;

    public CCommandSpindleSpeed(double s) {
        super(CanonCommand.type.WAIT_STATE_CHANGE);
        speed_ = s;
    }

    @Override
    public void execute() {
        // TODO implementation needed
    }

    @Override
    public void draw(Canvas canvas) {
        // TODO change spindle speed on display needed
    }
}