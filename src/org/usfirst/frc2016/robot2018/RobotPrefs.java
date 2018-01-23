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
/*		if (Robot.oi.operatorJoy.getRawButton(RobotMap.PREFS_BUTTON)) {
			doLoadPrefs();
		}
		*/
	}
	// Read the values stored in NV RAM and store them in variables
 public	void doLoadPrefs() {
 
		/*
		 * After setting the value, setMax must be called
		 * Unlike the other prefs, drivetrain does not use the limit in every cycle
		 * instead, it is set once by robotbuilder generated code.
		 *  The call to setMax will update the limit.
		 */

		// Straight Gear 
		straightGearSpeed = 
				prefs.getDouble("StraightGearSpeed", Defaults.STRAIGHTGEARSPEED);
		straightGearDistance = 
				prefs.getDouble("StraightGearDistance" , Defaults.STRAIGHTGEARDISTANCE);

		
		//Gear Elevator
		Robot.gearElevator.presetPositions[Robot.gearElevator.LOW] = 
				prefs.getDouble("Elevator "+Robot.gearElevator.ElevatorPositionLabels[Robot.gearElevator.LOW], Defaults.ELEVATORPOSITION1);
		Robot.gearElevator.presetPositions[Robot.gearElevator.PREPICKUP] = 
				prefs.getDouble("Elevator "+Robot.gearElevator.ElevatorPositionLabels[Robot.gearElevator.PREPICKUP], Defaults.ELEVATORPOSITION2);
		Robot.gearElevator.presetPositions[Robot.gearElevator.HIGH] = 
				prefs.getDouble("Elevator "+Robot.gearElevator.ElevatorPositionLabels[Robot.gearElevator.HIGH], Defaults.ELEVATORPOSITION3);
		//Gear Slide
		Robot.gearSlide.lowerLimit = 
				prefs.getDouble("Slide Lower Limit", Defaults.SLIDELOWERLIMIT);
		Robot.gearSlide.upperLimit = 
				prefs.getDouble("Slide Upper Limit", Defaults.SLIDEUPPERLIMIT);
		
		Robot.highGoalShooter.presetSpeed[0] = 
				prefs.getDouble("Wheel "+Robot.highGoalShooter.WheelSpeedLabels[0], Defaults.WHEEL_SPEED0);
		Robot.highGoalShooter.presetSpeed[1] = 
				prefs.getDouble("Wheel "+Robot.highGoalShooter.WheelSpeedLabels[1], Defaults.WHEEL_SPEED1);

		/*
 		Robot.highGoalShooter.presetSpeed[2] = 
				prefs.getDouble("Wheel "+Robot.highGoalShooter.WheelSpeedLabels[2], Defaults.WHEEL_SPEED2);
		Robot.highGoalShooter.presetSpeed[3] = 
				prefs.getDouble("Wheel "+Robot.highGoalShooter.WheelSpeedLabels[3], Defaults.WHEEL_SPEED3);
*/
		Robot.drivetrain.drivetrainVoltageLimit = prefs.getDouble("drivetrainVoltageLimit", Defaults.DRIVETRAIN_VOLTAGE_LIMIT_DEFAULT);
        Robot.drivetrain.setMax();
	}


	// Used to store initial values and create entries in the cRio NVRAM
	void setupPrefs() {
		//Setup the nv RAM in the Roborio
		prefs = Preferences.getInstance();
		
		
		// Straight Gear Placement
		if (!prefs.containsKey("StraightGearSpeed")) {
			prefs.putDouble("StraightGearSpeed", Defaults.STRAIGHTGEARSPEED);
		}
		if (!prefs.containsKey("StraightGearDistance")) {
			prefs.putDouble("StraightGearDistance", Defaults.STRAIGHTGEARDISTANCE);
		}
		// Elevator
		if (!prefs.containsKey("Elevator "+Robot.gearElevator.ElevatorPositionLabels[Robot.gearElevator.LOW])) {
			prefs.putDouble("Elevator "+Robot.gearElevator.ElevatorPositionLabels[Robot.gearElevator.LOW], Defaults.ELEVATORPOSITION1);
		}
		if (!prefs.containsKey("Elevator "+Robot.gearElevator.ElevatorPositionLabels[Robot.gearElevator.PREPICKUP]))  {	
			prefs.putDouble("Elevator "+Robot.gearElevator.ElevatorPositionLabels[Robot.gearElevator.PREPICKUP], Defaults.ELEVATORPOSITION2);
		}
		if (!prefs.containsKey("Elevator "+Robot.gearElevator.ElevatorPositionLabels[Robot.gearElevator.HIGH]))  {	
			prefs.putDouble("Elevator "+Robot.gearElevator.ElevatorPositionLabels[Robot.gearElevator.HIGH], Defaults.ELEVATORPOSITION3);
		}
		// Slide
		if (!prefs.containsKey("Slide Lower Limit")) {
			prefs.putDouble("Slide Lower Limit", Defaults.SLIDELOWERLIMIT);
		}
		if (!prefs.containsKey("Slide Upper Limit")) {
			prefs.putDouble("Slide Upper Limit", Defaults.SLIDEUPPERLIMIT);
		}
		
		if (!prefs.containsKey("Wheel "+Robot.highGoalShooter.WheelSpeedLabels[0])) {
			prefs.putDouble("Wheel "+Robot.highGoalShooter.WheelSpeedLabels[0], Defaults.WHEEL_SPEED0);
		}
		if (!prefs.containsKey("Wheel "+Robot.highGoalShooter.WheelSpeedLabels[1])) {
			prefs.putDouble("Wheel "+Robot.highGoalShooter.WheelSpeedLabels[1], Defaults.WHEEL_SPEED1);
		}
/*
		if (!prefs.containsKey("Wheel "+Robot.highGoalShooter.WheelSpeedLabels[2])) {
			prefs.putDouble("Wheel "+Robot.highGoalShooter.WheelSpeedLabels[2], Defaults.WHEEL_SPEED2);
		}
		if (!prefs.containsKey("Wheel "+Robot.highGoalShooter.WheelSpeedLabels[3])) {
			prefs.putDouble("Wheel "+Robot.highGoalShooter.WheelSpeedLabels[3], Defaults.WHEEL_SPEED3);
		}
 */
		
		if (!prefs.containsKey("drivetrainVoltageLimit")) {
			prefs.putDouble("drivetrainVoltageLimit", Defaults.DRIVETRAIN_VOLTAGE_LIMIT_DEFAULT);
		}
	}
}
