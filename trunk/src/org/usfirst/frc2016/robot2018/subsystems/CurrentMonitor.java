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

import org.usfirst.frc2016.robot2018.RobotMap;
import org.usfirst.frc2016.robot2018.commands.*;

import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
// BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=IMPORTS
import edu.wpi.first.wpilibj.PowerDistributionPanel;

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=IMPORTS


/**
 *
 */
public class CurrentMonitor extends Subsystem {
	private final int numPdPorts = 16;
	private final int FAILTHRESHOLD = 100;
	private final double THROTTLETHRESHOLD = .2;
	private final int
	            leftDrivePort1=0,
			    leftDrivePort2=2,
			    leftDrivePort3=3;
	private final int
	            rightDrivePort1=14,
	            rightDrivePort2=13,
	            rightDrivePort3=1;
	
	private final int
				winchPort1 = 9,
				winchPort2 = 11;
	public CurrentStatus[] currentStatus = CurrentStatus.currentStatusSet(numPdPorts);

    // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTANTS

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTANTS

    // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DECLARATIONS
    private final PowerDistributionPanel powerDistributionPanel1 = RobotMap.currentMonitorPowerDistributionPanel1;

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DECLARATIONS

    @Override
    public void initDefaultCommand() {
        // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DEFAULT_COMMAND


    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DEFAULT_COMMAND

        // Set the default command for a subsystem here.
        // setDefaultCommand(new MySpecialCommand());
    }
	// Put methods for controlling this subsystem
    // here. Call these from Commands.


    @Override
    public void periodic() {
        // Put code here to be run every loop
    	// Drive train
    	for (int i=0; i<numPdPorts; i++) {
    		if (Math.abs(currentStatus[i].throttle) > THROTTLETHRESHOLD ) {
    			if (powerDistributionPanel1.getCurrent(i) == 0 && !currentStatus[i].fail) {
    				currentStatus[i].failCount++;
    				if (currentStatus[i].failCount >= FAILTHRESHOLD) {
    					currentStatus[i].fail = true;
    			    }
    		    }
    			else if (!currentStatus[i].fail) {
    				currentStatus[i].failCount=0;
    			}
    	    }
    	}
    	SmartDashboard.putNumber("Fail count 9", currentStatus[9].failCount);
    	SmartDashboard.putNumber("Fail count 11", currentStatus[11].failCount);
    	SmartDashboard.putNumber("Right1 Current", powerDistributionPanel1.getCurrent(14));
    	SmartDashboard.putNumber("Right2 Current", powerDistributionPanel1.getCurrent(13));
    	SmartDashboard.putNumber("Right3 Current", powerDistributionPanel1.getCurrent(1));
    	SmartDashboard.putNumber("Left1 Current", powerDistributionPanel1.getCurrent(0));
    	SmartDashboard.putNumber("Left2 Current", powerDistributionPanel1.getCurrent(2));
    	SmartDashboard.putNumber("Left3 Current", powerDistributionPanel1.getCurrent(3));
    }
    		
    public void driveTrainCurrentReport(double leftThrottle, double rightThrottle) {
    	currentStatus[leftDrivePort1].throttle=leftThrottle;
    	currentStatus[leftDrivePort2].throttle=leftThrottle;
    	currentStatus[leftDrivePort3].throttle=leftThrottle;
    	currentStatus[rightDrivePort1].throttle=rightThrottle;
    	currentStatus[rightDrivePort2].throttle=rightThrottle;
    	currentStatus[rightDrivePort3].throttle=rightThrottle;
    }
    
    public void winchCurrentReport(double throttle) {
    	currentStatus[winchPort1].throttle = throttle;
    	currentStatus[winchPort2].throttle = throttle;
    }

    public boolean driveLeftFault() {
    	return(currentStatus[leftDrivePort1].fail ||
    			currentStatus[leftDrivePort2].fail ||
    			currentStatus[leftDrivePort3].fail);
    }
    
    public boolean driveRightFault() {
    	return(currentStatus[rightDrivePort1].fail ||
    			currentStatus[rightDrivePort2].fail ||
    			currentStatus[rightDrivePort3].fail);
    }
    
    public boolean winchFault1() {
    	return(currentStatus[winchPort1].fail);
    }
    
    public boolean winchFault2() {
    	return(currentStatus[winchPort2].fail);
    }
}

