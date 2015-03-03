package ru.android_cnc.acnc.Drivers.CanonicalCommands;

import android.graphics.Canvas;

/*
 * @author Alexey Chernysh
 */

public class CCommandMessage extends CanonCommand {
    private final String msg_;
    public CCommandMessage(String m) {
        super(type.MESSAGE);
        msg_ = m;
    }

    @Override
    public void execute() {

    }

    @Override
    public void draw(Canvas canvas) {
//        Toast.makeText(context.getViewContext(), msg_, Toast.LENGTH_LONG).show();
    }
}
