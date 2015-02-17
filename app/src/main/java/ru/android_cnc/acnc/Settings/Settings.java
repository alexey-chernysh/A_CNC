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

package ru.android_cnc.acnc.Settings;

public class Settings {

	private static double work_vel_ = 30.0; // mm/sec
	private static double free_vel_ = 30.0; // mm/sec
	private static double start_vel_ = 3.0; // mm/sec
	private static double accel_ = 10.0; // mm/sec/sec
	
	private static double perf_vel_ = 1.0; // mm/sec
	private static double perf_vel_dur_ = 2.0; // sec
	
	public static final double minLine = 0.05; // mm
	public static final double angleTol = 0.01; // mm
	public static final double pointTol = 0.01; // mm

	public static double getWorkSpeed() { return work_vel_;	}
	public static void   setWorkSpeed(double work_vel) { Settings.work_vel_ = work_vel; }
	public static double getWorkSpeedMM2Min() {	return 60*work_vel_; }
	public static void   setWorkSpeedMM2Min(double work_vel) { Settings.work_vel_ = work_vel/60.0; }

	public static double getFreeSpeed() { return free_vel_;	}
	public static void   setFreeSpeed(double free_vel) { Settings.free_vel_ = free_vel; }
	public static double getFreeSpeedMM2Min() { return 60.0*free_vel_;	}
	public static void   setFreeSpeedMM2Min(double free_vel) { Settings.free_vel_ = free_vel/60.0; }

	public static double getStartSpeed() { return start_vel_; }
	public static void   setStartSpeed(double start_vel) { Settings.start_vel_ = start_vel; }
	public static double getStartSpeedMM2Min() { return 60.0*start_vel_; }
	public static void   setStartSpeedMM2Min(double start_vel) { Settings.start_vel_ = start_vel/60.0; }

	public static double getPerforationSpeed() { return perf_vel_; }
	public static void   setPerforationSpeed(double perf_vel) { Settings.perf_vel_ = perf_vel; }
	public static double getPerforationSpeedMM2Min() { return 60.0*perf_vel_; }
	public static void   setPerforationSpeedMM2Min(double perf_vel) { Settings.perf_vel_ = perf_vel/60.0; }

	public static double getPerfSpeedDuration() { return perf_vel_dur_;	}
	public static void   setPerfSpeedDuration(double perf_vel_dur) { Settings.perf_vel_dur_ = perf_vel_dur; }

	public static double getAcceleration() { return accel_;	}
	public static void   setAcceleration(double accel) { Settings.accel_ = accel; }

}
