package ru.android_cnc.acnc.Drivers.CanonicalCommands;

import android.graphics.Canvas;
import android.widget.Toast;

import ru.android_cnc.acnc.GraphView.CNCViewContext;

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
    public void draw(CNCViewContext context, Canvas canvas) {
//        Toast.makeText(context.getViewContext(), msg_, Toast.LENGTH_LONG).show();
    }
}
