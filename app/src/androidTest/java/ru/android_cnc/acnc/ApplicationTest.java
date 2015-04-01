package ru.android_cnc.acnc;

import android.app.Application;
import android.test.ApplicationTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import ru.android_cnc.acnc.Drivers.CanonicalCommands.MotionMode;
import ru.android_cnc.acnc.Drivers.CanonicalCommands.VelocityPlan;
import ru.android_cnc.acnc.Geometry.CNCPoint;
import ru.android_cnc.acnc.Interpreter.State.CutterRadiusCompensation;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */

public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
        TestCNCPoint();
    }

    @SmallTest
    public void TestCNCPoint(){
        VelocityPlan vp = new VelocityPlan(2000.0);
        MotionMode m = MotionMode.FREE;
        CutterRadiusCompensation crc = new CutterRadiusCompensation(CutterRadiusCompensation.mode.OFF,0.0);
        CNCPoint point1 = new CNCPoint(0.0,0.0);
        CNCPoint point2 = new CNCPoint(0.0,1.0);
        CNCPoint point3 = new CNCPoint(1.0,1.0);
        CCommandStraightLine line1 = new CCommandStraightLine(point1,point2,vp,m,crc);
        CCommandStraightLine line2 = new CCommandStraightLine(point2,point3,vp,m,crc);
        CNCPoint crossingPoint = CNCPoint.getCrossLineNLine(line1,line2);
        assertEquals(crossingPoint,point2);
    }
}