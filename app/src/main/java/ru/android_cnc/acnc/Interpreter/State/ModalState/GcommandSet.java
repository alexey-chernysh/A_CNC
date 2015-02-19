/*
 * @author Alexey Chernysh
 */

package ru.android_cnc.acnc.Interpreter.State.ModalState;

import ru.android_cnc.acnc.Drivers.CanonicalCommands.ArcDirection;
import ru.android_cnc.acnc.Drivers.CanonicalCommands.G00_G01;
import ru.android_cnc.acnc.Drivers.CanonicalCommands.G02_G03;
import ru.android_cnc.acnc.Drivers.CanonicalCommands.G04;
import ru.android_cnc.acnc.Drivers.CanonicalCommands.MotionMode;
import ru.android_cnc.acnc.Drivers.CanonicalCommands.VelocityPlan;
import ru.android_cnc.acnc.Interpreter.Expression.ParamExpresionList;
import ru.android_cnc.acnc.Interpreter.Expression.Tokens.TokenParameter;
import ru.android_cnc.acnc.Interpreter.Expression.Variables.VariablesSet;
import ru.android_cnc.acnc.Interpreter.InterpreterException;
import ru.android_cnc.acnc.Interpreter.Motion.CNCPoint;
import ru.android_cnc.acnc.Interpreter.ProgramLoader;
import ru.android_cnc.acnc.Interpreter.State.CutterRadiusCompensation;
import ru.android_cnc.acnc.Interpreter.State.InterpreterState;

public enum GcommandSet {
	G0(0.0, GcommandModalGroupSet.G_GROUP1_MOTION){ // Rapid positioning
		@Override
		public void evalute(ParamExpresionList words) throws InterpreterException {
			InterpreterState.modalState.set(modalGroup, this);
			CNCPoint startCNCPoint = InterpreterState.getLastPosition();
			CNCPoint endCNCPoint = InterpreterState.modalState.getTargetPoint(startCNCPoint, words);
			VelocityPlan vp = new VelocityPlan(InterpreterState.feedRate.getRapidFeedRate());
			G00_G01 newG0 = new G00_G01(startCNCPoint,
                    endCNCPoint,
										vp, 
										MotionMode.FREE,
										InterpreterState.zeroOffsetMode);
			ProgramLoader.command_sequence.add(newG0);
			InterpreterState.setLastPosition(endCNCPoint);
		}
	}, 
	G1(1.0, GcommandModalGroupSet.G_GROUP1_MOTION){ // Linear interpolation
		@Override
		public void evalute(ParamExpresionList words) throws InterpreterException {
			InterpreterState.modalState.set(modalGroup, this);
			CNCPoint startCNCPoint = InterpreterState.getLastPosition();
			CNCPoint endCNCPoint = InterpreterState.modalState.getTargetPoint(startCNCPoint, words);
			VelocityPlan vp = new VelocityPlan(InterpreterState.feedRate.getWorkFeedRate());
			G00_G01 newG1 = new G00_G01(startCNCPoint,
                    endCNCPoint,
										vp, 
										MotionMode.WORK, 
										InterpreterState.offsetMode);
			ProgramLoader.command_sequence.add(newG1);
			InterpreterState.setLastPosition(endCNCPoint);
		}
	}, 
	G2(2.0, GcommandModalGroupSet.G_GROUP1_MOTION){ // Clockwise circular/helical interpolation
		@Override
		public void evalute(ParamExpresionList words) throws InterpreterException {
			InterpreterState.modalState.set(modalGroup, this);
			CNCPoint startCNCPoint = InterpreterState.getLastPosition();
			CNCPoint endCNCPoint = InterpreterState.modalState.getTargetPoint(startCNCPoint, words);
			// TODO R format also needed
			CNCPoint centerCNCPoint = InterpreterState.modalState.getCenterPoint(startCNCPoint, words);
			VelocityPlan vp = new VelocityPlan(InterpreterState.feedRate.getWorkFeedRate());
			G02_G03 newG2 = new G02_G03(startCNCPoint,
                    endCNCPoint,
                    centerCNCPoint,
										ArcDirection.CLOCKWISE,
										vp, 
										InterpreterState.offsetMode);
			ProgramLoader.command_sequence.add(newG2);
			InterpreterState.setLastPosition(endCNCPoint);
		}
	}, 
	G3(3.0, GcommandModalGroupSet.G_GROUP1_MOTION){ // Counterclockwise circular/Helical interpolation
		@Override
		public void evalute(ParamExpresionList words) throws InterpreterException {
			checkThatScalesAreEquals();
			InterpreterState.modalState.set(modalGroup, this);
			CNCPoint startCNCPoint = InterpreterState.getLastPosition();
			CNCPoint endCNCPoint = InterpreterState.modalState.getTargetPoint(startCNCPoint, words);
			// TODO R format also needed
			CNCPoint centerCNCPoint = InterpreterState.modalState.getCenterPoint(startCNCPoint, words);
			VelocityPlan vp = new VelocityPlan(InterpreterState.feedRate.getWorkFeedRate());
			G02_G03 newG3 = new G02_G03(startCNCPoint,
                    endCNCPoint,
                    centerCNCPoint,
										ArcDirection.COUNTERCLOCKWISE,
										vp, 
										InterpreterState.offsetMode);
			ProgramLoader.command_sequence.add(newG3);
			InterpreterState.setLastPosition(endCNCPoint);
		}
	}, 
	G4(4.0, GcommandModalGroupSet.G_GROUP0_G4_DWELL){ // Dwell
		@Override
		public void evalute(ParamExpresionList words) throws InterpreterException {
			checkThatScalesAreEquals();
			InterpreterState.modalState.set(modalGroup, this);
			double p = words.get(TokenParameter.P);
			if(p >= 0.0){
				G04 newG4 = new G04(p);
				ProgramLoader.command_sequence.add(newG4);
			} else throw new InterpreterException("Illegal dwell time");
		}
	}, 
	G10(10.0, GcommandModalGroupSet.G_GROUP0_NON_MODAL){ // Coordinate system origin setting
		@Override
		public void evalute(ParamExpresionList words) throws InterpreterException {
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
	G12(12.0, GcommandModalGroupSet.G_GROUP1_MOTION){ // Clockwise circular pocket
		@Override
		public void evalute(ParamExpresionList words) throws InterpreterException { 
			checkThatPlaneIsXY();
			double radius = words.get(TokenParameter.I); // circle radius
			if(radius > 0.0){
				CNCPoint centerCNCPoint = InterpreterState.getLastPosition();
				CNCPoint circleStartCNCPoint = centerCNCPoint.clone();
				VelocityPlan vp = new VelocityPlan(InterpreterState.feedRate.getWorkFeedRate());
				circleStartCNCPoint.shift(radius, 0.0);
				G00_G01 G1_in = new G00_G01(centerCNCPoint,
                        circleStartCNCPoint,
											vp, 
											MotionMode.WORK, 
											InterpreterState.offsetMode);
				ProgramLoader.command_sequence.add(G1_in);
				G02_G03 newG2 = new G02_G03(circleStartCNCPoint,
                        circleStartCNCPoint,
                        centerCNCPoint,
											ArcDirection.CLOCKWISE,
											vp, 
											InterpreterState.offsetMode);
				ProgramLoader.command_sequence.add(newG2);
				G00_G01 G1_out = new G00_G01(circleStartCNCPoint,
                        centerCNCPoint,
											 vp, 
											 MotionMode.WORK, 
											 InterpreterState.offsetMode);
				ProgramLoader.command_sequence.add(G1_out);
			} else new InterpreterException("For G12 pocket positive I parameter needed");
		}
	}, 
	G13(13.0, GcommandModalGroupSet.G_GROUP1_MOTION){ // Counterclockwise circular pocket
		@Override
		public void evalute(ParamExpresionList words) throws InterpreterException { 
			checkThatPlaneIsXY();
			double radius = words.get(TokenParameter.I); // circle radius
			if(radius > 0.0){
				CNCPoint centerCNCPoint = InterpreterState.getLastPosition();
				CNCPoint circleStartCNCPoint = centerCNCPoint.clone();
				VelocityPlan vp = new VelocityPlan(InterpreterState.feedRate.getWorkFeedRate());
				circleStartCNCPoint.shift(radius, 0.0);
				G00_G01 G1_in = new G00_G01(centerCNCPoint,
                        circleStartCNCPoint,
											vp, 
											MotionMode.WORK, 
											InterpreterState.offsetMode);
				ProgramLoader.command_sequence.add(G1_in);
				G02_G03 newG2 = new G02_G03(circleStartCNCPoint,
                        circleStartCNCPoint,
                        centerCNCPoint,
											ArcDirection.COUNTERCLOCKWISE,
											vp, 
											InterpreterState.offsetMode);
				ProgramLoader.command_sequence.add(newG2);
				G00_G01 G1_out = new G00_G01(circleStartCNCPoint,
                        centerCNCPoint,
											 vp, 
											 MotionMode.WORK, 
											 InterpreterState.offsetMode);
				ProgramLoader.command_sequence.add(G1_out);
			} else new InterpreterException("For G12 pocket positive I parameter needed");
		}
	}, 
	G15(15.0, GcommandModalGroupSet.G_GROUP17_POLAR_COORDINATES){ // Polar coordinate moves in G0 and G1
		@Override
		public void evalute(ParamExpresionList words) throws InterpreterException { 
			InterpreterState.modalState.set(modalGroup, this);
		}
	}, 
	G16(16.0, GcommandModalGroupSet.G_GROUP17_POLAR_COORDINATES){ // Cancel polar coordinate moves
		@Override
		public void evalute(ParamExpresionList words) throws InterpreterException { 
			InterpreterState.modalState.set(modalGroup, this);
		}
	}, 
	G17(17.0, GcommandModalGroupSet.G_GROUP2_PLANE){ // XY Plane select
		@Override
		public void evalute(ParamExpresionList words) throws InterpreterException { 
			InterpreterState.modalState.set(modalGroup, this);
		}
	}, 
	G18(18.0, GcommandModalGroupSet.G_GROUP2_PLANE){ // XZ plane select
		@Override
		public void evalute(ParamExpresionList words) throws InterpreterException { 
			checkThatCutterRadiusCompebsationIsOff();
			InterpreterState.modalState.set(modalGroup, this);
		}
	}, 
	G19(19.0, GcommandModalGroupSet.G_GROUP2_PLANE){ // YZ plane select
		@Override
		public void evalute(ParamExpresionList words) throws InterpreterException{ 
			checkThatCutterRadiusCompebsationIsOff();
			InterpreterState.modalState.set(modalGroup, this);
		};
	}, 
	G20(20.0, GcommandModalGroupSet.G_GROUP6_UNITS){  // Inch unit
		@Override
		public void evalute(ParamExpresionList words) throws InterpreterException { 
			InterpreterState.modalState.set(modalGroup, this);
		}
	},
	G21(21.0, GcommandModalGroupSet.G_GROUP6_UNITS){ // Millimeter unit
		@Override
		public void evalute(ParamExpresionList words) throws InterpreterException { 
			InterpreterState.modalState.set(modalGroup, this);
		}
	}, 
	G28(28.0, GcommandModalGroupSet.G_GROUP0_NON_MODAL){ // Return home
		@Override
		public void evalute(ParamExpresionList words) throws InterpreterException { 
			CNCPoint currentCNCPoint = InterpreterState.getLastPosition();
			CNCPoint intermediateCNCPoint = words.getPoint();
			VelocityPlan vp = new VelocityPlan(InterpreterState.feedRate.getRapidFeedRate());
			if(intermediateCNCPoint != null){
				G00_G01 motion1 = new G00_G01(currentCNCPoint,
                        intermediateCNCPoint,
						  					  vp, 
						  					  MotionMode.FREE, 
						  					  null);
				ProgramLoader.command_sequence.add(motion1);
				InterpreterState.setLastPosition(intermediateCNCPoint);
				currentCNCPoint = intermediateCNCPoint;
			};
			CNCPoint homeCNCPoint = InterpreterState.vars_.getHomePointG28();
			G00_G01 motion2 = new G00_G01(currentCNCPoint,
                    homeCNCPoint,
										  vp, 
										  MotionMode.FREE, 
										  null);
			ProgramLoader.command_sequence.add(motion2);
			InterpreterState.setLastPosition(homeCNCPoint);
		}
	}, 
	G28_1(28.1, GcommandModalGroupSet.G_GROUP0_NON_MODAL), // Reference axes
	G30(30.0, GcommandModalGroupSet.G_GROUP0_NON_MODAL){ // Return home
		@Override
		public void evalute(ParamExpresionList words) throws InterpreterException { 
			CNCPoint currentCNCPoint = InterpreterState.getLastPosition();
			CNCPoint intermediateCNCPoint = words.getPoint();
			VelocityPlan vp = new VelocityPlan(InterpreterState.feedRate.getRapidFeedRate());
			if(intermediateCNCPoint != null){
				G00_G01 motion1 = new G00_G01(currentCNCPoint,
                        intermediateCNCPoint,
						  					  vp, 
						  					  MotionMode.FREE, 
						  					  null);
				ProgramLoader.command_sequence.add(motion1);
				InterpreterState.setLastPosition(intermediateCNCPoint);
				currentCNCPoint = intermediateCNCPoint;
			};
			CNCPoint homeCNCPoint = InterpreterState.vars_.getHomePointG30();
			G00_G01 motion2 = new G00_G01(currentCNCPoint,
                    homeCNCPoint,
										  vp, 
										  MotionMode.FREE, 
										  null);
			ProgramLoader.command_sequence.add(motion2);
			InterpreterState.setLastPosition(homeCNCPoint);
		}
	}, 
	G31(31.0, GcommandModalGroupSet.G_GROUP1_MOTION), // Straight probe
	G40(40.0, GcommandModalGroupSet.G_GROUP7_CUTTER_RADIUS_COMPENSATION){ // Cancel cutter radius compensation
		@Override
		public void evalute(ParamExpresionList words) throws InterpreterException{ 
			InterpreterState.modalState.set(modalGroup, this);
			InterpreterState.offsetMode.setMode(CutterRadiusCompensation.mode.OFF);
		};
	}, 
	G41(41.0, GcommandModalGroupSet.G_GROUP7_CUTTER_RADIUS_COMPENSATION){ // Start cutter radius compensation left
		@Override
		public void evalute(ParamExpresionList words) throws InterpreterException{ 
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
	G42(42.0, GcommandModalGroupSet.G_GROUP7_CUTTER_RADIUS_COMPENSATION){ // Start cutter radius compensation right
		@Override
		public void evalute(ParamExpresionList words) throws InterpreterException{ 
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
	G43(43.0, GcommandModalGroupSet.G_GROUP8_TOOL_LENGHT_OFFSET), // Apply tool length offset (plus)
	G49(49.0, GcommandModalGroupSet.G_GROUP8_TOOL_LENGHT_OFFSET), // Cancel tool length offset
	G50(50.0, GcommandModalGroupSet.G_GROUP18_SCALING){ // Reset all scale factors to 1.0
		@Override
		public void evalute(ParamExpresionList words) throws InterpreterException { 
			InterpreterState.modalState.set(modalGroup, this);
			InterpreterState.vars_.setScale(1.0, 1.0, 1.0, 1.0, 1.0, 1.0);
		}
	}, 
	G51(51.0, GcommandModalGroupSet.G_GROUP18_SCALING){ // Set axis data input scale factors
		@Override
		public void evalute(ParamExpresionList words) throws InterpreterException { 
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
	G52(52.0, GcommandModalGroupSet.G_GROUP0_NON_MODAL), // Temporary coordinate system offsets
	G53(53.0, GcommandModalGroupSet.G_GROUP0_G53_MODIFIER), // Move in absolute machine coordinate system
	G54(54.0, GcommandModalGroupSet.G_GROUP12_OFFSET_SELECTION){ // Use fixture offset 1
		@Override
		public void evalute(ParamExpresionList words) throws InterpreterException { 
			checkThatCutterRadiusCompebsationIsOff();
			InterpreterState.vars_.setCurrentWorkOffsetNum(1);
		}
	}, 
	G55(55.0, GcommandModalGroupSet.G_GROUP12_OFFSET_SELECTION){ // Use fixture offset 2
		@Override
		public void evalute(ParamExpresionList words) throws InterpreterException { 
			checkThatCutterRadiusCompebsationIsOff();
			InterpreterState.vars_.setCurrentWorkOffsetNum(2);
		}
	}, 
	G56(56.0, GcommandModalGroupSet.G_GROUP12_OFFSET_SELECTION){ // Use fixture offset 3
		@Override
		public void evalute(ParamExpresionList words) throws InterpreterException { 
			checkThatCutterRadiusCompebsationIsOff();
			InterpreterState.vars_.setCurrentWorkOffsetNum(3);
		}
	}, 
	G57(57.0, GcommandModalGroupSet.G_GROUP12_OFFSET_SELECTION){ // Use fixture offset 4
		@Override
		public void evalute(ParamExpresionList words) throws InterpreterException { 
			checkThatCutterRadiusCompebsationIsOff();
			InterpreterState.vars_.setCurrentWorkOffsetNum(4);
		}
	}, 
	G58(58.0, GcommandModalGroupSet.G_GROUP12_OFFSET_SELECTION){ // Use fixture offset 5
		@Override
		public void evalute(ParamExpresionList words) throws InterpreterException { 
			checkThatCutterRadiusCompebsationIsOff();
			InterpreterState.vars_.setCurrentWorkOffsetNum(5);
		}
	}, 
	G59(59.0, GcommandModalGroupSet.G_GROUP12_OFFSET_SELECTION){ // Use fixture offset 6 / use general fixture number
		@Override
		public void evalute(ParamExpresionList words) throws InterpreterException { 
			checkThatCutterRadiusCompebsationIsOff();
			if(words.has(TokenParameter.P)){
				int P = words.getInt(TokenParameter.P);
				InterpreterState.vars_.setCurrentWorkOffsetNum(P);
			} 
			else InterpreterState.vars_.setCurrentWorkOffsetNum(1);
		}
	}, 
	G61(61.0, GcommandModalGroupSet.G_GROUP13_PATH_CONTROL_MODE), // Exact stop mode
	G64(64.0, GcommandModalGroupSet.G_GROUP13_PATH_CONTROL_MODE), // Constant Velocity mode
	G68(68.0, GcommandModalGroupSet.G_GROUP16_COORDINATE_ROTATION), // Rotate program coordinate system
	G69(69.0, GcommandModalGroupSet.G_GROUP16_COORDINATE_ROTATION), // Cancel program coordinate system rotation
	G70(70.0, GcommandModalGroupSet.G_GROUP6_UNITS){ // Inch unit
		@Override
		public void evalute(ParamExpresionList words) throws InterpreterException { 
			G20.evalute(words);
		}
	},  
	G71(71.0, GcommandModalGroupSet.G_GROUP6_UNITS){ // Millimetre unit
		@Override
		public void evalute(ParamExpresionList words) throws InterpreterException { 
			G21.evalute(words);
		}
	}, 
	G73(73.0, GcommandModalGroupSet.G_GROUP9_CANNED_CYCLES), // Canned cycle - peck drilling
	G80(80.0, GcommandModalGroupSet.G_GROUP9_CANNED_CYCLES), // Cancel motion mode (including canned cycles)
	G81(81.0, GcommandModalGroupSet.G_GROUP9_CANNED_CYCLES), // Canned cycle - drilling
	G82(82.0, GcommandModalGroupSet.G_GROUP9_CANNED_CYCLES), // Canned cycle - drilling with dwell
	G83(83.0, GcommandModalGroupSet.G_GROUP9_CANNED_CYCLES), // Canned cycle - peck drilling
	G84(84.0, GcommandModalGroupSet.G_GROUP9_CANNED_CYCLES), // Canned cycle - right hand rigid tapping
	G85(85.0, GcommandModalGroupSet.G_GROUP9_CANNED_CYCLES), // Canned cycle - boring
	G86(86.0, GcommandModalGroupSet.G_GROUP9_CANNED_CYCLES), // Canned cycle - boring
	G87(87.0, GcommandModalGroupSet.G_GROUP9_CANNED_CYCLES), // Canned cycle - boring
	G88(88.0, GcommandModalGroupSet.G_GROUP9_CANNED_CYCLES), // Canned cycle - boring
	G89(89.0, GcommandModalGroupSet.G_GROUP9_CANNED_CYCLES), // Canned cycle - boring
	G90(90.0, GcommandModalGroupSet.G_GROUP3_DISTANCE_MODE){ // Absolute distance mode
		@Override
		public void evalute(ParamExpresionList words) throws InterpreterException { 
			InterpreterState.modalState.set(modalGroup, this);
		}
	}, // Absolute distance mode
	G90_1(90.1, GcommandModalGroupSet.G_GROUP4_ARC_DISTANCE_MODE){ // Arc absolute distance mode
		@Override
		public void evalute(ParamExpresionList words) throws InterpreterException { 
			InterpreterState.modalState.set(modalGroup, this);
		}
	}, 
	G91(91.0, GcommandModalGroupSet.G_GROUP3_DISTANCE_MODE){ // Incremental distance mode
		@Override
		public void evalute(ParamExpresionList words) throws InterpreterException { 
			InterpreterState.modalState.set(modalGroup, this);
		}
	}, 
	G91_1(91.1, GcommandModalGroupSet.G_GROUP4_ARC_DISTANCE_MODE){ // Arc incremental distance mode
		@Override
		public void evalute(ParamExpresionList words) throws InterpreterException { 
			InterpreterState.modalState.set(modalGroup, this);
		}
	}, 
	G92(92.0, GcommandModalGroupSet.G_GROUP0_NON_MODAL){// Offset coordinates and set parameters
		@Override
		public void evalute(ParamExpresionList words) throws InterpreterException{
			InterpreterState.setHomePoint(words.get(TokenParameter.X), 
										  words.get(TokenParameter.Y));
		};
	}, 
	G92_1(92.1, GcommandModalGroupSet.G_GROUP0_NON_MODAL), // Cancel G92 etc.
	G92_2(92.2, GcommandModalGroupSet.G_GROUP0_NON_MODAL){ // G92 X0 Y0
		@Override
		public void evalute(ParamExpresionList words) throws InterpreterException{
			InterpreterState.setHomePoint(0.0, 0.0);
		};
	}, 
	G92_3(92.3, GcommandModalGroupSet.G_GROUP0_NON_MODAL), // 
	G93(93.0, GcommandModalGroupSet.G_GROUP5_FEED_RATE_MODE){ // Inverse time feed mode
		@Override
		public void evalute(ParamExpresionList words) throws InterpreterException{
			InterpreterState.modalState.set(modalGroup, this);
		};
	}, 
	G94(94.0, GcommandModalGroupSet.G_GROUP5_FEED_RATE_MODE){ // Feed per minute mode
		@Override
		public void evalute(ParamExpresionList words) throws InterpreterException{
			InterpreterState.modalState.set(modalGroup, this);
		};
	}, 
	G95(95.0, GcommandModalGroupSet.G_GROUP5_FEED_RATE_MODE){ // Feed per revolution mode
		@Override
		public void evalute(ParamExpresionList words) throws InterpreterException{
			InterpreterState.modalState.set(modalGroup, this);
		};
	}, 
	G98(98.0, GcommandModalGroupSet.G_GROUP10_CANNED_CYCLES_RETURN_MODE){ // Initial level return after canned cycles
		@Override
		public void evalute(ParamExpresionList words) throws InterpreterException{
			InterpreterState.modalState.set(modalGroup, this);
		};
	}, 
	G99(99.0, GcommandModalGroupSet.G_GROUP10_CANNED_CYCLES_RETURN_MODE){ // R-point level return after canned cycles
		@Override
		public void evalute(ParamExpresionList words) throws InterpreterException{
			InterpreterState.modalState.set(modalGroup, this);
		};
	}, 
	GDUMMY(-1.0, GcommandModalGroupSet.G_GROUP0_NON_MODAL){
		@Override
		public void evalute(ParamExpresionList words) throws InterpreterException{
		};
	}; // dummy command for initial assignment
	
	public int number;
	public GcommandModalGroupSet modalGroup;
	
	public void evalute(ParamExpresionList words) throws InterpreterException{
		InterpreterState.modalState.set(modalGroup, this);
	};
	
	
	private GcommandSet(double n, GcommandModalGroupSet g){
		this.number = (int)(10*n);
		this.modalGroup = g;
	};
	
	private static void checkThatCutterRadiusCompebsationIsOff() throws InterpreterException{
		if(InterpreterState.modalState.getGModalState(GcommandModalGroupSet.G_GROUP7_CUTTER_RADIUS_COMPENSATION) != G40)
			throw new InterpreterException("Command available only while cutter raius compensation is off");
	}

	private static void checkThatPlaneIsXY() throws InterpreterException{
		if(InterpreterState.modalState.getGModalState(GcommandModalGroupSet.G_GROUP2_PLANE) != G17)
			throw new InterpreterException("Command available for XY plane only");
	}
	
	private static void checkThatScalesAreEquals() throws InterpreterException{
		if(!InterpreterState.vars_.scalesAreEquals())
			throw new InterpreterException("Command available with equal scales for X, Y and Z");
	}
	
}
