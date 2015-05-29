/*
 * @author Alexey Chernysh
 */

package ru.android_cnc.acnc.Drivers.CanonicalCommands;

import android.graphics.Canvas;
import android.widget.Toast;

import ru.android_cnc.acnc.GraphView.CNCControlViewActivity;

public class CCommandMessage extends CanonCommand {
    private final String msg_;
    public CCommandMessage(String m) {
        super(type.MESSAGE);
        msg_ = m;
    }

    @Override
    public void execute() {
        Toast.makeText(CNCControlViewActivity.getAppContext(), msg_, Toast.LENGTH_LONG).show();
    }

    @Override
    public void draw(Canvas canvas) {
    }
}
