/*
 * Copyright 2014-2015 Alexey Chernysh
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package ru.android_cnc.acnc.Interpreter.State.ModalState;

public enum GcommandModalGroupSet {
	G_GROUP1_MOTION,  // G00, G01, G02, G03
	G_GROUP2_PLANE,  // G17, G18, G19 plane selection
	G_GROUP3_DISTANCE_MODE,  // G90, G91 distance mode
	G_GROUP4_ARC_DISTANCE_MODE, // Arc IJK Distance Mode
	G_GROUP5_FEED_RATE_MODE,  // G93, G94, G95 feed rate mode
	G_GROUP6_UNITS,  // G20, G21, G70, G71 units
	G_GROUP7_CUTTER_RADIUS_COMPENSATION,  // G40, G41, G42 cutter radius compensation
	G_GROUP8_TOOL_LENGHT_OFFSET,  // G43, G49 tool length offset
	G_GROUP9_CANNED_CYCLES, // G73, G80, G81, G82, G84, G85, G86, G87, G88, G89
	G_GROUP10_CANNED_CYCLES_RETURN_MODE, // G98, G99 return mode in canned cycles
	G_GROUP12_OFFSET_SELECTION, // G54, G55, G56, G57, G58, G59, G59.xxx coordinate system selection
	G_GROUP13_PATH_CONTROL_MODE, // G61, G61.1, G64 path control mode
	G_GROUP16_COORDINATE_ROTATION, // G68, G69 coordinate rotaton selection
	G_GROUP17_POLAR_COORDINATES, // G15, G16 polar coordinate mode
	G_GROUP18_SCALING, // G50, G51 coodinate scale enable/disable
	G_GROUP0_NON_MODAL,  // G10, G28, G30, G53, G92, G92.1, G92.2, G92.3 non modal group
	G_GROUP0_G53_MODIFIER, // G53
	G_GROUP0_G4_DWELL;  // G4 dwell only
}
