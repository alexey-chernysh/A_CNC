package ru.android_cnc.acnc.GraphView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

/**
 * Created by Sales on 25.02.2015.
 */
public class GcodeGraphView extends View {
    Paint paint = new Paint();

    public GcodeGraphView(Context context) {
        super(context);
    }
    @Override
    public void onDraw(Canvas canvas) {
        paint.setColor(Color.GREEN);
        Rect rect = new Rect(20, 56, 200, 112);
        canvas.drawRect(rect, paint );
    }

}
