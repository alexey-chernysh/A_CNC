package ru.android_cnc.acnc.Drivers.CanonicalCommands;

import android.content.Context;
import android.graphics.Canvas;

/**
 * Created by Sales on 26.02.2015.
 */
public class CCommandMessage extends CanonCommand {
    private final String msg_;
    public CCommandMessage(String m) {
        super(type.MESSAGE);
        msg_ = m;
    }

    @Override
    public void draw(Context context, Canvas canvas) {

    }
}
