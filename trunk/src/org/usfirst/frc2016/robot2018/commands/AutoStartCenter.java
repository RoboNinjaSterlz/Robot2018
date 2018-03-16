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

import edu.wpi.first.wpilibj.command.CommandGroup;
import org.usfirst.frc2016.robot2018.subsystems.*;

/**
 *
 */
public class AutoStartCenter extends CommandGroup {


    // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=PARAMETERS
    public AutoStartCenter() {

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=PARAMETERS
        // Add Commands here:
        // e.g. addSequential(new Command1());
        //      addSequential(new Command2());
        // these will run in order.

        // To run multiple commands at the same time,
        // use addParallel()
        // e.g. addParallel(new Command1());
        //      addSequential(new Command2());
        // Command1 and Command2 will run in parallel.

        // A command group will require all of the subsystems that each member
        // would require.
        // e.g. if Command1 requires chassis, and Command2 requires arm,
        // a CommandGroup containing them would require both the chassis and the
        // arm.
        // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=COMMAND_DECLARATIONS
        

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=COMMAND_DECLARATIONS
         addParallel(new ArmHigh());
        addSequential(new DelaySeconds(.5));
        addSequential(new ResetGyro());
        addSequential(new GetGameData());
        addSequential(new MagicDrive(0, -15, 0));
        addSequential(new TurnLeftOrRight(true));
        addSequential(new MagicDrive(0, -69, 0));
        addSequential(new ResetGyro());
        addSequential(new TurnLeftOrRight(false));
        addSequential(new MagicDrive(0, -96, 0));
        addSequential(new PlaceCube());

    /*	addParallel(new ArmHigh());
        addSequential(new DelaySeconds(.5));
        addSequential(new ResetGyro());
        addSequential(new GetGameData());
        addSequential(new TestArcDrive(-80, -50));
        addSequential(new TestArcDrive(-45, -75));
        addSequential(new PlaceCube());
    */
    } 
}
