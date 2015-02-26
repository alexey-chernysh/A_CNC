package ru.android_cnc.acnc.Drivers.CanonicalCommands;

import android.content.Context;
import android.graphics.Canvas;
import android.widget.Toast;

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
    public void draw(Context context, Canvas canvas) {
        Toast.makeText(context, msg_, Toast.LENGTH_LONG).show();
    }
}
