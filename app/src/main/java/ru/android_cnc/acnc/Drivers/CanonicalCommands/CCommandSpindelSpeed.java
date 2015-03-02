package ru.android_cnc.acnc.Drivers.CanonicalCommands;

import android.graphics.Canvas;

import ru.android_cnc.acnc.GraphView.CNCViewContext;

/**
 * Created by Sales on 27.02.2015.
 */
public class CCommandSpindelSpeed extends CanonCommand {

    double speed_;

    public CCommandSpindelSpeed(double s) {
        super(CanonCommand.type.WAIT_STATE_CHANGE);
        speed_ = s;
    }

    @Override
    public void execute() {
        // TODO impementation needed
    }

    @Override
    public void draw(CNCViewContext context, Canvas canvas) {
        // TODO change spindel speed on dosplay needed
    }
}
