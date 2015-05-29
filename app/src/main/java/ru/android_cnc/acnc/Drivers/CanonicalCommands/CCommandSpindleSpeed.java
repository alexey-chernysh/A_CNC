/*
 * @author Alexey Chernysh
 */

package ru.android_cnc.acnc.Drivers.CanonicalCommands;

import android.graphics.Canvas;

public class CCommandSpindleSpeed extends CanonCommand {

    double speed_;

    public CCommandSpindleSpeed(double s) {
        super(CanonCommand.type.WAIT_STATE_CHANGE);
        speed_ = s;
    }

    @Override
    public void execute() {
    }

    @Override
    public void draw(Canvas canvas) {
    }
}
