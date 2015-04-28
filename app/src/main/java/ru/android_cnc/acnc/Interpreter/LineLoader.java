/*
 * @author Alexey Chernysh
 */

package ru.android_cnc.acnc.Interpreter;

import android.util.Log;

import ru.android_cnc.acnc.Drivers.CanonicalCommands.CCommandMessage;
import ru.android_cnc.acnc.Drivers.CanonicalCommands.CCommandSpindelSpeed;
import ru.android_cnc.acnc.Interpreter.Exceptions.EvolutionException;
import ru.android_cnc.acnc.Interpreter.Exceptions.InterpreterException;
import ru.android_cnc.acnc.Interpreter.Expression.CommandLineLoader;
import ru.android_cnc.acnc.Interpreter.Expression.CommandPair;
import ru.android_cnc.acnc.Interpreter.Expression.ExpressionGeneral;
import ru.android_cnc.acnc.Interpreter.Expression.Variables.ExpressionVarAssignment;
import ru.android_cnc.acnc.Interpreter.State.InterpreterState;
import ru.android_cnc.acnc.Interpreter.State.ModalState.GCommandSet;
import ru.android_cnc.acnc.Interpreter.State.ModalState.MCommandSet;

public class LineLoader extends CommandLineLoader {

    private static final String LOG_TAG = "G code line loader:";
	
	private String message_ = null;
	private ExpressionGeneral feedRate_     = null;
	private ExpressionGeneral tool_         = null;
	private ExpressionGeneral spindelSpeed_ = null;
	private MCommandSet M1_M2_M3    = MCommandSet.MDUMMY;
	private MCommandSet M3_M4_M5    = MCommandSet.MDUMMY;
	private MCommandSet M6          = MCommandSet.MDUMMY;
	private MCommandSet M7_M8_M9    = MCommandSet.MDUMMY;
	private MCommandSet M48_M49     = MCommandSet.MDUMMY;
	private MCommandSet M47_M98_M99 = MCommandSet.MDUMMY;
	private GCommandSet G4          = GCommandSet.GDUMMY;
	private GCommandSet G_NON_MODAL = GCommandSet.GDUMMY;
	private GCommandSet G15_G16     = GCommandSet.GDUMMY;
	private GCommandSet G17_G18_G19 = GCommandSet.GDUMMY;
	private GCommandSet G20_G21     = GCommandSet.GDUMMY;
	private GCommandSet G40_G41_G42 = GCommandSet.GDUMMY;
	private GCommandSet G43_G49     = GCommandSet.GDUMMY;
	private GCommandSet G50_G51     = GCommandSet.GDUMMY;
	private GCommandSet G53         = GCommandSet.GDUMMY;
	private GCommandSet G54___G59   = GCommandSet.GDUMMY;
	private GCommandSet G61_G64     = GCommandSet.GDUMMY;
	private GCommandSet G68_G69     = GCommandSet.GDUMMY;
	private GCommandSet G80_G89     = GCommandSet.GDUMMY;
	private GCommandSet G90_G91     = GCommandSet.GDUMMY;
	private GCommandSet G90_1_G91_1 = GCommandSet.GDUMMY;
	private GCommandSet G93_G94_G95 = GCommandSet.GDUMMY;
	private GCommandSet G98_G99     = GCommandSet.GDUMMY;
	private GCommandSet G_MOTION    = GCommandSet.GDUMMY;
	
	private int moduleNum_ = -1;

	public LineLoader(String s) throws InterpreterException {
        super(s);
        load();
    }

    private void load() throws InterpreterException {
		int size = this.commandSet_.size();
		int i;
		for(i=0; i<size; i++){
			CommandPair currentCommand = this.commandSet_.get(i);
			ExpressionGeneral commandValueExpression = currentCommand.getValueExpression();
			switch(currentCommand.getType()){
			case F:
				this.feedRate_ = commandValueExpression;
				break;
			case G:
                GCommandSet g_command = null;
                try { // TODO change this smelling code - G
                    g_command = this.GcommandByNumber(currentCommand.getCurrentValue());
                } catch (EvolutionException e) {
                    e.printStackTrace();
                }
                switch(g_command){
				case G0:
					if(this.G_MOTION == GCommandSet.GDUMMY) this.G_MOTION = GCommandSet.G0;
					else throw new InterpreterException("Twice motion command in same string", currentCommand.getPosInString());
                    break;
				case G1:
					if(this.G_MOTION == GCommandSet.GDUMMY) this.G_MOTION = GCommandSet.G1;
					else throw new InterpreterException("Twice motion command in same string", currentCommand.getPosInString());
					break;
				case G2:
					if(this.G_MOTION == GCommandSet.GDUMMY) this.G_MOTION = GCommandSet.G2;
					else throw new InterpreterException("Twice motion command in same string", currentCommand.getPosInString());
					break;
				case G3:
					if(this.G_MOTION == GCommandSet.GDUMMY) this.G_MOTION = GCommandSet.G3;
					else throw new InterpreterException("Twice motion command in same string", currentCommand.getPosInString());
					break;
				case G4:
					if(this.G4 == GCommandSet.GDUMMY) this.G4 = GCommandSet.G4;
					else throw new InterpreterException("Twice dwell command in same string", currentCommand.getPosInString());
					break;
				case G10:
					if(this.G_NON_MODAL == GCommandSet.GDUMMY) this.G_NON_MODAL = GCommandSet.G10;
					else throw new InterpreterException("Twice homing command in same string", currentCommand.getPosInString());
					break;
				case G12:
					if(this.G_MOTION == GCommandSet.GDUMMY) this.G_MOTION = GCommandSet.G12;
					else throw new InterpreterException("Twice motion command in same string", currentCommand.getPosInString());
					break;
				case G13:
					if(this.G_MOTION == GCommandSet.GDUMMY) this.G_MOTION = GCommandSet.G13;
					else throw new InterpreterException("Twice motion command in same string", currentCommand.getPosInString());
					break;
				case G15:
					if(this.G15_G16 == GCommandSet.GDUMMY) this.G15_G16 = GCommandSet.G15;
					else throw new InterpreterException("Twice polar coordinate command in same string", currentCommand.getPosInString());
					break;
				case G16:
					if(this.G15_G16 == GCommandSet.GDUMMY) this.G15_G16 = GCommandSet.G16;
					else throw new InterpreterException("Twice polar coordinate command in same string", currentCommand.getPosInString());
					break;
				case G17:
					if(this.G17_G18_G19 == GCommandSet.GDUMMY) this.G17_G18_G19 = GCommandSet.G17;
					else throw new InterpreterException("Twice plane selection command in same string", currentCommand.getPosInString());
					break;
				case G18:
					if(this.G17_G18_G19 == GCommandSet.GDUMMY) this.G17_G18_G19 = GCommandSet.G18;
					else throw new InterpreterException("Twice plane selection command in same string", currentCommand.getPosInString());
					break;
				case G19:
					if(this.G17_G18_G19 == GCommandSet.GDUMMY) this.G17_G18_G19 = GCommandSet.G19;
					else throw new InterpreterException("Twice plane selection command in same string", currentCommand.getPosInString());
					break;
				case G20:
					if(this.G20_G21 == GCommandSet.GDUMMY) this.G20_G21 = GCommandSet.G20;
					else throw new InterpreterException("Twice units change command in same string", currentCommand.getPosInString());
					break;
				case G21:
					if(this.G20_G21 == GCommandSet.GDUMMY) this.G20_G21 = GCommandSet.G21;
					else throw new InterpreterException("Twice units change command in same string", currentCommand.getPosInString());
					break;
				case G28:
					if(this.G_NON_MODAL == GCommandSet.GDUMMY) this.G_NON_MODAL = GCommandSet.G28;
					else throw new InterpreterException("Twice homing command in same string", currentCommand.getPosInString());
					break;
				case G28_1:
					if(this.G_NON_MODAL == GCommandSet.GDUMMY) this.G_NON_MODAL = GCommandSet.G28_1;
					else throw new InterpreterException("Twice homing command in same string", currentCommand.getPosInString());
					break;
				case G30:
					if(this.G_NON_MODAL == GCommandSet.GDUMMY) this.G_NON_MODAL = GCommandSet.G30;
					else throw new InterpreterException("Twice homing command in same string", currentCommand.getPosInString());
					break;
				case G31:
					if(this.G_MOTION == GCommandSet.GDUMMY) this.G_MOTION = GCommandSet.G31;
					else throw new InterpreterException("Twice motion command in same string", currentCommand.getPosInString());
					break;
				case G40:
					if(this.G40_G41_G42 == GCommandSet.GDUMMY) this.G40_G41_G42 = GCommandSet.G40;
					else throw new InterpreterException("Twice cutter radius compensation change command in same string", currentCommand.getPosInString());
					break;
				case G41:
					if(this.G40_G41_G42 == GCommandSet.GDUMMY) this.G40_G41_G42 = GCommandSet.G41;
					else throw new InterpreterException("Twice cutter radius compensation change command in same string", currentCommand.getPosInString());
					break;
				case G42:
					if(this.G40_G41_G42 == GCommandSet.GDUMMY) this.G40_G41_G42 = GCommandSet.G42;
					else throw new InterpreterException("Twice cutter radius compensation change command in same string", currentCommand.getPosInString());
					break;
				case G43:
					if(this.G43_G49 == GCommandSet.GDUMMY) this.G43_G49 = GCommandSet.G43;
					else throw new InterpreterException("Twice cutter height compensation change command in same string", currentCommand.getPosInString());
					break;
				case G49:
					if(this.G43_G49 == GCommandSet.GDUMMY) this.G43_G49 = GCommandSet.G49;
					else throw new InterpreterException("Twice cutter height compensation change command in same string", currentCommand.getPosInString());
					break;
				case G50:
					if(this.G50_G51 == GCommandSet.GDUMMY) this.G50_G51 = GCommandSet.G50;
					else throw new InterpreterException("Twice scale change command in same string", currentCommand.getPosInString());
					break;
				case G51:
					if(this.G50_G51 == GCommandSet.GDUMMY) this.G50_G51 = GCommandSet.G51;
					else throw new InterpreterException("Twice scale change command in same string", currentCommand.getPosInString());
					break;
				case G52:
					if(this.G_NON_MODAL == GCommandSet.GDUMMY) this.G_NON_MODAL = GCommandSet.G52;
					else throw new InterpreterException("Twice homing command in same string", currentCommand.getPosInString());
					break;
				case G53:
					if(this.G53 == GCommandSet.GDUMMY) this.G53 = GCommandSet.G53;
					else throw new InterpreterException("Twice fixture tool offset command in same string", currentCommand.getPosInString());
					break;
				case G54:
					if(this.G54___G59 == GCommandSet.GDUMMY) this.G54___G59 = GCommandSet.G54;
					else throw new InterpreterException("Twice fixture tool offset command in same string", currentCommand.getPosInString());
					break;
				case G55:
					if(this.G54___G59 == GCommandSet.GDUMMY) this.G54___G59 = GCommandSet.G55;
					else throw new InterpreterException("Twice fixture tool offset command in same string", currentCommand.getPosInString());
					break;
				case G56:
					if(this.G54___G59 == GCommandSet.GDUMMY) this.G54___G59 = GCommandSet.G56;
					else throw new InterpreterException("Twice fixture tool offset command in same string", currentCommand.getPosInString());
					break;
				case G57:
					if(this.G54___G59 == GCommandSet.GDUMMY) this.G54___G59 = GCommandSet.G57;
					else throw new InterpreterException("Twice fixture tool offset command in same string", currentCommand.getPosInString());
					break;
				case G58:
					if(this.G54___G59 == GCommandSet.GDUMMY) this.G54___G59 = GCommandSet.G58;
					else throw new InterpreterException("Twice fixture tool offset command in same string", currentCommand.getPosInString());
					break;
				case G59:
					if(this.G54___G59 == GCommandSet.GDUMMY) this.G54___G59 = GCommandSet.G59;
					else throw new InterpreterException("Twice fixture tool offset command in same string", currentCommand.getPosInString());
					break;
				case G61:
					if(this.G61_G64 == GCommandSet.GDUMMY) this.G61_G64 = GCommandSet.G61;
					else throw new InterpreterException("Twice path control mode command in same string", currentCommand.getPosInString());
					break;
				case G64:
					if(this.G61_G64 == GCommandSet.GDUMMY) this.G61_G64 = GCommandSet.G64;
					else throw new InterpreterException("Twice path control mode command in same string", currentCommand.getPosInString());
					break;
				case G68:
					if(this.G68_G69 == GCommandSet.GDUMMY) this.G68_G69 = GCommandSet.G68;
					else throw new InterpreterException("Twice coordinate rotation command in same string", currentCommand.getPosInString());
					break;
				case G69:
					if(this.G68_G69 == GCommandSet.GDUMMY) this.G68_G69 = GCommandSet.G69;
					else throw new InterpreterException("Twice coordinate rotation command in same string", currentCommand.getPosInString());
					break;
				case G70:
					if(this.G20_G21 == GCommandSet.GDUMMY) this.G20_G21 = GCommandSet.G70;
					else throw new InterpreterException("Twice units change command in same string", currentCommand.getPosInString());
					break;
				case G71:
					if(this.G20_G21 == GCommandSet.GDUMMY) this.G20_G21 = GCommandSet.G71;
					else throw new InterpreterException("Twice units change command in same string", currentCommand.getPosInString());
					break;
				case G73:
					if(this.G80_G89 == GCommandSet.GDUMMY) this.G80_G89 = GCommandSet.G73;
					else throw new InterpreterException("Twice canned cycle command in same string", currentCommand.getPosInString());
					break;
				case G80:
					if(this.G80_G89 == GCommandSet.GDUMMY) this.G80_G89 = GCommandSet.G80;
					else throw new InterpreterException("Twice canned cycle command in same string", currentCommand.getPosInString());
					break;
				case G81:
					if(this.G80_G89 == GCommandSet.GDUMMY) this.G80_G89 = GCommandSet.G81;
					else throw new InterpreterException("Twice canned cycle command in same string", currentCommand.getPosInString());
					break;
				case G82:
					if(this.G80_G89 == GCommandSet.GDUMMY) this.G80_G89 = GCommandSet.G82;
					else throw new InterpreterException("Twice canned cycle command in same string", currentCommand.getPosInString());
					break;
				case G83:
					if(this.G80_G89 == GCommandSet.GDUMMY) this.G80_G89 = GCommandSet.G83;
					else throw new InterpreterException("Twice canned cycle command in same string", currentCommand.getPosInString());
					break;
				case G84:
					if(this.G80_G89 == GCommandSet.GDUMMY) this.G80_G89 = GCommandSet.G84;
					else throw new InterpreterException("Twice canned cycle command in same string", currentCommand.getPosInString());
					break;
				case G85:
					if(this.G80_G89 == GCommandSet.GDUMMY) this.G80_G89 = GCommandSet.G85;
					else throw new InterpreterException("Twice canned cycle command in same string", currentCommand.getPosInString());
					break;
				case G86:
					if(this.G80_G89 == GCommandSet.GDUMMY) this.G80_G89 = GCommandSet.G86;
					else throw new InterpreterException("Twice canned cycle command in same string", currentCommand.getPosInString());
					break;
				case G87:
					if(this.G80_G89 == GCommandSet.GDUMMY) this.G80_G89 = GCommandSet.G87;
					else throw new InterpreterException("Twice canned cycle command in same string", currentCommand.getPosInString());
					break;
				case G88:
					if(this.G80_G89 == GCommandSet.GDUMMY) this.G80_G89 = GCommandSet.G88;
					else throw new InterpreterException("Twice canned cycle command in same string", currentCommand.getPosInString());
					break;
				case G89:
					if(this.G80_G89 == GCommandSet.GDUMMY) this.G80_G89 = GCommandSet.G89;
					else throw new InterpreterException("Twice canned cycle command in same string", currentCommand.getPosInString());
					break;
				case G90:
					if(this.G90_G91 == GCommandSet.GDUMMY) this.G90_G91 = GCommandSet.G90;
					else throw new InterpreterException("Twice distance mode command in same string", currentCommand.getPosInString());
					break;
				case G90_1:
					if(this.G90_1_G91_1 == GCommandSet.GDUMMY) this.G90_1_G91_1 = GCommandSet.G90_1;
					else throw new InterpreterException("Twice arc center distance mode command in same string", currentCommand.getPosInString());
					break;
				case G91:
					if(this.G90_G91 == GCommandSet.GDUMMY) this.G90_G91 = GCommandSet.G91;
					else throw new InterpreterException("Twice distance mode command in same string", currentCommand.getPosInString());
					break;
				case G91_1:
					if(this.G90_1_G91_1 == GCommandSet.GDUMMY) this.G90_1_G91_1 = GCommandSet.G91_1;
					else throw new InterpreterException("Twice arc center distance mode command in same string", currentCommand.getPosInString());
					break;
				case G92:
					if(this.G_NON_MODAL == GCommandSet.GDUMMY) this.G_NON_MODAL = GCommandSet.G92;
					else throw new InterpreterException("Twice homing command in same string", currentCommand.getPosInString());
					break;
				case G93:
					if(this.G93_G94_G95 == GCommandSet.GDUMMY) this.G93_G94_G95 = GCommandSet.G93;
					else throw new InterpreterException("Twice feed rate mode change command in same string", currentCommand.getPosInString());
					break;
				case G94:
					if(this.G93_G94_G95 == GCommandSet.GDUMMY) this.G93_G94_G95 = GCommandSet.G94;
					else throw new InterpreterException("Twice feed rate mode change command in same string", currentCommand.getPosInString());
					break;
				case G95:
					if(this.G93_G94_G95 == GCommandSet.GDUMMY) this.G93_G94_G95 = GCommandSet.G95;
					else throw new InterpreterException("Twice feed rate mode change command in same string", currentCommand.getPosInString());
					break;
				case G98:
					if(this.G98_G99 == GCommandSet.GDUMMY) this.G98_G99 = GCommandSet.G98;
					else throw new InterpreterException("Twice cycle return mode command in same string", currentCommand.getPosInString());
					break;
				case G99:
					if(this.G98_G99 == GCommandSet.GDUMMY) this.G98_G99 = GCommandSet.G99;
					else throw new InterpreterException("Twice cycle return mode command in same string", currentCommand.getPosInString());
					break;
				default:
					throw new InterpreterException("Unsupported G code num", currentCommand.getPosInString());
				}
				break;
			case M:
                MCommandSet m_command = null;
                try { // TODO change this smelling code - M
                    m_command = this.McommandByNumber(currentCommand.getCurrentValue());
                } catch (EvolutionException e) {
                    e.printStackTrace();
                }
                switch(m_command){
				case M0:
					if(this.M1_M2_M3 == MCommandSet.MDUMMY) this.M1_M2_M3 = MCommandSet.M0;
					else throw new InterpreterException("Twice stopping command in same string", currentCommand.getPosInString());
					break;
				case M1:
					if(this.M1_M2_M3 == MCommandSet.MDUMMY) this.M1_M2_M3 = MCommandSet.M1;
					else throw new InterpreterException("Twice stopping command in same string", currentCommand.getPosInString());
					break;
				case M2:
					if(this.M1_M2_M3 == MCommandSet.MDUMMY) this.M1_M2_M3 = MCommandSet.M2;
					else throw new InterpreterException("Twice stopping command in same string", currentCommand.getPosInString());
					break;
				case M3:
					if(this.M3_M4_M5 == MCommandSet.MDUMMY) this.M3_M4_M5 = MCommandSet.M3;
					else throw new InterpreterException("Twice spindle rotation command in same string", currentCommand.getPosInString());
					break;
				case M4:
					if(this.M3_M4_M5 == MCommandSet.MDUMMY) this.M3_M4_M5 = MCommandSet.M4;
					else throw new InterpreterException("Twice spindle rotation command in same string", currentCommand.getPosInString());
					break;
				case M5:
					if(this.M3_M4_M5 == MCommandSet.MDUMMY) this.M3_M4_M5 = MCommandSet.M5;
					else throw new InterpreterException("Twice spindle rotation command in same string", currentCommand.getPosInString());
					break;
				case M6:
					if(this.M6 == MCommandSet.MDUMMY) this.M6 = MCommandSet.M5;
					else throw new InterpreterException("Twice change tool command in same string", currentCommand.getPosInString());
					break;
				case M7:
					if(this.M7_M8_M9 == MCommandSet.MDUMMY) this.M7_M8_M9 = MCommandSet.M7;
					else throw new InterpreterException("Twice coolant mode command in same string", currentCommand.getPosInString());
					break;
				case M8:
					if(this.M7_M8_M9 == MCommandSet.MDUMMY) this.M7_M8_M9 = MCommandSet.M8;
					else throw new InterpreterException("Twice coolant mode command in same string", currentCommand.getPosInString());
					break;
				case M9:
					if(this.M7_M8_M9 == MCommandSet.MDUMMY) this.M7_M8_M9 = MCommandSet.M9;
					else throw new InterpreterException("Twice coolant mode command in same string", currentCommand.getPosInString());
					break;
				case M30:
					if(this.M1_M2_M3 == MCommandSet.MDUMMY) this.M1_M2_M3 = MCommandSet.M30;
					else throw new InterpreterException("Twice stopping command in same string", currentCommand.getPosInString());
					break;
				case M47:
					if(this.M47_M98_M99 == MCommandSet.MDUMMY) this.M47_M98_M99 = MCommandSet.M47;
					else throw new InterpreterException("Twice execution control command in same string", currentCommand.getPosInString());
					break;
				case M48:
					if(this.M48_M49 == MCommandSet.MDUMMY) this.M48_M49 = MCommandSet.M48;
					else throw new InterpreterException("Twice override command in same string", currentCommand.getPosInString());
					break;
				case M49:
					if(this.M48_M49 == MCommandSet.MDUMMY) this.M48_M49 = MCommandSet.M49;
					else throw new InterpreterException("Twice override command in same string", currentCommand.getPosInString());
					break;
				case M98:
					if(this.M47_M98_M99 == MCommandSet.MDUMMY) this.M47_M98_M99 = MCommandSet.M98;
					else throw new InterpreterException("Twice execution control command in same string", currentCommand.getPosInString());
					break;
				case M99:
					if(this.M47_M98_M99 == MCommandSet.MDUMMY) this.M47_M98_M99 = MCommandSet.M99;
					else throw new InterpreterException("Twice execution control command in same string", currentCommand.getPosInString());
					break;
				default:
					throw new InterpreterException("Unsupported M code num", currentCommand.getPosInString());
				};
				break;
			case N: // nothing to do
				break;
			case O:
                try { // TODO change this smelling code - O
                    moduleNum_ = commandValueExpression.integerEvaluate();
                } catch (EvolutionException e) {
                    e.printStackTrace();
                }
                break;
			case S:
				this.spindelSpeed_ = commandValueExpression;
				break;
			case T:
				this.tool_ = commandValueExpression;
				break;
			default:
				throw new InterpreterException("Unsupported command", currentCommand.getPosInString());
			}
		}
//        Log.d(LOG_TAG, this.toString());
	}
	
	public void evaluate() throws EvolutionException{
		// evalution sequence strictly in order described by "Mach3 G and M code reference"
		// every evolution change interpreter's virtual CNC-machine state or generate HAL command
		// and add it in HAL execution sequence
		// 1 display message
		if(this.message_ != null){
            ProgramLoader.command_sequence.add(new CCommandMessage(this.message_));
//            Log.i("GCODE MESSAGE: ",this.message_);
        }

		// 2 set feed rate mode
        // TODO check needed
		this.G93_G94_G95.evaluate(this.wordList_);
		
		// 3 set feed rate (F)
		if(this.feedRate_ != null){
            InterpreterState.feedRate.setFeedRate(this.feedRate_.evaluate());
        }

		// 4 set spindel speed (S)
		if(this.spindelSpeed_ != null){
            InterpreterState.spindle.setSpeed(this.spindelSpeed_.evaluate());
            double newSpindelSpeed = InterpreterState.spindle.getSpeed();
            ProgramLoader.command_sequence.add(new CCommandSpindelSpeed(newSpindelSpeed));
        }

		// 5 select tool (T)
		if(this.tool_ != null)
			InterpreterState.toolSet.setCurrentTool((int)this.tool_.evaluate());
		
		// 6 tool change macro M6
		this.M6.evaluate();
		
		// 7 set spindel rotation
		this.M3_M4_M5.evaluate();
		
		// 8 set coolant state
		this.M7_M8_M9.evaluate();
		
		// 9 set overrides
		this.M48_M49.evaluate();
		
		// 10 dwell
		this.G4.evaluate(this.wordList_);

		// 11 set active plane
		this.G17_G18_G19.evaluate(this.wordList_);
		
		// maybe it should be in another place of this sequence
		this.G15_G16.evaluate(this.wordList_);
		
		// 12 set length units
		this.G20_G21.evaluate(this.wordList_);
		
		// 13 set cutter radius compensation
		this.G40_G41_G42.evaluate(this.wordList_);
		
		// 14 set tool table offset
		this.G43_G49.evaluate(this.wordList_);
		
		// 15 fixture table select
		this.G54___G59.evaluate(this.wordList_);
		
		// 16 set path control mode
		this.G61_G64.evaluate(this.wordList_);
		
		// 17 set distance mode
		this.G90_G91.evaluate(this.wordList_);
		this.G53.evaluate(this.wordList_);
		this.G68_G69.evaluate(this.wordList_);
		
		// 18 set canned cycle return level mode
		this.G98_G99.evaluate(this.wordList_);
		
		// 19 homing and coordinate system offset non modal commands
		this.G_NON_MODAL.evaluate(this.wordList_);
		
		// 20 perform motion
		this.G_MOTION.evaluate(this.wordList_);

        this.M1_M2_M3.evaluate();

		int size = this.varAssignmentSet_.size();
		for(int i=0; i<size; i++){
			ExpressionVarAssignment currentVar = this.varAssignmentSet_.get(i);
			currentVar.evaluate();
		}
	}

	public GCommandSet GcommandByNumber(double x){
		int tmp = (int)(10*x);
		for(int i=0; i< GCommandSet.GDUMMY.ordinal(); i++){
			if(GCommandSet.values()[i].number == tmp) return GCommandSet.values()[i];
		};
		return GCommandSet.GDUMMY;
	}

	private MCommandSet McommandByNumber(double x) {
		int tmp = (int)(x);
		for(int i=0; i< MCommandSet.MDUMMY.ordinal(); i++){
			if(MCommandSet.values()[i].number == tmp) return MCommandSet.values()[i];
		};
		return MCommandSet.MDUMMY;
	}

	@Override
	public String toString(){
		String result = "";
		if(message_  != null) result += "MSG = " + this.message_ + "; ";
		if(feedRate_ != null) result += "FeedRate = " + feedRate_.toString() + "; ";
		if(tool_ != null)     result += "Tool = " + tool_.toString() + "; ";
		if(spindelSpeed_ != null) result += "Spindle speed = " + spindelSpeed_.toString() + "; ";
        result += super.toString();
		return result;
	}

	public boolean isModuleStart() {
		return (this.moduleNum_ > 0);
	}
	
	public int getModuleNum(){
		return this.moduleNum_;
	}

	public boolean isProgramEnd() {
		return ((this.M1_M2_M3 == MCommandSet.M2)||(this.M1_M2_M3 == MCommandSet.M30));
	}
}
