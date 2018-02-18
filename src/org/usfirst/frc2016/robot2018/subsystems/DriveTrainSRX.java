// RobotBuilder Version: 2.0
//
// This file was generated by RobotBuilder. It contains sections of
// code that are automatically generated and assigned by robotbuilder.
// These sections will be updated in the future when you export to
// Java from RobotBuilder. Do not put any code or make any change in
// the blocks indicating autogenerated code or it will be lost on an
// update. Deleting the comments indicating the section will prevent
// it from being updated in the future.


package org.usfirst.frc2016.robot2018.subsystems;

import org.usfirst.frc2016.robot2018.Defaults;
import org.usfirst.frc2016.robot2018.Robot;
import org.usfirst.frc2016.robot2018.Config;
import org.usfirst.frc2016.robot2018.RobotMap;
import org.usfirst.frc2016.robot2018.MMW_DifferentialDrive;
import org.usfirst.frc2016.robot2018.commands.*;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.MotorSafetyHelper;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import com.ctre.phoenix.motorcontrol.*;
// BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=IMPORTS
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=IMPORTS

/**
 *
 */
@SuppressWarnings("deprecation")
public class DriveTrainSRX extends Subsystem {
	static int finalLeft, finalRight;
	// BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTANTS

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTANTS

	// BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DECLARATIONS
    private final WPI_TalonSRX talonDriveLeft2 = RobotMap.driveTrainSRXTalonDriveLeft2;
    private final WPI_TalonSRX talonDriveLeft3 = RobotMap.driveTrainSRXTalonDriveLeft3;
    private final WPI_TalonSRX talonDriveRight2 = RobotMap.driveTrainSRXTalonDriveRight2;
    private final WPI_TalonSRX talonDriveRight3 = RobotMap.driveTrainSRXTalonDriveRight3;
    private final WPI_TalonSRX talonDriveLeft1 = RobotMap.driveTrainSRXTalonDriveLeft1;
    private final WPI_TalonSRX talonDriveRight1 = RobotMap.driveTrainSRXTalonDriveRight1;

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DECLARATIONS

	private final MMW_DifferentialDrive differentialDrive = RobotMap.driveTrainSRXDifferentialDrive;

	//    private final RobotDrive robotDrive = RobotMap.drivetrainRobotDrive;
	private final double SPEED_P = .15;
	private final double SPEED_I = .05;
	private final double speedFeedForward = .6;
	private final double COUNTS_PER_INCH = 4096/12.57;  //Encoder counts / inch of travel
	private final int MAX_POSITION_ERROR = 40;

	/*
	 * The following block of variables are used to hold values loaded from
	 * NV RAM by RobotPrefs.
	 */
	public double drivetrainVoltageLimit;
	public double rampIncrement;
	public double driveP;
	public double driveI;
	public double driveD;
	public double driveF;
	public int cruiseVelocity;
	public int acceleration;
	public boolean joySquare;
	/*
	 * ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
	 * End of values set by RobotPrefs
	 */

	private double lastDesiredSpeed = 0;

	private double leftCurrentSpeed = 0;
	private double rightCurrentSpeed = 0;

	private double lastJoyLeft, lastJoyRight;
	private String lastDriveMode;
	private double lastRightCount, lastLeftCount;
	private double accumSpeed =0;
	StringBuilder _sb = new StringBuilder();
	int _loops = 0;

	/*
	 * Magic Motion vales
	 * 
	 * Cim max no load speed 5330
	 * Cim load speed 5330 * .9 = 4797
	 * Gear box 6.66:1 
	 * 4797/6.66 = 720.2 RPMs
	 * 720.2/60 = 12.0 RPS
	 * 4096 counts/rev * 12.0 RPS = 49,152 Counts / second
	 * 49152/1000*100 = 4915.2 Revs Per 100ms
	 * SRX intrnal speed is -1023 to +1023
	 * Feed forward = 1023/4915.2 = .8333
	 * 
	 * Wheel diameter = 4 inches
	 * 4 * Pi = 12.57 inches per rev
	 * 12 inches /sec = 4096 counts/second
	 * 4096/1000 * 100 = 409.6 counts/100ms
	 * Cruise Velocity = 410
	 * 
	 * Plan on .25 seconds to accelerate and .25 to decelerate
	 * Acceleration = 205
	 */

	public DriveTrainSRX() {
		loadConfig(Robot.config);
		talonDriveLeft1.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative , 0, 0);
		talonDriveLeft1.setSensorPhase(true); //!!!! Check this !!!!!
		talonDriveLeft1.setInverted(false);
		talonDriveLeft1.configAllowableClosedloopError(0, 0, 0);
		talonDriveLeft1.configForwardLimitSwitchSource(
				LimitSwitchSource.FeedbackConnector,
				LimitSwitchNormal.NormallyOpen, 0);
		talonDriveLeft1.configForwardSoftLimitEnable(false, 0);
		talonDriveLeft1.configReverseSoftLimitEnable(false, 0);
		talonDriveLeft1.clearStickyFaults(0);
		talonDriveLeft1.setIntegralAccumulator(0, 0, 0);
		talonDriveLeft1.setNeutralMode(NeutralMode.Brake);
		talonDriveLeft1.set(ControlMode.PercentOutput, 0);
		talonDriveLeft1.config_kP(0, driveP, 0);
		talonDriveLeft1.config_kI(0, driveI, 0);
		talonDriveLeft1.config_kD(0, driveD, 0);
		talonDriveLeft1.config_kF(0, driveF, 0);

		// talonDriveLeft1.setSafetyEnabled(false);

		talonDriveLeft2.setInverted(false);
		talonDriveLeft2.configAllowableClosedloopError(0, 0, 0);
		talonDriveLeft2.configForwardLimitSwitchSource(
				LimitSwitchSource.FeedbackConnector,
				LimitSwitchNormal.NormallyOpen, 0);
		talonDriveLeft2.configForwardSoftLimitEnable(false, 0);
		talonDriveLeft2.configReverseSoftLimitEnable(false, 0);
		talonDriveLeft2.clearStickyFaults(0);
		talonDriveLeft2.setIntegralAccumulator(0, 0, 0);
		talonDriveLeft2.setNeutralMode(NeutralMode.Brake);
		talonDriveLeft2.set(ControlMode.Follower, 0);

		talonDriveLeft3.setInverted(false);
		talonDriveLeft3.configAllowableClosedloopError(0, 0, 0);
		talonDriveLeft3.configForwardLimitSwitchSource(
				LimitSwitchSource.FeedbackConnector,
				LimitSwitchNormal.NormallyOpen, 0);
		talonDriveLeft3.configForwardSoftLimitEnable(false, 0);
		talonDriveLeft3.configReverseSoftLimitEnable(false, 0);
		talonDriveLeft3.clearStickyFaults(0);
		talonDriveLeft3.setIntegralAccumulator(0, 0, 0);
		talonDriveLeft3.setNeutralMode(NeutralMode.Brake);
		talonDriveLeft3.set(ControlMode.Follower, 0);

		talonDriveRight1.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative , 0, 0);
		talonDriveRight1.setSensorPhase(true); //!!!! Check this !!!!!
		talonDriveRight1.setInverted(true);
		talonDriveRight1.configAllowableClosedloopError(0, 0, 0);
		talonDriveRight1.configForwardLimitSwitchSource(
				LimitSwitchSource.FeedbackConnector,
				LimitSwitchNormal.NormallyOpen, 0);
		talonDriveRight1.configForwardSoftLimitEnable(false, 0);
		talonDriveRight1.configReverseSoftLimitEnable(false, 0);
		talonDriveRight1.clearStickyFaults(0);
		talonDriveRight1.setIntegralAccumulator(0, 0, 0);
		talonDriveRight1.setNeutralMode(NeutralMode.Brake);
		talonDriveRight1.set(ControlMode.PercentOutput, 0);
		talonDriveRight1.config_kP(0, driveP, 0);
		talonDriveRight1.config_kI(0, driveI, 0);
		talonDriveRight1.config_kD(0, driveD, 0);
		talonDriveRight1.config_kF(0, driveF, 0);
		// 	talonDriveRight1.setSafetyEnabled(false);
		talonDriveRight2.setInverted(true);
		talonDriveRight2.configAllowableClosedloopError(0, 0, 0);
		talonDriveRight2.configForwardLimitSwitchSource(
				LimitSwitchSource.FeedbackConnector,
				LimitSwitchNormal.NormallyOpen, 0);
		talonDriveRight2.configForwardSoftLimitEnable(false, 0);
		talonDriveRight2.configReverseSoftLimitEnable(false, 0);
		talonDriveRight2.clearStickyFaults(0);
		talonDriveRight2.setIntegralAccumulator(0, 0, 0);
		talonDriveRight2.setNeutralMode(NeutralMode.Brake);
		talonDriveRight2.set(ControlMode.Follower, 3);

		talonDriveRight3.setInverted(true);
		talonDriveRight3.configAllowableClosedloopError(0, 0, 0);
		talonDriveRight3.configForwardLimitSwitchSource(
				LimitSwitchSource.FeedbackConnector,
				LimitSwitchNormal.NormallyOpen, 0);
		talonDriveRight3.configForwardSoftLimitEnable(false, 0);
		talonDriveRight3.configReverseSoftLimitEnable(false, 0);
		talonDriveRight3.clearStickyFaults(0);
		talonDriveRight3.setIntegralAccumulator(0, 0, 0);
		talonDriveRight3.setNeutralMode(NeutralMode.Brake);
		talonDriveRight3.set(ControlMode.Follower, 3);

		/*
		 * Additional settings for motion magic Left
		 */
		/* Set relevant frame periods to be at least as fast as periodic rate*/
		talonDriveLeft1.setStatusFramePeriod(StatusFrameEnhanced.Status_13_Base_PIDF0, 10, 0);
		talonDriveLeft1.setStatusFramePeriod(StatusFrameEnhanced.Status_10_MotionMagic, 10, 0);

		/* set the peak and nominal outputs */
		talonDriveLeft1.configNominalOutputForward(0, 0);
		talonDriveLeft1.configNominalOutputReverse(0, 0);
		talonDriveLeft1.configPeakOutputForward(1, 0);
		talonDriveLeft1.configPeakOutputReverse(-1, 0);

		/* set acceleration and vcruise velocity - see documentation */
		talonDriveLeft1.configMotionCruiseVelocity(cruiseVelocity, 0);
		talonDriveLeft1.configMotionAcceleration(acceleration, 0);

		/*
		 * Additional settings for motion magic Right
		 */

		/* Set relevant frame periods to be at least as fast as periodic rate*/
		talonDriveRight1.setStatusFramePeriod(StatusFrameEnhanced.Status_13_Base_PIDF0, 10, 0);
		talonDriveRight1.setStatusFramePeriod(StatusFrameEnhanced.Status_10_MotionMagic, 10, 0);

		/* set the peak and nominal outputs */
		talonDriveRight1.configNominalOutputForward(0, 0);
		talonDriveRight1.configNominalOutputReverse(0, 0);
		talonDriveRight1.configPeakOutputForward(1, 0);
		talonDriveRight1.configPeakOutputReverse(-1, 0);

		/* set acceleration and vcruise velocity - see documentation */
		talonDriveRight1.configMotionCruiseVelocity(cruiseVelocity, 0);
		talonDriveRight1.configMotionAcceleration(acceleration, 0);
	}


	public void periodic() {
	}

	// Put methods for controlling this subsystem
	// here. Call these from Commands.

	/**
	 * Limit motor values to the -1.0 to +1.0 range.
	 */
	protected double limit(double value) {
		if (value > 1.0) {
			return 1.0;
		}
		if (value < -1.0) {
			return -1.0;
		}
		return value;
	}

	protected double applyDeadband(double value, double deadband) {
		if (Math.abs(value) > deadband) {
			if (value > 0.0) {
				return (value - deadband) / (1.0 - deadband);
			} else {
				return (value + deadband) / (1.0 - deadband);
			}
		} else {
			return 0.0;
		}
	}

	/**
	 * Arcade drive method for differential drive platform.
	 *
	 * @param xSpeed        The robot's speed along the X axis [-1.0..1.0]. Forward is positive.
	 * @param zRotation     The robot's rotation rate around the Z axis [-1.0..1.0]. Clockwise is
	 *                      positive.
	 * @param squaredInputs If set, decreases the input sensitivity at low speeds.
	 */
	public void arcadeDriveX(double xSpeed, double zRotation, boolean squaredInputs) {
		xSpeed = limit(xSpeed);
		xSpeed = applyDeadband(xSpeed, .01);

		zRotation = limit(zRotation);
		zRotation = applyDeadband(zRotation, .01);

		// Square the inputs (while preserving the sign) to increase fine control
		// while permitting full power.
		if (squaredInputs) {
			xSpeed = Math.copySign(xSpeed * xSpeed, xSpeed);
			zRotation = Math.copySign(zRotation * zRotation, zRotation);
		}

		double leftMotorOutput;
		double rightMotorOutput;

		double maxInput = Math.copySign(Math.max(Math.abs(xSpeed), Math.abs(zRotation)), xSpeed);

		if (xSpeed >= 0.0) {
			// First quadrant, else second quadrant
			if (zRotation >= 0.0) {
				leftMotorOutput = maxInput;
				rightMotorOutput = xSpeed - zRotation;
			} else {
				leftMotorOutput = xSpeed + zRotation;
				rightMotorOutput = maxInput;
			}
		} else {
			// Third quadrant, else fourth quadrant
			if (zRotation >= 0.0) {
				leftMotorOutput = xSpeed + zRotation;
				rightMotorOutput = maxInput;
			} else {
				leftMotorOutput = maxInput;
				rightMotorOutput = xSpeed - zRotation;
			}
		}

		differentialDrive.tankDrive(-leftMotorOutput, rightMotorOutput, joySquare);
	}

	public void gameVelocityDrive(Joystick joy) {
		final double MAX_SPEED = 2000;
		double leftOut, rightOut;
		double rightYstick = -joy.getY();
		double motorOutput = talonDriveRight1.getMotorOutputPercent();
		/* prepare line to print */
		_sb.append("\tout:");
		_sb.append(motorOutput);
		_sb.append("\tspd:");
		_sb.append(talonDriveRight1.getSelectedSensorVelocity(0));

		if (joy.getRawButton(1)) {
			/* Speed mode */
			/* Convert 500 RPM to units / 100ms.
			 * 4096 Units/Rev * 500 RPM / 600 100ms/min in either direction:
			 * velocity setpoint is in units/100ms
			 */
			double targetVelocity_UnitsPer100ms = rightYstick * 500.0 * 4096 / 600;
			/* 500 RPM in either direction */
			talonDriveRight1.set(ControlMode.Velocity, targetVelocity_UnitsPer100ms);

			/* append more signals to print when in speed mode. */
			_sb.append("\terr:");
			_sb.append(talonDriveRight1.getClosedLoopError(0));
			_sb.append("\ttrg:");
			_sb.append(targetVelocity_UnitsPer100ms);
		} else {
			/* Percent voltage mode */
			SmartDashboard.putNumber("Vel Pad Y", rightYstick);
			talonDriveRight1.set(ControlMode.PercentOutput, rightYstick);

		}

		if (++_loops >= 10) {
			_loops = 0;
			System.out.println(_sb.toString());
		}
		_sb.setLength(0);
		//differentialDrive.tankDrive(0,0);

		/*
		leftOut = joy.getY()*MAX_SPEED;
		rightOut = joy.getRawAxis(5) * MAX_SPEED;
		talonDriveLeft1.set(ControlMode.Velocity, leftOut);
		talonDriveRight1.set(ControlMode.Velocity, rightOut);
		 */   
		pingDifferentialDrive();
	}

	/*
	 * Used for single joystick device using X and Y
	 */
	public void arcadeDrive(Joystick joy) {
		accumSpeed = 0;
		lastJoyLeft = joy.getY();
		lastJoyRight = joy.getX();
//		differentialDrive.arcadeDrive(joy.getY(), joy.getX(), true);
		arcadeDriveX(joy.getY(), joy.getX(), true);
		lastDriveMode = "ArcadeJoy";
	}

	/*
	 * Arcade drive using game pad axis 5 as direction
	 */
	public void arcadeDrive(Joystick leftJoy, Joystick rightJoy) {
		accumSpeed = 0;
		lastJoyRight= leftJoy.getRawAxis(5);
		lastJoyLeft = leftJoy.getY();
		// The values to pass to the motors are adjusted by the ramp method
		leftCurrentSpeed = returnRamp(leftCurrentSpeed, lastJoyLeft);
		rightCurrentSpeed = returnRamp(rightCurrentSpeed, lastJoyRight);
		//SmartDashboard.putNumber("LJoyY", lastJoyLeft);
		//SmartDashboard.putNumber("LCurrentSpeed", leftCurrentSpeed);
//		differentialDrive.arcadeDrive(leftCurrentSpeed, rightCurrentSpeed, true);
		arcadeDriveX(leftCurrentSpeed, rightCurrentSpeed, true);
		lastDriveMode = "ArcadeJoy";
	}

	/*
	 * Arcade drive using values for speed and direction
	 */
	public void arcadeDrive(double speed, double direction) {
		velocityDrive(speed, direction);
		lastJoyLeft = speed;
		lastJoyRight = direction;
		lastDriveMode = "Arcade";
	}

	/*
	 * Arcade style drive using the gyro for feed back
	 * 
	 * Special case:
	 * 	If the speed is zero, take the speed from the left joy stick.
	 */
	public void gyroDrive(double speed, double angle) {
		double steer =  (Robot.gyro.getAngle() - angle);
		if (steer > 180) {
			steer = steer - 360;
		}
		else if (steer < -180) {
			steer = steer + 360;
		}
		steer *= -Robot.gyro.gyroP;

		if (steer > Robot.gyro.TURN_MAX) {
			steer = Robot.gyro.TURN_MAX;
		}
		else if (steer < -Robot.gyro.TURN_MAX) {
			steer = -Robot.gyro.TURN_MAX;
		}
		if (speed== 0) {
			//Use the joystick or stop if centered
			//Robot.drivetrain.arcadeDrive(Robot.oi.driveRight.getY(), steer);
			//velocityDrive(Robot.oi.driveRight.getY(), steer);
			//        	robotDrive.arcadeDrive(Robot.oi.driveRight.getY(), steer);
//			differentialDrive.arcadeDrive(Robot.oi.driveLeft.getY(), steer);
			arcadeDriveX(Robot.oi.driveLeft.getY(), steer ,false);
		}
		else {
			//    		Robot.drivetrain.arcadeDrive(speed, steer);
			velocityDrive(speed, steer);
			lastJoyLeft = speed;
			lastJoyRight = steer;
			lastDriveMode = "Gyro";
		}
	}

	/*
	 * Used by other methods for sort of PID speed control 
	 */
	public void velocityDrive(double speed, double direction) {
		double rateError;
		double finalSpeed;
		double averageRate;
		finalSpeed = 0;
		speed /= 10;
		if (Math.abs(speed) < .01) speed = 0;
		if (speed==0) {
			accumSpeed = 0;
		}
		else
		{
			if (accumSpeed == 0)
			{
				accumSpeed = (speed > 0) ? speedFeedForward : -speedFeedForward;
			}

			averageRate=getAverageRate();
			rateError = speed + averageRate/300;
			accumSpeed += rateError * SPEED_I;
			if (accumSpeed > .7) {
				accumSpeed = .7;
			}
			else if (accumSpeed <-.7) {
				accumSpeed = -.7;
			}
			finalSpeed = accumSpeed + rateError * SPEED_P;
			SmartDashboard.putNumber("rateError", rateError);
			SmartDashboard.putNumber("accumSpeed", accumSpeed);
		}

//		differentialDrive.arcadeDrive(finalSpeed, direction);
		arcadeDriveX(finalSpeed, direction, false);
		lastJoyLeft = finalSpeed;
		lastJoyRight = direction;
		lastDriveMode = "Velocity";
	}

	/*
	 * Method for driving a fixed distance straight
	 * This may get expanded to steer as well.
	 * For now it's a test to used motion magic to drive
	 */
	public void goToUsingMM(double speed, int distance, double direction) {
		int distanceAsCounts;
		
		// convert absolute distance into absolute encoder counts
		distanceAsCounts = (int)Math.round(distance * COUNTS_PER_INCH);

		// for now reset encoders to make debugging easier
		// May not want to do this in the final code.
		// resetEncoders();
		// resetEncoders();
		SmartDashboard.putNumber("Left After Reset", getLeftEncoder());
		// SmartDashboard.putNumber("Right After Reset", getRightEncoder());
		/*
		 * Get the current encoder counts and 
		 * convert distance counts to relative counts
		 * It may be better to reset the counters.
		 */
		// Dont reset the counters since it is not reliable
		finalLeft = distanceAsCounts + getLeftEncoder();
		finalRight = distanceAsCounts + getRightEncoder();

		// Reset the speed in case someone else changed it.
		talonDriveLeft1.configMotionCruiseVelocity(cruiseVelocity, 0);
		talonDriveRight1.configMotionCruiseVelocity(cruiseVelocity, 0);
		talonDriveLeft1.set(ControlMode.MotionMagic, finalLeft);
		talonDriveRight1.set(ControlMode.MotionMagic, finalRight);
		/*
		 * Will need to disable motorSafetyHepler when not using differentialDrive Calls
		 */
		//	SmartDashboard.putNumber("FinalRight", finalRight);
		//	SmartDashboard.putNumber("Distance in Counts", distanceAsCounts);
	}
	
	public void gotoUsingMM(int  leftDistance, int rightDistance) {
		int distanceAsCountsLeft;
		int distanceAsCountsRight;
		int leftCruiseVelocity;
		int rightCruiseVelocity;
		
		// convert absolute distance into absolute encoder counts
		distanceAsCountsLeft = (int)Math.round(leftDistance * COUNTS_PER_INCH);
		distanceAsCountsRight = (int)Math.round(rightDistance * COUNTS_PER_INCH);
		
		// Dont reset the counters since it is not reliable
		finalLeft = distanceAsCountsLeft + getLeftEncoder();
		finalRight = distanceAsCountsRight + getRightEncoder();

		// Now adjust the speed for the distance difference
		double distanceRatio = leftDistance/rightDistance;
		if (distanceRatio > 1) {
			leftCruiseVelocity = (int)Math.round(cruiseVelocity * distanceRatio);
			rightCruiseVelocity = cruiseVelocity;
		}
		else {
			leftCruiseVelocity = cruiseVelocity;
			rightCruiseVelocity = (int)Math.round(cruiseVelocity/distanceRatio);
		}
		
		// Reset the speed in case someone else changed it.
		talonDriveLeft1.configMotionCruiseVelocity(leftCruiseVelocity, 0);
		talonDriveRight1.configMotionCruiseVelocity(rightCruiseVelocity, 0);
		talonDriveLeft1.set(ControlMode.MotionMagic, finalLeft);
		talonDriveRight1.set(ControlMode.MotionMagic, finalRight);
	}

	public void pingDifferentialDrive() {
		differentialDrive.pingMotorSafety();
	}

	/*
	 * Method to go with the above move used to determine
	 * when the move has completed
	 */
	public boolean moveComplete() {
		boolean leftGood = Math.abs(finalLeft - getLeftEncoder()) < MAX_POSITION_ERROR;
		boolean rightGood = Math.abs(finalRight - getRightEncoder()) < MAX_POSITION_ERROR;
		//  SmartDashboard.putNumber("TalonDriveRight position", finalRight);
		SmartDashboard.putBoolean("Right Good", rightGood);
		SmartDashboard.putBoolean("Left Good", leftGood);
		SmartDashboard.putNumber("Left Error", finalLeft - getLeftEncoder());
		SmartDashboard.putNumber("Right Error", finalRight - getRightEncoder());
		return (leftGood && rightGood);
	}
	/*
	 * Returns the average encoder rate of left and right 
	 */
	public double getAverageRate() {
		double rightRate, leftRate, averageRate;

		rightRate = talonDriveRight1.getSelectedSensorVelocity(0);
		leftRate = talonDriveLeft1.getSelectedSensorVelocity(0);
		averageRate=(leftRate+rightRate)/2;
		return averageRate;
	}

	/*
	 * Tank drive using game pad where right comes from axis 5
	 */
	public void tankDrive(Joystick leftJoy, Joystick rightJoy) {
		accumSpeed = 0;
		lastJoyRight= rightJoy.getRawAxis(5);
		lastJoyLeft = leftJoy.getY();

		// The values to pass to the motors are adjusted by the ramp method
		leftCurrentSpeed = returnRamp(leftCurrentSpeed, lastJoyLeft);
		rightCurrentSpeed = returnRamp(rightCurrentSpeed, lastJoyRight);
		SmartDashboard.putNumber("LSpeed", leftCurrentSpeed);
		SmartDashboard.putNumber("RSpeed", rightCurrentSpeed);
		SmartDashboard.putNumber("lastJoyLeft", lastJoyLeft);
		differentialDrive.tankDrive(-leftCurrentSpeed, rightCurrentSpeed, true);
		lastDriveMode = "Tank";    	
	}	

	/*
	 * Stop the robot
	 */
	public void driveStop() {
		differentialDrive.tankDrive(0,0);
		accumSpeed = 0;
	}

	/*
	 * Set the max voltage to the drive train
	 * The joystick values will be scaled by this value
	 */
	public void setMax() {
		differentialDrive.setMaxOutput(drivetrainVoltageLimit);
	}

	// This method performs the ramp calculation for the drive train
	double returnRamp(double currentSpeed, double desiredSpeed) {
		double delta = desiredSpeed - currentSpeed;

		if (delta > rampIncrement) {
			delta = rampIncrement;
		}
		else if ( delta < - rampIncrement) {
			delta = - rampIncrement;
		}
		return (currentSpeed + delta);
	}

	public int getRightEncoder() {

		return talonDriveRight1.getSelectedSensorPosition(0);
	}

	public int getLeftEncoder() {
		return talonDriveLeft1.getSelectedSensorPosition(0);
	}

	public void resetEncoders() {
		talonDriveLeft1.setSelectedSensorPosition(0, 0, 10);
		talonDriveRight1.setSelectedSensorPosition(0, 0, 10);
	}

	public void addTelemetryHeaders() {
		Robot.currentMonitor.registerMonitorDevive(talonDriveLeft1, "Drive Left 1");
		Robot.currentMonitor.registerMonitorDevive(talonDriveLeft2, "Drive Left 2");
		Robot.currentMonitor.registerMonitorDevive(talonDriveLeft3, "Drive Left 3");
		Robot.currentMonitor.registerMonitorDevive(talonDriveRight1, "Drive Right 1");
		Robot.currentMonitor.registerMonitorDevive(talonDriveRight2, "Drive Right 2");
		Robot.currentMonitor.registerMonitorDevive(talonDriveRight3, "Drive Right 3");
		Robot.telem.addColumn("Drive Left");
		Robot.telem.addColumn("Drive Right");
		Robot.telem.addColumn("Drive Mode");
	}

	public void writeTelemetry() {
		Robot.telem.saveDouble("Drive Left", lastJoyLeft);
		Robot.telem.saveDouble("Drive Right", lastJoyRight);
		Robot.telem.saveString("Drive Mode", lastDriveMode);
	}

	public void loadConfig(Config config) {
		drivetrainVoltageLimit = config.getDouble("DriveVLimit", Defaults.DRIVETRAIN_VOLTAGE_LIMIT_DEFAULT);
		setMax();
		rampIncrement = config.getDouble("DriveRampIncrement", Defaults.DRIVETRAIN_RAMP_INCREMENT);
		driveP = config.getDouble("DriveP", Defaults.DRIVETRAIN_P);
		driveI = config.getDouble("DriveI", Defaults.DRIVETRAIN_I);
		driveD = config.getDouble("DriveD", Defaults.DRIVETRAIN_D);
		driveF = config.getDouble("DriveF", Defaults.DRIVETRAIN_F);
		cruiseVelocity = config.getInt("CruiseVelocity", Defaults.DRIVE_CRUISEVELOCITY);
		acceleration = config.getInt("Acceleration", Defaults.DRIVE_ACCELERATION);
		joySquare = config.getBoolean("DriveJoystickSquare", Defaults.DRIVE_JOYSQUARE);
	}
	@Override
	public void initDefaultCommand() {
		// BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DEFAULT_COMMAND

        setDefaultCommand(new GamePadDrive());

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DEFAULT_COMMAND

		// Set the default command for a subsystem here.
		// setDefaultCommand(new MySpecialCommand());
	}

}

