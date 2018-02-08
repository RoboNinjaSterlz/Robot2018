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

import org.usfirst.frc2016.robot2018.Robot;
import org.usfirst.frc2016.robot2018.Config;
import org.usfirst.frc2016.robot2018.RobotMap;
import org.usfirst.frc2016.robot2018.commands.*;

import edu.wpi.first.wpilibj.RobotDrive;

import org.usfirst.frc2016.robot2018.commands.TankDrive;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;



import com.ctre.phoenix.motorcontrol.*;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.LimitSwitchNormal;
import com.ctre.phoenix.motorcontrol.LimitSwitchSource;
import com.ctre.phoenix.motorcontrol.NeutralMode;
// BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=IMPORTS
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=IMPORTS


/**
 *
 */
@SuppressWarnings("deprecation")
public class DriveTrainSRX extends Subsystem {

    // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTANTS

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTANTS

    // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DECLARATIONS
    private final WPI_TalonSRX talonDriveLeft2 = RobotMap.driveTrainSRXTalonDriveLeft2;
    private final WPI_TalonSRX talonDriveLeft3 = RobotMap.driveTrainSRXTalonDriveLeft3;
    private final WPI_TalonSRX talonDriveRight2 = RobotMap.driveTrainSRXTalonDriveRight2;
    private final WPI_TalonSRX talonDriveRight3 = RobotMap.driveTrainSRXTalonDriveRight3;
    private final WPI_TalonSRX talonDriveLeft1 = RobotMap.driveTrainSRXTalonDriveLeft1;
    private final WPI_TalonSRX talonDriveRight1 = RobotMap.driveTrainSRXTalonDriveRight1;
    private final DifferentialDrive differentialDrive = RobotMap.driveTrainSRXDifferentialDrive;

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DECLARATIONS
//    private final RobotDrive robotDrive = RobotMap.drivetrainRobotDrive;
    private final double SPEED_P = .15;
	private final double SPEED_I = .05;
	private final double speedFeedForward = .6;
	/*
     * The following block of variables are used to hold values loaded from
     * NV RAM by RobotPrefs.
    */
	public double drivetrainVoltageLimit;
	public double rampIncrement = .2;
	private double lastDesiredSpeed = 0;
	
	private double leftCurrentSpeed = 0;
	private double rightCurrentSpeed = 0;
	
	private double lastJoyLeft, lastJoyRight;
	private String lastDriveMode;
	/*
     * ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
     * End of values set by RobotPrefs
     */
    private double lastRightCount, lastLeftCount;
    private double accumSpeed =0;
	StringBuilder _sb = new StringBuilder();
	int _loops = 0;

   public DriveTrainSRX() {
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
   	
   }
   
   
   public void periodic() {
    }

    // Put methods for controlling this subsystem
    // here. Call these from Commands.
   
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
	}

		
		
		
		
/*		leftOut = joy.getY()*MAX_SPEED;
	   rightOut = joy.getRawAxis(5) * MAX_SPEED;
	   talonDriveLeft1.set(ControlMode.Velocity, leftOut);
	   talonDriveRight1.set(ControlMode.Velocity, rightOut);
   }
*/   
   
    public void arcadeDrive(Joystick joy) {
//    	robotDrive.arcadeDrive(joy, true);
    	differentialDrive.arcadeDrive(joy.getY(), joy.getX(), true);
    	lastJoyLeft = joy.getY();
    	lastJoyRight = joy.getX();
    	lastDriveMode = "ArcadeJoy";
    }
    
    public void arcadeDrive(Joystick leftJoy, Joystick rightJoy) {
    	accumSpeed = 0;
    	double rightY;
    	double leftY;
    	//if (Robot.frontCameraActive) {
//    		rightY= adjustDriveValue(rightJoy.getY());
    		rightY= adjustDriveValue(leftJoy.getRawAxis(4));
    		leftY = adjustDriveValue(leftJoy.getY());
        	lastJoyLeft = leftY;
        	lastJoyRight = rightY;
        	lastDriveMode = "ArcadeJoy";
    	//}
    	//else {
    	//	leftY= -adjustDriveValue(rightJoy.getY());
    	//	rightY = -adjustDriveValue(leftJoy.getY());
    	//}
        // The values to pass to the motors are adjusted by the ramp method
        leftCurrentSpeed = returnRamp(leftCurrentSpeed, leftY);
        rightCurrentSpeed = returnRamp(rightCurrentSpeed, rightY);
        //SmartDashboard.putNumber("LJoyY", letfY);
        //SmartDashboard.putNumber("LCurrentSpeed", leftCurrentSpeed);
//    	robotDrive.arcadeDrive(leftCurrentSpeed, rightCurrentSpeed, true);
        differentialDrive.arcadeDrive(leftCurrentSpeed, rightCurrentSpeed, true);
    }
    public void arcadeDrive(double speed, double direction) {
//    	robotDrive.arcadeDrive(speed, direction);
		velocityDrive(speed, direction);
    	lastJoyLeft = speed;
    	lastJoyRight = direction;
    	lastDriveMode = "Arcade";

    }
    
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
        	differentialDrive.arcadeDrive(Robot.oi.driveLeft.getX(), steer);
    	}
    	else {
//    		Robot.drivetrain.arcadeDrive(speed, steer);
    		velocityDrive(speed, steer);
        	lastJoyLeft = speed;
        	lastJoyRight = steer;
        	lastDriveMode = "Gyro";

    	}
    }
    
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
//    	robotDrive.arcadeDrive(finalSpeed, direction);
    	differentialDrive.arcadeDrive(finalSpeed, direction);
    	lastJoyLeft = finalSpeed;
    	lastJoyRight = direction;
    	lastDriveMode = "Velocity";

    }
    
    public double getAverageRate() {
    	double rightRate, leftRate, averageRate;

    	rightRate = talonDriveRight1.getSelectedSensorVelocity(0);
    	leftRate = talonDriveLeft1.getSelectedSensorVelocity(0);
    	averageRate=(leftRate+rightRate)/2;
    	return averageRate;
    }
    
    public void tankDrive(Joystick leftJoy, Joystick rightJoy) {
    	accumSpeed = 0;
    	double rightY;
    	double leftY;
    	rightY= leftJoy.getRawAxis(5);
    	leftY = leftJoy.getY();
    	lastJoyLeft = leftY;
    	lastJoyRight = rightY;
    	lastDriveMode = "Tank";

        // The values to pass to the motors are adjusted by the ramp method
        leftCurrentSpeed = returnRamp(leftCurrentSpeed, leftY);
        rightCurrentSpeed = returnRamp(rightCurrentSpeed, rightY);
        //SmartDashboard.putNumber("LJoyY", letfY);
        SmartDashboard.putNumber("LSpeed", leftCurrentSpeed);
        SmartDashboard.putNumber("RSpeed", rightCurrentSpeed);
//    	robotDrive.tankDrive(leftCurrentSpeed, rightCurrentSpeed);
    	differentialDrive.tankDrive(-leftCurrentSpeed, rightCurrentSpeed, true);
    	
    }	
    
    public void driveStop() {
    
//    	robotDrive.tankDrive(0,0);
    	differentialDrive.tankDrive(0,0);
    	accumSpeed = 0;
    }
    
    public void setMax() {
//        robotDrive.setMaxOutput(drivetrainVoltageLimit);
        differentialDrive.setMaxOutput(drivetrainVoltageLimit);
    }
    
    private double adjustDriveValue(double joyY) {
        // Scale the value by the drive limit to limit speed over full travel of the joystick
        joyY = joyY * drivetrainVoltageLimit;
        
        // Square the values for better control at the low end
        // Check the sign now to fix it later
        boolean ltz = joyY < 0;
        joyY = joyY * joyY;

        /* should no longer be needed with scale above
        // Limit logic
        if (Math.abs(leftY) > RobotMain.drivetrainVoltageLimit) {
            leftY = RobotMain.drivetrainVoltageLimit;
        }
        */
        // Fix the sign
        if (ltz) {
            joyY *= -1;
        }
        
    	return joyY ;
    }

    // This method performs the ramp calculation for the drive train
    double returnRamp(double currentSpeed, double desiredSpeed) {
    	double delta = desiredSpeed - currentSpeed;
/*    	if ( delta < 0 && desiredSpeed > 0) { //slowing from forward, but still forward
    		return desiredSpeed;
    	}
    	if ( delta > 0 && desiredSpeed < 0) { // slowing from reverse but still reverse)
    		return desiredSpeed;
    	}
*/
    	if (delta > rampIncrement) {
    		delta = rampIncrement;
    	}
    	else if ( delta < - rampIncrement) {
    		delta = - rampIncrement;
    	}
    	return (currentSpeed + delta);
    }
    
    public double getRightEncoder() {
    	
    	return talonDriveRight1.getSelectedSensorPosition(0);
    }

    public double getLeftEncoder() {
    	return talonDriveLeft1.getSelectedSensorPosition(0);
    }
    
    public void resetEncoders() {
    }

    public void loadConfig(Config config) {
    	
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

