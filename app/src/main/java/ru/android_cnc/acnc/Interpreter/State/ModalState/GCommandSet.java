/*
 * @author Alexey Chernysh
 */

package ru.android_cnc.acnc.Interpreter.State.ModalState;

import ru.android_cnc.acnc.Drivers.CanonicalCommands.ArcDirection;
import ru.android_cnc.acnc.Drivers.CanonicalCommands.CCommandArcLine;
import ru.android_cnc.acnc.Drivers.CanonicalCommands.CCommandDwell;
import ru.android_cnc.acnc.Drivers.CanonicalCommands.CCommandStraightLine;
import ru.android_cnc.acnc.Drivers.CanonicalCommands.MotionMode;
import ru.android_cnc.acnc.Drivers.CanonicalCommands.VelocityPlan;
import ru.android_cnc.acnc.Interpreter.Expression.ParamExpressionList;
import ru.android_cnc.acnc.Interpreter.Expression.Tokens.TokenParameter;
import ru.android_cnc.acnc.Interpreter.Expression.Variables.VariablesSet;
import ru.android_cnc.acnc.Interpreter.InterpreterException;
import ru.android_cnc.acnc.Geometry.CNCPoint;
import ru.android_cnc.acnc.Interpreter.ProgramLoader;
import ru.android_cnc.acnc.Interpreter.State.CutterRadiusCompensation;
import ru.android_cnc.acnc.Interpreter.State.InterpreterState;

public enum GCommandSet {
	G0(0.0, GCommandModalGroupSet.G_GROUP1_MOTION){ // Rapid positioning
		@Override
		public void evalute(ParamExpressionList words) throws InterpreterException {
			InterpreterState.modalState.set(modalGroup, this);
			CNCPoint startCNCPoint = InterpreterState.getLastPosition();
			CNCPoint endCNCPoint = InterpreterState.modalState.getTargetPoint(startCNCPoint, words);
			VelocityPlan vp = new VelocityPlan(InterpreterState.feedRate.getRapidFeedRate());
			CCommandStraightLine newG0 = new CCommandStraightLine(startCNCPoint,
                                        endCNCPoint,
										vp, 
										MotionMode.FREE,
										InterpreterState.zeroOffsetMode);
			ProgramLoader.command_sequence.add(newG0);
			InterpreterState.setLastPosition(endCNCPoint);
		}
	}, 
	G1(1.0, GCommandModalGroupSet.G_GROUP1_MOTION){ // Linear interpolation
		@Override
		public void evalute(ParamExpressionList words) throws InterpreterException {
			InterpreterState.modalState.set(modalGroup, this);
			CNCPoint startCNCPoint = InterpreterState.getLastPosition();
			CNCPoint endCNCPoint = InterpreterState.modalState.getTargetPoint(startCNCPoint, words);
			VelocityPlan vp = new VelocityPlan(InterpreterState.feedRate.getWorkFeedRate());
			CCommandStraightLine newG1 = new CCommandStraightLine(startCNCPoint,
                                        endCNCPoint,
										vp, 
										MotionMode.WORK, 
										InterpreterState.offsetMode);
			ProgramLoader.command_sequence.add(newG1);
			InterpreterState.setLastPosition(endCNCPoint);
		}
	}, 
	G2(2.0, GCommandModalGroupSet.G_GROUP1_MOTION){ // Clockwise circular/helical interpolation
		@Override
		public void evalute(ParamExpressionList words) throws InterpreterException {
			InterpreterState.modalState.set(modalGroup, this);
			CNCPoint startCNCPoint = InterpreterState.getLastPosition();
			CNCPoint endCNCPoint = InterpreterState.modalState.getTargetPoint(startCNCPoint, words);
			double R = InterpreterState.modalState.getR(words);
            CNCPoint centerCNCPoint;
            if(R == 0){ // I,J style arc coordinates
                centerCNCPoint = InterpreterState.modalState.getCenterPoint(startCNCPoint, words);
            } else { // R style clockwise arc coordinates
                boolean less_or_equal_to_pi = R>0;
                R = Math.abs(R);
                double dx = Math.abs(startCNCPoint.getX() - endCNCPoint.getX());
                double dy = Math.abs(startCNCPoint.getY() - endCNCPoint.getY());
                if((dx == 0)&&(dy == 00)) throw new InterpreterException("At least one coordinate fo R-style arc needed");
                else {
                    double catet1 = Math.sqrt(dx*dx+dy*dy)/2.0;
                    double catet2 = Math.sqrt(R*R - catet1*catet1);
                    double alfa = Math.atan2(catet2,catet1);
                    double beta = Math.atan2(dy,dx);
                    if(less_or_equal_to_pi) alfa -= beta;
                    else alfa += beta;
                    centerCNCPoint = new CNCPoint(startCNCPoint.getX() + R*Math.cos(alfa),
                                                  startCNCPoint.getY() + R*Math.sin(alfa));
                }
            };
            VelocityPlan vp = new VelocityPlan(InterpreterState.feedRate.getWorkFeedRate());
            CCommandArcLine newG2 = new CCommandArcLine(startCNCPoint,
                                                        endCNCPoint,
                                                        centerCNCPoint,
                                                        ArcDirection.CLOCKWISE,
                                                        vp,
                                                        InterpreterState.offsetMode);
            ProgramLoader.command_sequence.add(newG2);
			InterpreterState.setLastPosition(endCNCPoint);
		}
	}, 
	G3(3.0, GCommandModalGroupSet.G_GROUP1_MOTION){ // Counterclockwise circular/Helical interpolation
		@Override
		public void evalute(ParamExpressionList words) throws InterpreterException {
			checkThatScalesAreEquals();
			InterpreterState.modalState.set(modalGroup, this);
			CNCPoint startCNCPoint = InterpreterState.getLastPosition();
			CNCPoint endCNCPoint = InterpreterState.modalState.getTargetPoint(startCNCPoint, words);
            double R = InterpreterState.modalState.getR(words);
            CNCPoint centerCNCPoint;
            if(R == 0){ // I,J style arc coordinates
                centerCNCPoint = InterpreterState.modalState.getCenterPoint(startCNCPoint, words);
            } else { // R style clockwise arc coordinates
                boolean less_or_equal_to_pi = R>0;
                R = Math.abs(R);
                double dx = Math.abs(startCNCPoint.getX() - endCNCPoint.getX());
                double dy = Math.abs(startCNCPoint.getY() - endCNCPoint.getY());
                if((dx == 0)&&(dy == 00)) throw new InterpreterException("At least one coordinate fo R-style arc needed");
                else {
                    double catet1 = Math.sqrt(dx*dx+dy*dy)/2.0;
                    double catet2 = Math.sqrt(R*R - catet1*catet1);
                    double alfa = Math.atan2(catet2,catet1);
                    double beta = Math.atan2(dy,dx);
                    if(less_or_equal_to_pi) alfa += beta;
                    else alfa -= beta;
                    centerCNCPoint = new CNCPoint(startCNCPoint.getX() + R*Math.cos(alfa),
                            startCNCPoint.getY() + R*Math.sin(alfa));
                }
            };
            VelocityPlan vp = new VelocityPlan(InterpreterState.feedRate.getWorkFeedRate());
            CCommandArcLine newG2 = new CCommandArcLine(startCNCPoint,
                    endCNCPoint,
                    centerCNCPoint,
                    ArcDirection.COUNTERCLOCKWISE,
                    vp,
                    InterpreterState.offsetMode);
            ProgramLoader.command_sequence.add(newG2);
            InterpreterState.setLastPosition(endCNCPoint);
		}
	}, 
	G4(4.0, GCommandModalGroupSet.G_GROUP0_G4_DWELL){ // Dwell
		@Override
		public void evalute(ParamExpressionList words) throws InterpreterException {
			checkThatScalesAreEquals();
			InterpreterState.modalState.set(modalGroup, this);
			double p = words.get(TokenParameter.P);
			if(p >= 0.0){
				CCommandDwell newG4 = new CCommandDwell(p);
				ProgramLoader.command_sequence.add(newG4);
			} else throw new InterpreterException("Illegal dwell time");
		}
	}, 
	G10(10.0, GCommandModalGroupSet.G_GROUP0_NON_MODAL){ // Coordinate system origin setting
		@Override
		public void evalute(ParamExpressionList words) throws InterpreterException {
			int l = words.getInt(TokenParameter.L);
			int p = words.getInt(TokenParameter.P);
			if((p >= 0)&&(p <= VariablesSet.maxToolNumber)){
				switch(l){
				case 1: //
					if(words.has(TokenParameter.X))
						InterpreterState.vars_.setToolFixtureOffset(p, TokenParameter.X, words.get(TokenParameter.X));
					if(words.has(TokenParameter.Z))
						InterpreterState.vars_.setToolFixtureOffset(p, TokenParameter.Z, words.get(TokenParameter.Z));
					if(words.has(TokenParameter.A))
						InterpreterState.vars_.setToolFixtureOffset(p, TokenParameter.A, words.get(TokenParameter.A));
					break;
				case 2: 
					// set tool fixture offset for tool with number p
					if(words.has(TokenParameter.X))
						InterpreterState.vars_.setToolFixtureOffset(p, TokenParameter.X, words.get(TokenParameter.X));
					if(words.has(TokenParameter.Y))
						InterpreterState.vars_.setToolFixtureOffset(p, TokenParameter.Y, words.get(TokenParameter.Y));
					if(words.has(TokenParameter.Z))
						InterpreterState.vars_.setToolFixtureOffset(p, TokenParameter.Z, words.get(TokenParameter.Z));
					if(words.has(TokenParameter.A))
						InterpreterState.vars_.setToolFixtureOffset(p, TokenParameter.A, words.get(TokenParameter.A));
					if(words.has(TokenParameter.B))
						InterpreterState.vars_.setToolFixtureOffset(p, TokenParameter.B, words.get(TokenParameter.B));
					if(words.has(TokenParameter.C))
						InterpreterState.vars_.setToolFixtureOffset(p, TokenParameter.C, words.get(TokenParameter.C));
					break; 
				default:
					throw new InterpreterException("Unsupported G10 mode");
				}
			} else throw new InterpreterException("Illegal (" + p + ") tool number in G10");
		}
	}, 
	G12(12.0, GCommandModalGroupSet.G_GROUP1_MOTION){ // Clockwise circular pocket
		@Override
		public void evalute(ParamExpressionList words) throws InterpreterException {
			checkThatPlaneIsXY();
			double radius = words.get(TokenParameter.I); // circle radius
			if(radius > 0.0){
				CNCPoint centerCNCPoint = InterpreterState.getLastPosition();
				CNCPoint circleStartCNCPoint = centerCNCPoint.clone();
				VelocityPlan vp = new VelocityPlan(InterpreterState.feedRate.getWorkFeedRate());
				circleStartCNCPoint.shift(radius, 0.0);
				CCommandStraightLine G1_in = new CCommandStraightLine(centerCNCPoint,
                                            circleStartCNCPoint,
											vp, 
											MotionMode.WORK, 
											InterpreterState.offsetMode);
				ProgramLoader.command_sequence.add(G1_in);
				CCommandArcLine newG2 = new CCommandArcLine(circleStartCNCPoint,
                                            circleStartCNCPoint,
                                            centerCNCPoint,
											ArcDirection.CLOCKWISE,
											vp, 
											InterpreterState.offsetMode);
				ProgramLoader.command_sequence.add(newG2);
				CCommandStraightLine G1_out = new CCommandStraightLine(circleStartCNCPoint,
                                             centerCNCPoint,
											 vp, 
											 MotionMode.WORK, 
											 InterpreterState.offsetMode);
				ProgramLoader.command_sequence.add(G1_out);
			} else new InterpreterException("For G12 pocket positive I parameter needed");
		}
	}, 
	G13(13.0, GCommandModalGroupSet.G_GROUP1_MOTION){ // Counterclockwise circular pocket
		@Override
		public void evalute(ParamExpressionList words) throws InterpreterException {
			checkThatPlaneIsXY();
			double radius = words.get(TokenParameter.I); // circle radius
			if(radius > 0.0){
				CNCPoint centerCNCPoint = InterpreterState.getLastPosition();
				CNCPoint circleStartCNCPoint = centerCNCPoint.clone();
				VelocityPlan vp = new VelocityPlan(InterpreterState.feedRate.getWorkFeedRate());
				circleStartCNCPoint.shift(radius, 0.0);
				CCommandStraightLine G1_in = new CCommandStraightLine(centerCNCPoint,
                                            circleStartCNCPoint,
											vp, 
											MotionMode.WORK, 
											InterpreterState.offsetMode);
				ProgramLoader.command_sequence.add(G1_in);
				CCommandArcLine newG2 = new CCommandArcLine(circleStartCNCPoint,
                                            circleStartCNCPoint,
                                            centerCNCPoint,
											ArcDirection.COUNTERCLOCKWISE,
											vp, 
											InterpreterState.offsetMode);
				ProgramLoader.command_sequence.add(newG2);
				CCommandStraightLine G1_out = new CCommandStraightLine(circleStartCNCPoint,
                                             centerCNCPoint,
											 vp, 
											 MotionMode.WORK, 
											 InterpreterState.offsetMode);
				ProgramLoader.command_sequence.add(G1_out);
			} else new InterpreterException("For G12 pocket positive I parameter needed");
		}
	}, 
	G15(15.0, GCommandModalGroupSet.G_GROUP17_POLAR_COORDINATES){ // Polar coordinate moves in G0 and G1
		@Override
		public void evalute(ParamExpressionList words) throws InterpreterException {
			InterpreterState.modalState.set(modalGroup, this);
		}
	}, 
	G16(16.0, GCommandModalGroupSet.G_GROUP17_POLAR_COORDINATES){ // Cancel polar coordinate moves
		@Override
		public void evalute(ParamExpressionList words) throws InterpreterException {
			InterpreterState.modalState.set(modalGroup, this);
		}
	}, 
	G17(17.0, GCommandModalGroupSet.G_GROUP2_PLANE){ // XY Plane select
		@Override
		public void evalute(ParamExpressionList words) throws InterpreterException {
			InterpreterState.modalState.set(modalGroup, this);
		}
	}, 
	G18(18.0, GCommandModalGroupSet.G_GROUP2_PLANE){ // XZ plane select
		@Override
		public void evalute(ParamExpressionList words) throws InterpreterException {
			checkThatCutterRadiusCompebsationIsOff();
			InterpreterState.modalState.set(modalGroup, this);
		}
	}, 
	G19(19.0, GCommandModalGroupSet.G_GROUP2_PLANE){ // YZ plane select
		@Override
		public void evalute(ParamExpressionList words) throws InterpreterException{
			checkThatCutterRadiusCompebsationIsOff();
			InterpreterState.modalState.set(modalGroup, this);
		};
	}, 
	G20(20.0, GCommandModalGroupSet.G_GROUP6_UNITS){  // Inch unit
		@Override
		public void evalute(ParamExpressionList words) throws InterpreterException {
			InterpreterState.modalState.set(modalGroup, this);
		}
	},
	G21(21.0, GCommandModalGroupSet.G_GROUP6_UNITS){ // Millimeter unit
		@Override
		public void evalute(ParamExpressionList words) throws InterpreterException {
			InterpreterState.modalState.set(modalGroup, this);
		}
	}, 
	G28(28.0, GCommandModalGroupSet.G_GROUP0_NON_MODAL){ // Return home
		@Override
		public void evalute(ParamExpressionList words) throws InterpreterException {
			CNCPoint currentCNCPoint = InterpreterState.getLastPosition();
			CNCPoint intermediateCNCPoint = words.getPoint();
			VelocityPlan vp = new VelocityPlan(InterpreterState.feedRate.getRapidFeedRate());
			if(intermediateCNCPoint != null){
				CCommandStraightLine motion1 = new CCommandStraightLine(currentCNCPoint,
                                              intermediateCNCPoint,
						  					  vp, 
						  					  MotionMode.FREE, 
						  					  null);
				ProgramLoader.command_sequence.add(motion1);
				InterpreterState.setLastPosition(intermediateCNCPoint);
				currentCNCPoint = intermediateCNCPoint;
			};
			CNCPoint homeCNCPoint = InterpreterState.vars_.getHomePointG28();
			CCommandStraightLine motion2 = new CCommandStraightLine(currentCNCPoint,
                                          homeCNCPoint,
										  vp, 
										  MotionMode.FREE, 
										  null);
			ProgramLoader.command_sequence.add(motion2);
			InterpreterState.setLastPosition(homeCNCPoint);
		}
	}, 
	G28_1(28.1, GCommandModalGroupSet.G_GROUP0_NON_MODAL){ // Reference axes
        @Override
        public void evalute(ParamExpressionList words) throws InterpreterException{
            CNCPoint position = words.getPoint();
            InterpreterState.vars_.setG28HomePos(position);
        };
    },
	G30(30.0, GCommandModalGroupSet.G_GROUP0_NON_MODAL){ // Return home
		@Override
		public void evalute(ParamExpressionList words) throws InterpreterException {
			CNCPoint currentCNCPoint = InterpreterState.getLastPosition();
			CNCPoint intermediateCNCPoint = words.getPoint();
			VelocityPlan vp = new VelocityPlan(InterpreterState.feedRate.getRapidFeedRate());
			if(intermediateCNCPoint != null){
				CCommandStraightLine motion1 = new CCommandStraightLine(currentCNCPoint,
                                              intermediateCNCPoint,
						  					  vp, 
						  					  MotionMode.FREE, 
						  					  null);
				ProgramLoader.command_sequence.add(motion1);
				InterpreterState.setLastPosition(intermediateCNCPoint);
				currentCNCPoint = intermediateCNCPoint;
			};
			CNCPoint homeCNCPoint = InterpreterState.vars_.getHomePointG30();
			CCommandStraightLine motion2 = new CCommandStraightLine(currentCNCPoint,
                                          homeCNCPoint,
										  vp, 
										  MotionMode.FREE, 
										  null);
			ProgramLoader.command_sequence.add(motion2);
			InterpreterState.setLastPosition(homeCNCPoint);
		}
	},
    G30_1(30.1, GCommandModalGroupSet.G_GROUP0_NON_MODAL){ // Reference axes
        @Override
        public void evalute(ParamExpressionList words) throws InterpreterException{
            CNCPoint position = words.getPoint();
            InterpreterState.vars_.setG30HomePos(position);
        };
    },
	G31(31.0, GCommandModalGroupSet.G_GROUP1_MOTION), // Straight probe
	G40(40.0, GCommandModalGroupSet.G_GROUP7_CUTTER_RADIUS_COMPENSATION){ // Cancel cutter radius compensation
		@Override
		public void evalute(ParamExpressionList words) throws InterpreterException{
			InterpreterState.modalState.set(modalGroup, this);
			InterpreterState.offsetMode.setMode(CutterRadiusCompensation.mode.OFF);
		};
	}, 
	G41(41.0, GCommandModalGroupSet.G_GROUP7_CUTTER_RADIUS_COMPENSATION){ // Start cutter radius compensation left
		@Override
		public void evalute(ParamExpressionList words) throws InterpreterException{
			checkThatPlaneIsXY();
			InterpreterState.modalState.set(modalGroup, this);
			InterpreterState.offsetMode.setMode(CutterRadiusCompensation.mode.LEFT);
			double offset = -1.0;
			int d = (int)words.get(TokenParameter.D);
			if(d > 0){
				offset = InterpreterState.toolSet.getToolRadius(d);
			} else {
				offset = words.get(TokenParameter.P);
			};
			if(offset > 0.0) InterpreterState.offsetMode.setRadius(offset);
		};
	}, // Start cutter radius compensation left
	G42(42.0, GCommandModalGroupSet.G_GROUP7_CUTTER_RADIUS_COMPENSATION){ // Start cutter radius compensation right
		@Override
		public void evalute(ParamExpressionList words) throws InterpreterException{
			checkThatPlaneIsXY();
			InterpreterState.modalState.set(modalGroup, this);
			InterpreterState.offsetMode.setMode(CutterRadiusCompensation.mode.RIGHT);
			double offset = -1.0;
			int d = (int)words.get(TokenParameter.D);
			if(d > 0){
				offset = InterpreterState.toolSet.getToolRadius(d);
			} else {
				offset = words.get(TokenParameter.P);
			};
			if(offset > 0.0)InterpreterState.offsetMode.setRadius(offset);
		};
	}, 
	G43(43.0, GCommandModalGroupSet.G_GROUP8_TOOL_LENGHT_OFFSET){ // Apply tool length offset (plus)
        @Override
        public void evalute(ParamExpressionList words) throws InterpreterException {
            if(words.has(TokenParameter.H)){
                int h = (int)words.get(TokenParameter.H);
                if((h>0)&&(h<=255)){
                    InterpreterState.toolHeightCompensation.setHeight(h);
                    InterpreterState.toolHeightCompensation.setOn();
                } else new InterpreterException("H parameter should be in range 1...255 for G43");
            } else new InterpreterException("H parameter needed for G43");
        }
    },
	G49(49.0, GCommandModalGroupSet.G_GROUP8_TOOL_LENGHT_OFFSET){ // Cancel tool length offset
        @Override
        public void evalute(ParamExpressionList words) throws InterpreterException {
            InterpreterState.toolHeightCompensation.setOff();
        }
    },
	G50(50.0, GCommandModalGroupSet.G_GROUP18_SCALING){ // Reset all scale factors to 1.0
		@Override
		public void evalute(ParamExpressionList words) throws InterpreterException {
			InterpreterState.modalState.set(modalGroup, this);
			InterpreterState.vars_.setScale(1.0, 1.0, 1.0, 1.0, 1.0, 1.0);
		}
	}, 
	G51(51.0, GCommandModalGroupSet.G_GROUP18_SCALING){ // Set axis data input scale factors
		@Override
		public void evalute(ParamExpressionList words) throws InterpreterException {
			InterpreterState.modalState.set(modalGroup, this);
			double X = 1.0;
			double Y = 1.0;
			double Z = 1.0;
			double A = 1.0;
			double B = 1.0;
			double C = 1.0;
			if(words.has(TokenParameter.X)) X = words.get(TokenParameter.X);
			if(words.has(TokenParameter.Y)) X = words.get(TokenParameter.Y);
			if(words.has(TokenParameter.Z)) X = words.get(TokenParameter.Z);
			if(words.has(TokenParameter.A)) X = words.get(TokenParameter.A);
			if(words.has(TokenParameter.B)) X = words.get(TokenParameter.B);
			if(words.has(TokenParameter.C)) X = words.get(TokenParameter.C);
			InterpreterState.vars_.setScale(X, Y, Z, A, B, C);
		}
	}, 
	G52(52.0, GCommandModalGroupSet.G_GROUP0_NON_MODAL){// Temporary coordinate system offsets
        @Override
        public void evalute(ParamExpressionList words) throws InterpreterException {
            G92.evalute(words);
        }
    },
	G53(53.0, GCommandModalGroupSet.G_GROUP0_G53_MODIFIER), // Move in absolute machine coordinate system
	G54(54.0, GCommandModalGroupSet.G_GROUP12_OFFSET_SELECTION){ // Use fixture offset 1
		@Override
		public void evalute(ParamExpressionList words) throws InterpreterException {
			checkThatCutterRadiusCompebsationIsOff();
			InterpreterState.vars_.setCurrentWorkOffsetNum(1);
		}
	}, 
	G55(55.0, GCommandModalGroupSet.G_GROUP12_OFFSET_SELECTION){ // Use fixture offset 2
		@Override
		public void evalute(ParamExpressionList words) throws InterpreterException {
			checkThatCutterRadiusCompebsationIsOff();
			InterpreterState.vars_.setCurrentWorkOffsetNum(2);
		}
	}, 
	G56(56.0, GCommandModalGroupSet.G_GROUP12_OFFSET_SELECTION){ // Use fixture offset 3
		@Override
		public void evalute(ParamExpressionList words) throws InterpreterException {
			checkThatCutterRadiusCompebsationIsOff();
			InterpreterState.vars_.setCurrentWorkOffsetNum(3);
		}
	}, 
	G57(57.0, GCommandModalGroupSet.G_GROUP12_OFFSET_SELECTION){ // Use fixture offset 4
		@Override
		public void evalute(ParamExpressionList words) throws InterpreterException {
			checkThatCutterRadiusCompebsationIsOff();
			InterpreterState.vars_.setCurrentWorkOffsetNum(4);
		}
	}, 
	G58(58.0, GCommandModalGroupSet.G_GROUP12_OFFSET_SELECTION){ // Use fixture offset 5
		@Override
		public void evalute(ParamExpressionList words) throws InterpreterException {
			checkThatCutterRadiusCompebsationIsOff();
			InterpreterState.vars_.setCurrentWorkOffsetNum(5);
		}
	}, 
	G59(59.0, GCommandModalGroupSet.G_GROUP12_OFFSET_SELECTION){ // Use fixture offset 6 / use general fixture number
		@Override
		public void evalute(ParamExpressionList words) throws InterpreterException {
			checkThatCutterRadiusCompebsationIsOff();
			if(words.has(TokenParameter.P)){
				int P = words.getInt(TokenParameter.P);
				InterpreterState.vars_.setCurrentWorkOffsetNum(P);
			} 
			else InterpreterState.vars_.setCurrentWorkOffsetNum(6);
		}
	}, 
	G61(61.0, GCommandModalGroupSet.G_GROUP13_PATH_CONTROL_MODE), // Exact stop mode
	G64(64.0, GCommandModalGroupSet.G_GROUP13_PATH_CONTROL_MODE), // Constant Velocity mode
	G68(68.0, GCommandModalGroupSet.G_GROUP16_COORDINATE_ROTATION){ // Rotate program coordinate system
        public void evalute(ParamExpressionList words) throws InterpreterException {
            double A = 0.0;
            double B = 0.0;
            double R = 0.0;
            if(words.has(TokenParameter.A)) A = words.get(TokenParameter.A);
            if(words.has(TokenParameter.B)) B = words.get(TokenParameter.B);
            if(words.has(TokenParameter.R)) R = words.get(TokenParameter.R);
            if(words.has(TokenParameter.I)) InterpreterState.coordinateRotation.add(A, B, R);
            else InterpreterState.coordinateRotation.replace(A, B, R);
        }
    },
	G69(69.0, GCommandModalGroupSet.G_GROUP16_COORDINATE_ROTATION){ // Cancel program coordinate system rotation
        public void evalute(ParamExpressionList words) throws InterpreterException {
            InterpreterState.coordinateRotation.cancel();
        }
    },
	G70(70.0, GCommandModalGroupSet.G_GROUP6_UNITS){ // Inch unit
		@Override
		public void evalute(ParamExpressionList words) throws InterpreterException {
			G20.evalute(words);
		}
	},  
	G71(71.0, GCommandModalGroupSet.G_GROUP6_UNITS){ // Millimetre unit
		@Override
		public void evalute(ParamExpressionList words) throws InterpreterException {
			G21.evalute(words);
		}
	}, 
	G73(73.0, GCommandModalGroupSet.G_GROUP9_CANNED_CYCLES), // Canned cycle - peck drilling
	G80(80.0, GCommandModalGroupSet.G_GROUP9_CANNED_CYCLES), // Cancel motion mode (including canned cycles)
	G81(81.0, GCommandModalGroupSet.G_GROUP9_CANNED_CYCLES), // Canned cycle - drilling
	G82(82.0, GCommandModalGroupSet.G_GROUP9_CANNED_CYCLES), // Canned cycle - drilling with dwell
	G83(83.0, GCommandModalGroupSet.G_GROUP9_CANNED_CYCLES), // Canned cycle - peck drilling
	G84(84.0, GCommandModalGroupSet.G_GROUP9_CANNED_CYCLES), // Canned cycle - right hand rigid tapping
	G85(85.0, GCommandModalGroupSet.G_GROUP9_CANNED_CYCLES), // Canned cycle - boring
	G86(86.0, GCommandModalGroupSet.G_GROUP9_CANNED_CYCLES), // Canned cycle - boring
	G87(87.0, GCommandModalGroupSet.G_GROUP9_CANNED_CYCLES), // Canned cycle - boring
	G88(88.0, GCommandModalGroupSet.G_GROUP9_CANNED_CYCLES), // Canned cycle - boring
	G89(89.0, GCommandModalGroupSet.G_GROUP9_CANNED_CYCLES), // Canned cycle - boring
	G90(90.0, GCommandModalGroupSet.G_GROUP3_DISTANCE_MODE){ // Absolute distance mode
		@Override
		public void evalute(ParamExpressionList words) throws InterpreterException {
			InterpreterState.modalState.set(modalGroup, this);
		}
	}, // Absolute distance mode
	G90_1(90.1, GCommandModalGroupSet.G_GROUP4_ARC_DISTANCE_MODE){ // Arc absolute distance mode
		@Override
		public void evalute(ParamExpressionList words) throws InterpreterException {
			InterpreterState.modalState.set(modalGroup, this);
		}
	}, 
	G91(91.0, GCommandModalGroupSet.G_GROUP3_DISTANCE_MODE){ // Incremental distance mode
		@Override
		public void evalute(ParamExpressionList words) throws InterpreterException {
			InterpreterState.modalState.set(modalGroup, this);
		}
	}, 
	G91_1(91.1, GCommandModalGroupSet.G_GROUP4_ARC_DISTANCE_MODE){ // Arc incremental distance mode
		@Override
		public void evalute(ParamExpressionList words) throws InterpreterException {
			InterpreterState.modalState.set(modalGroup, this);
		}
	}, 
	G92(92.0, GCommandModalGroupSet.G_GROUP0_NON_MODAL){// Offset coordinates and set parameters
		@Override
		public void evalute(ParamExpressionList words) throws InterpreterException{
            if(words.hasXYZ()||words.hasABC())
    			InterpreterState.setHomePoint(words.get(TokenParameter.X),
	    									  words.get(TokenParameter.Y),
                                              words.get(TokenParameter.Z),
                                              words.get(TokenParameter.A),
                                              words.get(TokenParameter.B),
                                              words.get(TokenParameter.C));
            else throw new InterpreterException("G92 without arguments");

        };
	}, 
	G92_1(92.1, GCommandModalGroupSet.G_GROUP0_NON_MODAL), // Cancel G92 etc.
	G92_2(92.2, GCommandModalGroupSet.G_GROUP0_NON_MODAL){ // G92 X0 Y0
		@Override
		public void evalute(ParamExpressionList words) throws InterpreterException{
			InterpreterState.setHomePoint(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
		};
	}, 
	G92_3(92.3, GCommandModalGroupSet.G_GROUP0_NON_MODAL), //
	G93(93.0, GCommandModalGroupSet.G_GROUP5_FEED_RATE_MODE){ // Inverse time feed mode
		@Override
		public void evalute(ParamExpressionList words) throws InterpreterException{
			InterpreterState.modalState.set(modalGroup, this);
		};
	}, 
	G94(94.0, GCommandModalGroupSet.G_GROUP5_FEED_RATE_MODE){ // Feed per minute mode
		@Override
		public void evalute(ParamExpressionList words) throws InterpreterException{
			InterpreterState.modalState.set(modalGroup, this);
		};
	}, 
	G95(95.0, GCommandModalGroupSet.G_GROUP5_FEED_RATE_MODE){ // Feed per revolution mode
		@Override
		public void evalute(ParamExpressionList words) throws InterpreterException{
			InterpreterState.modalState.set(modalGroup, this);
		};
	}, 
	G98(98.0, GCommandModalGroupSet.G_GROUP10_CANNED_CYCLES_RETURN_MODE){ // Initial level return after canned cycles
		@Override
		public void evalute(ParamExpressionList words) throws InterpreterException{
			InterpreterState.modalState.set(modalGroup, this);
		};
	}, 
	G99(99.0, GCommandModalGroupSet.G_GROUP10_CANNED_CYCLES_RETURN_MODE){ // R-point level return after canned cycles
		@Override
		public void evalute(ParamExpressionList words) throws InterpreterException{
			InterpreterState.modalState.set(modalGroup, this);
		};
	}, 
	GDUMMY(-1.0, GCommandModalGroupSet.G_GROUP0_NON_MODAL){
		@Override
		public void evalute(ParamExpressionList words) throws InterpreterException{
		};
	}; // dummy command for initial assignment
	
	public int number;
	public GCommandModalGroupSet modalGroup;
	
	public void evalute(ParamExpressionList words) throws InterpreterException{
		InterpreterState.modalState.set(modalGroup, this);
	};
	
	
	private GCommandSet(double n, GCommandModalGroupSet g){
		this.number = (int)(10*n);
		this.modalGroup = g;
	};
	
	private static void checkThatCutterRadiusCompebsationIsOff() throws InterpreterException{
		if(InterpreterState.modalState.getGModalState(GCommandModalGroupSet.G_GROUP7_CUTTER_RADIUS_COMPENSATION) != G40)
			throw new InterpreterException("Command available only while cutter raius compensation is off");
	}

	private static void checkThatPlaneIsXY() throws InterpreterException{
		if(InterpreterState.modalState.getGModalState(GCommandModalGroupSet.G_GROUP2_PLANE) != G17)
			throw new InterpreterException("Command available for XY plane only");
	}
	
	private static void checkThatScalesAreEquals() throws InterpreterException{
		if(!InterpreterState.vars_.scalesAreEquals())
			throw new InterpreterException("Command available with equal scales for X, Y and Z");
	}
	
}
