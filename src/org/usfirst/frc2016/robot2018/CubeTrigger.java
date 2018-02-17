package org.usfirst.frc2016.robot2018;

import edu.wpi.first.wpilibj.buttons.*;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.command.*;

public class CubeTrigger extends Button {
	public boolean weGrabbed;
	private int activeCount;
	private int retryCount;
	private boolean lastTrue;
	
	private final int RETRYLIMIT = 10;
	
	CubeTrigger() {
		activeCount = 0;
		retryCount = 0;
		lastTrue = false;
	}

	public boolean get() {
		boolean state;
		// Count so we don't trigger on the first detection
		if (!RobotMap.cubeDetector.get()) {
			activeCount++;
		}
		else {
			activeCount = 0;
		}

		if (activeCount>5 )	// Must be there for 5 counts before we say good.
		{	
			lastTrue = true;
			return true;
		}
		else {
			lastTrue = false;
			return false;
		}
	}
}
