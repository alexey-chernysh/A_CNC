package ru.android_cnc.acnc.test;

import junit.framework.TestCase;

import org.junit.Test;

import ru.android_cnc.acnc.HAL.MotionController.ArcDirection;
import ru.android_cnc.acnc.HAL.MotionController.CCommandArcLine;
import ru.android_cnc.acnc.HAL.MotionController.CCommandStraightLine;
import ru.android_cnc.acnc.HAL.MotionController.MotionMode;
import ru.android_cnc.acnc.HAL.MotionController.VelocityPlan.VelocityPlan;
import ru.android_cnc.acnc.Geometry.CNCPoint;
import ru.android_cnc.acnc.Interpreter.State.CutterRadiusCompensation;

public class CNCPointTest extends TestCase {

    @Test
    public void testGetCrossLineNLine() throws Exception {
        VelocityPlan vp = new VelocityPlan(2000.0);
        MotionMode m = MotionMode.FREE;
        CutterRadiusCompensation crc = new CutterRadiusCompensation(CutterRadiusCompensation.mode.OFF,0.0);
        CNCPoint point1 = new CNCPoint(0.0,0.0);
        CNCPoint point2 = new CNCPoint(0.0,1.0);
        CNCPoint point3 = new CNCPoint(1.0,1.0);
        CCommandStraightLine line1;
        CCommandStraightLine line2;
        line1 = new CCommandStraightLine(point1, point2, vp, m, crc);
        line2 = new CCommandStraightLine(point2, point3, vp, m, crc);
        CNCPoint crossingPoint = CNCPoint.getCrossingPoint(line1, line2);
        assertEquals(crossingPoint,point2);
    }

    @Test
    public void testGetCrossArcNLine() throws Exception {
        VelocityPlan vp = new VelocityPlan(2000.0);
        MotionMode m = MotionMode.FREE;
        CutterRadiusCompensation crc = new CutterRadiusCompensation(CutterRadiusCompensation.mode.OFF,0.0);
        CNCPoint point1 = new CNCPoint(0.0,0.0);
        CNCPoint point2 = new CNCPoint(0.0,1.0);
        CNCPoint point3 = new CNCPoint(1.0,2.0);
        CNCPoint point4 = new CNCPoint(1.0,1.0);
        CCommandStraightLine line1;
        CCommandStraightLine line2;
        CCommandArcLine arc1;
        CCommandArcLine arc2;
        line1 = new CCommandStraightLine(point1, point2, vp, m, crc);
        line2 = new CCommandStraightLine(point2, point1, vp, m, crc);
        arc1 = new CCommandArcLine(point2, point3, point4, ArcDirection.CLOCKWISE, vp, crc);
        arc2 = new CCommandArcLine(point3, point2, point4, ArcDirection.COUNTERCLOCKWISE, vp, crc);
        CNCPoint crossingPoint = CNCPoint.getCrossingPoint(line1, arc1);
        assertEquals(crossingPoint,point2);
        crossingPoint = CNCPoint.getCrossingPoint(arc2, line2);
        assertEquals(crossingPoint,point2);
    }

    @Test
    public void testGetCrossArcNArc() throws Exception {
        VelocityPlan vp = new VelocityPlan(2000.0);
        CutterRadiusCompensation crc = new CutterRadiusCompensation(CutterRadiusCompensation.mode.OFF,0.0);
        CNCPoint point0 = new CNCPoint(-1.0,0.0);
        CNCPoint point1 = new CNCPoint(-1.0,1.0);
        CNCPoint point2 = new CNCPoint(0.0,1.0);
        CNCPoint point3 = new CNCPoint(1.0,2.0);
        CNCPoint point4 = new CNCPoint(1.0,1.0);
        CCommandArcLine arc1;
        CCommandArcLine arc2;
        arc1 = new CCommandArcLine(point0, point2, point1, ArcDirection.COUNTERCLOCKWISE, vp, crc);
        arc2 = new CCommandArcLine(point2, point3, point4, ArcDirection.CLOCKWISE, vp, crc);
        CNCPoint crossingPoint = CNCPoint.getCrossingPoint(arc1, arc2);
        assertEquals(crossingPoint,point2);
    }
}