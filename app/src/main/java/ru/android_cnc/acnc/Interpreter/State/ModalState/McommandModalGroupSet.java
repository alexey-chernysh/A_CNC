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

public enum McommandModalGroupSet {
	M_GROUP4_PROGRAM_CONTROL,  // M0, M1, M2, M30 stopping
	M_GROUP6_TOOL_CHANGE,  // M6 tool change
	M_GROUP7_SPINDLE_TURNING,  // M3, M4, M5 spindle turning
	M_GROUP8_COOLANT,  // M7, M8, M9 coolant (special case: M7 and M8 may be active at the same time)
	M_GROUP9_OVERRIDES,  // M48, M49 enable/disable feed and speed override controls
	M_GROUP0_NON_MODAL;
}
