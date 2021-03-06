// RobotBuilder Version: 2.0
//
// This file was generated by RobotBuilder. It contains sections of
// code that are automatically generated and assigned by robotbuilder.
// These sections will be updated in the future when you export to
// Java from RobotBuilder. Do not put any code or make any change in
// the blocks indicating autogenerated code or it will be lost on an
// update. Deleting the comments indicating the section will prevent
// it from being updated in the future.


package org.usfirst.frc2016.robot2018.commands;

import edu.wpi.first.wpilibj.command.Command;

import org.usfirst.frc2016.robot2018.Robot;

/**
 *
 */
public class ArcadeDrive extends Command {
//	public final double gyroP = 0.08;
//	public final double TURN_MAX = 0.6;
	private double lEncoderStart, rEncoderStart;
//	private final double distanceTolerance = 5;
	private int waitCounter;
	private final double DELAYPERCOUNT = .02;
	private final double m_seconds = 5;
    // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=VARIABLE_DECLARATIONS
	double m_speed;
	double m_angle;
	double m_distance;
	
    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=VARIABLE_DECLARATIONS

    // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTRUCTOR
    public ArcadeDrive(double speed, double angle, double distance) {
    	m_speed = speed;
    	m_angle = angle;
    	m_distance = distance;
    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTRUCTOR
        // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=VARIABLE_SETTING

        // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=VARIABLE_SETTING
        // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=REQUIRES

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=REQUIRES
    }
    // Called just before this Command runs the first time
    protected void initialize() {
    	lEncoderStart = Robot.driveTrainSRX.getLeftEncoder();
    	rEncoderStart = Robot.driveTrainSRX.getRightEncoder();
    	//Robot.gyro.reset();
    	
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	//Robot.driveTrainSRX.arcadeDrive(Robot.oi.getDriveLeft());
//    	double steer =  -gyroP * (Robot.gyro.getAngle() - m_angle);
    	waitCounter++;
    	double steer =  (Robot.gyro.getAngle() - m_angle);
    	if (steer > 180) {
    		steer = steer - 360;
    	}
    	else if (steer < -180) {
    		steer = steer + 360;
    	}
    	steer *= -Robot.gyro.gyroP;
    	
    	if (steer > Robot.gyro.gyroTurnMax) {
    		steer = Robot.gyro.gyroTurnMax;
    	}
    	else if (steer < -Robot.gyro.gyroTurnMax) {
    		steer = -Robot.gyro.gyroTurnMax;
    	}
    	if (m_speed== 0) {
    		//Use the joystick or stop if centered
    		Robot.driveTrainSRX.arcadeDrive(Robot.oi.driveLeft.getY(), steer);
    	}
    	else {
    		Robot.driveTrainSRX.arcadeDrive(m_speed, steer);
    	}
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
    	boolean done = false;
    	if ( m_distance !=0 ) {
    		done = (m_distance <= Math.abs((Robot.driveTrainSRX.getRightEncoder() - rEncoderStart)));
    	}
    	else {
    		//if (m_angle != 0 && m_distance == 0) {
    		done = (Math.abs((Robot.gyro.getAngle() - m_angle)) < 1) ;
    	}
    	if (!done) {
    		done = (waitCounter >= (int)(m_seconds/DELAYPERCOUNT));
    	}
    	//else {
    	//	return false;
    	//}
    return done;
    }

    // Called once after isFinished returns true
    protected void end() {
    	Robot.driveTrainSRX.driveStop();
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    }
}
