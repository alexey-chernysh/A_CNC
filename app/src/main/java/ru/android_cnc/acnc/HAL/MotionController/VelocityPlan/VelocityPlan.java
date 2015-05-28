/*
 * @author Alexey Chernysh
 */

package ru.android_cnc.acnc.HAL.MotionController.VelocityPlan;

import ru.android_cnc.acnc.Drivers.CanonicalCommands.CanonCommandSequence;
import ru.android_cnc.acnc.HAL.MotionController.CCommandArcLine;
import ru.android_cnc.acnc.HAL.MotionController.CCommandStraightLine;
import ru.android_cnc.acnc.HAL.MotionController.MotionControllerCommand;
import ru.android_cnc.acnc.HAL.MotionController.MotionControllerService;
import ru.android_cnc.acnc.HAL.MotionController.MotionMode;
import ru.android_cnc.acnc.HAL.MotionController.StepPlan.StepPlan;
import ru.android_cnc.acnc.Interpreter.Exceptions.EvolutionException;

public class VelocityPlan extends StepPlan {

    public static final double velocityTol = 0.01;
    public static final double angleTol = 0.01;
    public static final double perforationVel = 1.0;
    public static final double perforationVelDur = 5.0;
    public static final double perfLength = perforationVel*perforationVelDur/60.0; // in mm


    public VelocityPlan(MotionControllerCommand command) {
        super(command);

        double l = command.length();
        double feedRate = command.getFeedRate();

        final double step_x = MotionControllerService.getX_mm_in_step();
        final double step_y = MotionControllerService.getY_mm_in_step();
        final double timeScale = MotionControllerService.getTikInMM();

    }

    public static double conform(MotionControllerCommand command1, MotionControllerCommand command2) {
        if(command1 == null){
            // command2 is first motion in sequence
            command2.setVelocityPlan(new VelocityPlan(command2));
            return 1.0;
        } else {
            if(command1 == null){
                // command1 is last motion in sequence
            } else {
            }
        }
        return 1.0;
    }

    public static void buildVelocityProfile(CanonCommandSequence commands_) throws EvolutionException {

        int size = commands_.size();

        for(int i=0; i<size; i++){
            Object currentCommand = commands_.get(i);
            boolean itsCuttingLine = currentCommand instanceof CCommandStraightLine;
            boolean itsCuttingArc = currentCommand instanceof CCommandArcLine;
            if(itsCuttingLine || itsCuttingArc){
                // working with cutting lines and arc only
                Object prevCuttingCommand = PreviousCutting(commands_, i);
                Object nextCuttingCommand = NextCutting(commands_, i);
//				double neededVelocity = ((CCommandStraightLine)currentCommand).getVelocityPlan().getStartVel();
                if(prevCuttingCommand == null){
                    // first cutting after perforation - inserting slow perforation phase
                    if(itsCuttingLine){
                        CCommandStraightLine currentLine = (CCommandStraightLine)currentCommand;
                        double currentLength = currentLine.length();
                        if((perfLength < currentLength)&&(perfLength > 0.0)){
                            CCommandStraightLine newLine1 = currentLine.newSubLine(0, perfLength);
//							newLine1.setVelocityProfile(perforationVel,startVel);
                            CCommandStraightLine newLine2 = currentLine.newSubLine(perfLength, currentLength);
//							newLine1.setVelocityProfile(startVel, neededVelocity);
                            commands_.remove(i);
                            commands_.add(i, newLine2);
                            commands_.add(i, newLine1);
                            i++;
                        } else {
//							currentLine.setVelocityProfile(startVel, neededVelocity);
                        }
                    } else {
                        if(itsCuttingArc){
                            CCommandArcLine currentArc = (CCommandArcLine)currentCommand;
                            double currentLength = currentArc.length();
                            if((perfLength < currentLength)&&(perfLength > 0.0)){
                                CCommandArcLine newArc1 = currentArc.newSubArc(0, perfLength);
//								newArc1.setVelocityProfile(perforationVel,startVel);
                                CCommandArcLine newArc2 = currentArc.newSubArc(perfLength, currentLength);
//								newArc1.setVelocityProfile(startVel, neededVelocity);
                                commands_.remove(i);
                                commands_.add(i, newArc2);
                                commands_.add(i, newArc1);
                                i++;
                            } else {
//								currentArc.setVelocityProfile(startVel, neededVelocity);
                            }
                        }
                    }
                } else {
                    // its no first cutting line - adjustment needed
                    CCommandStraightLine beforeLine = (CCommandStraightLine)prevCuttingCommand;
                    double angleBeforeStart = beforeLine.getEndTangentAngle();
//					double velBeforeStart = beforeLine.getVelocityPlan().getEndVel();
                    CCommandStraightLine currentLine = (CCommandStraightLine)currentCommand;
                    double angleStart = currentLine.getStartTangentAngle();
                    if(Math.abs(angleStart - angleBeforeStart) < angleTol){
                        // fine case of smooth line angle adjustment.
                        // adjust velocity now
//						if(velBeforeStart == neededVelocity){
                        // velocity is equal
//							currentLine.setVelocityProfile(neededVelocity, neededVelocity);
//						} else {
//							currentLine.setVelocityProfile(velBeforeStart, neededVelocity);
//						}
                    } else {
//						currentLine.setVelocityProfile(startVel, neededVelocity);
//						((CCommandStraightLine)prevCuttingCommand).getVelocityPlan().setEndVel(startVel);
                    }
                }
                if(nextCuttingCommand == null){
//					((CCommandStraightLine)this.commands_.get(i)).getVelocityPlan().setEndVel(startVel);
                }

            }
        }
    }

    private static Object NextCutting(CanonCommandSequence commands_, int i) {
        if((i+1) >= commands_.size()) return null;
        Object next = commands_.get(i + 1);
        if(next instanceof CCommandStraightLine)
            if(((CCommandStraightLine)next).getMode() == MotionMode.WORK) return next;
        if(next instanceof CCommandArcLine) return next;
        return null;
    }

    private static Object PreviousCutting(CanonCommandSequence commands_, int i){  // command index
        if(i <= 0) return null;
        Object before = commands_.get(i-1);
        if(before instanceof CCommandStraightLine)
            if(((CCommandStraightLine)before).getMode() == MotionMode.WORK) return before;
        if(before instanceof CCommandArcLine) return before;
        return null;
    }

}
