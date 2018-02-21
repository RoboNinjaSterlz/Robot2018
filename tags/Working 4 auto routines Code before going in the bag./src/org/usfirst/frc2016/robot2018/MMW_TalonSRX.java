/**
 * WPI Compliant motor controller class.
 * WPILIB's object model requires many interfaces to be implemented to use
 * the various features.
 * This includes...
 * - Software PID loops running in the robot controller
 * - LiveWindow/Test mode features
 * - Motor Safety (auto-turn off of motor if Set stops getting called)
 * - Single Parameter set that assumes a simple motor controller.
 */
//package com.ctre.phoenix.motorcontrol.can;
package org.usfirst.frc2016.robot2018;

import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.MotorSafety;
import edu.wpi.first.wpilibj.MotorSafetyHelper;
import edu.wpi.first.wpilibj.Sendable;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilder;
import edu.wpi.first.wpilibj.hal.HAL;

public class MMW_TalonSRX extends WPI_TalonSRX implements SpeedController, Sendable, MotorSafety {

	private ControlMode currentMode;
	
	
	/** Constructor */
	public MMW_TalonSRX(int deviceNumber) {
		super(deviceNumber);
//		HAL.report(66, deviceNumber + 1);
//		_description = "Talon SRX " + deviceNumber;
		/* prep motor safety */
//		_safetyHelper = new MotorSafetyHelper(this);
//		_safetyHelper.setExpiration(0.0);
//		_safetyHelper.setSafetyEnabled(false);

//		LiveWindow.add(this);
//		setName("Talon SRX ", deviceNumber);
		
	}

	public void set(ControlMode mode, double demand0, double demand1) {
		mode = ControlMode.Velocity;
		super.set(mode, demand0, demand1);
	}
}
