/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

//package edu.wpi.first.wpilibj.templates;

package org.usfirst.frc2016.robot2018;
import org.usfirst.frc2016.robot2018.Defaults;
import org.usfirst.frc2016.robot2018.Robot;
import org.usfirst.frc2016.robot2018.commands.*;

import edu.wpi.first.wpilibj.Preferences;

/**
 *
 * @author Montagna
 */
public class RobotPrefs {
	// This class handles initializing and reading presets from the cRio NV RAM
	Preferences prefs;
	public double straightGearSpeed, straightGearDistance;	
	public RobotPrefs() {

	}

	// This is used to see if the operator wants to load new values from
	// the RobRio NVRAM. If so robotPrefs class will perform the operation.
	public void periodic() {
		if (Robot.oi.operatorJoy.getRawButton(RobotMap.PREFS_BUTTON)) {
			doLoadPrefs();
		}
	}
	// Read the values stored in NV RAM and store them in variables
 public	void doLoadPrefs() {
 
		/*
		 * After setting the value, setMax must be called
		 * Unlike the other prefs, drivetrain does not use the limit in every cycle
		 * instead, it is set once by robotbuilder generated code.
		 *  The call to setMax will update the limit.
		 */

		Robot.arm.presetPositions[Robot.arm.LOW] = 
				prefs.getDouble("Arm "+Robot.arm.ArmPositionLabels[Robot.arm.LOW], Defaults.ARMPOSITION1);
		Robot.arm.presetPositions[Robot.arm.MEDIUM] = 
				prefs.getDouble("Arm "+Robot.arm.ArmPositionLabels[Robot.arm.MEDIUM], Defaults.ARMPOSITION2);
		Robot.arm.presetPositions[Robot.arm.HIGH] = 
				prefs.getDouble("Arm "+Robot.arm.ArmPositionLabels[Robot.arm.HIGH], Defaults.ARMPOSITION3);
		/*
 		Robot.highGoalShooter.presetSpeed[2] = 
				prefs.getDouble("Wheel "+Robot.highGoalShooter.WheelSpeedLabels[2], Defaults.WHEEL_SPEED2);
		Robot.highGoalShooter.presetSpeed[3] = 
				prefs.getDouble("Wheel "+Robot.highGoalShooter.WheelSpeedLabels[3], Defaults.WHEEL_SPEED3);
*/
		Robot.driveTrainSRX.drivetrainVoltageLimit = prefs.getDouble("drivetrainVoltageLimit", Defaults.DRIVETRAIN_VOLTAGE_LIMIT_DEFAULT);
        Robot.driveTrainSRX.setMax();
		Robot.cubePickup.wheelSpeed = prefs.getDouble("CubeWheelSpeed0", Defaults.CUBE_WHEEL_SPEED0);
        Robot.driveTrainSRX.setMax();
	}


	// Used to store initial values and create entries in the cRio NVRAM
	void setupPrefs() {
		//Setup the nv RAM in the Roborio
		prefs = Preferences.getInstance();
		
		if (!prefs.containsKey("drivetrainVoltageLimit")) {
			prefs.putDouble("drivetrainVoltageLimit", Defaults.DRIVETRAIN_VOLTAGE_LIMIT_DEFAULT);
		}
		if (!prefs.containsKey("CubeWheelSpeed0")) {
			prefs.putDouble("CubeWheelSpeed0", Defaults.CUBE_WHEEL_SPEED0);
		}
		if (!prefs.containsKey("Arm "+Robot.arm.ArmPositionLabels[Robot.arm.LOW])) {
			prefs.putDouble("Arm "+Robot.arm.ArmPositionLabels[Robot.arm.LOW], Defaults.ARMPOSITION1);
		}
		if (!prefs.containsKey("Arm "+Robot.arm.ArmPositionLabels[Robot.arm.MEDIUM]))  {	
			prefs.putDouble("Arm "+Robot.arm.ArmPositionLabels[Robot.arm.MEDIUM], Defaults.ARMPOSITION2);
		}
		if (!prefs.containsKey("Arm "+Robot.arm.ArmPositionLabels[Robot.arm.HIGH]))  {	
			prefs.putDouble("Arm "+Robot.arm.ArmPositionLabels[Robot.arm.HIGH], Defaults.ARMPOSITION3);
		}
	}
}
