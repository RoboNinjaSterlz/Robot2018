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
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import org.usfirst.frc2016.robot2018.Robot;

/**
 *
 */
public class PlaceOrNot extends Command {
	private int waitCounter;
	private final double DELAYPERCOUNT = .02;
	private final double TIME2WAIT = .75;
	boolean placeCube, shotCube = false;

	// BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=VARIABLE_DECLARATIONS
    private String m_startingPosition;
 
    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=VARIABLE_DECLARATIONS

    // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTRUCTOR
    public PlaceOrNot(String startingPosition) {

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTRUCTOR
        // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=VARIABLE_SETTING
        m_startingPosition = startingPosition;

        // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=VARIABLE_SETTING
        // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=REQUIRES

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=REQUIRES
    }

    // Called just before this Command runs the first time
    @Override
    protected void initialize() {
    	//SmartDashboard.putString("GameData", ""+Robot.gameData);
    	//SmartDashboard.putString("StartingPosition", ""+ m_startingPosition.charAt(0));
    
    	waitCounter = 0;	// Reset the timer
    	if (Robot.gameData == m_startingPosition.charAt(0)) {
    		placeCube = true;
    	}
    	else {
    		placeCube = false;
    	}
    	//SmartDashboard.putBoolean("placeCube",placeCube);
    }

    // Called repeatedly when this Command is scheduled to run
    @Override
    protected void execute() {
    	waitCounter++;
    	if (placeCube) {
    		Robot.cubePickup.autoEjectCube();
    		shotCube = true;
    	}
    }

    // Make this return true when this Command no longer needs to run execute()
    @Override
    protected boolean isFinished() {
        if (!placeCube) {
        	return true;
        }
    	if (waitCounter >= (int)(TIME2WAIT/DELAYPERCOUNT)) {
    		return true;
    	}
        return false;
    }

    // Called once after isFinished returns true
    @Override
    protected void end() {
    	Robot.cubePickup.autoEndEjectCube();
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    @Override
    protected void interrupted() {
    }
}
