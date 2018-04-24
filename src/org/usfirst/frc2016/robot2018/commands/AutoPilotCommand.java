package org.usfirst.frc2016.robot2018.commands;

import org.usfirst.frc2016.robot2018.Robot;
import org.usfirst.frc2016.robot2018.AutoPilot.AutoPilotMethod;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

// class to start AutoPilot sequences
public class AutoPilotCommand extends CommandGroup {
	String sequenceKey;
	Boolean initFromFile = false;
	
	public AutoPilotCommand(String sequenceKey) {
		this.sequenceKey = sequenceKey;
		
        requires(Robot.driveTrainSRX);
        requires(Robot.cubePickup);
        requires(Robot.arm);
	}
	
	public AutoPilotCommand(String sequenceKey, Boolean loadFile) {
		this.sequenceKey = sequenceKey;
		initFromFile = loadFile;
		
        requires(Robot.driveTrainSRX);
        requires(Robot.cubePickup);
        requires(Robot.arm);
	}
	
	// Called just before this Command runs the first time
	@Override
	protected void initialize() {
		if (initFromFile) {
			Robot.autoPilotRobot.loadFile();
		}
		Robot.autoPilotRobot.initialize(sequenceKey);
	}

	// Called repeatedly when this Command is scheduled to run
	@Override
	public void execute() {
		Robot.autoPilotRobot.execute();
	}

	// Make this return true when this Command no longer needs to run execute()
	@Override
	protected boolean isFinished() {
		return Robot.autoPilotRobot.isFinished();
	}

	// Called once after isFinished returns true
	@Override
	protected void end() {
		Robot.autoPilotRobot.end();
	}

	// Called when another command which requires one or more of the same
	// subsystems is scheduled to run
	@Override
	protected void interrupted() {
		Robot.autoPilotRobot.interrupted();
	}
}
