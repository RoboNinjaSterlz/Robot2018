package org.usfirst.frc2016.robot2018;

import edu.wpi.first.wpilibj.buttons.*;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.command.*;

import org.usfirst.frc2016.robot2018.Robot;
import org.usfirst.frc2016.robot2018.RobotMap;

public class GearInTrigger extends Button {
	public boolean weGrabbed;
	private int activeCount;
	private int solenoidActiveCount;
	private int retryCount;
	private boolean lastTrue;
	
	private final int RETRYLIMIT = 10;
	
	GearInTrigger() {
		activeCount = 0;
		solenoidActiveCount = 0;
		retryCount = 0;
		lastTrue = false;
	}

	public boolean get() {
		boolean state;
		// Count so we don't trigger on the first detection
		if (!RobotMap.gearInDetector.get()) {
			activeCount++;
		}
		else {
			activeCount = 0;
		}
		// If this is re-run in auto, make sure the plunger had time to extend before
		// trying again
		if (!Robot.gearGrabber.isGrabbed()) {
			solenoidActiveCount ++;
		}
		else {
			solenoidActiveCount = 0;
		}
		
		/*
		 * Check to see if we had a success
		 * if so, reset the retry counter for next time.
		 * success = in prepickup position with the gear
		 */
		if (Robot.gearElevator.currentPreset() == Robot.gearElevator.PREPICKUP &&
			!RobotMap.didWeGrab.get() ) {
			
			// Successful grab, so reset the count
			retryCount = 0;
		}
		/*
		 * As another way to get auto to work after a retry fail
		 * reset the retry counter when not in auto mode.
		 */
		//!! Leave this commented out for now there appears to be a timing
		// issue on start up when the trigger get is called before cCI is created.
		//if (!Robot.oi.cCI.getRawButton(1)) {
		//	retryCount = 0;
		//}
		/* 
		 * Check where the elevator is
		 * If it isn't in prepare to grab, ignore the switch
		 */
		if (Robot.gearElevator.currentPreset() == Robot.gearElevator.PREPICKUP &&
			Robot.oi.cCI.getRawButton(1) &&  // Must be in auto mode
			!Robot.gearGrabber.isGrabbed() &&
			solenoidActiveCount > 12 && // Make sure grabber had time to open 12 should be about.25 seconds.
			activeCount>5 &&	// Must be there for 5 counts before we say good.
			retryCount <= RETRYLIMIT )
		{	
			/*
			 * The test is valid until the  plunger compresses or the elevator moves
			 * the following logic prevents counting more than 1 for a single trigger.
			 */
			if (!lastTrue) { 
				retryCount++;
			}
			lastTrue = true;
			return true;
		}
		else {
			lastTrue = false;
			return false;
		}
	}
}
