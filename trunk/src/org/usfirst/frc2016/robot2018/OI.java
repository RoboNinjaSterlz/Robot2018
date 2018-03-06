// RobotBuilder Version: 2.0
//
// This file was generated by RobotBuilder. It contains sections of
// code that are automatically generated and assigned by robotbuilder.
// These sections will be updated in the future when you export to
// Java from RobotBuilder. Do not put any code or make any change in
// the blocks indicating autogenerated code or it will be lost on an
// update. Deleting the comments indicating the section will prevent
// it from being updated in the future.


package org.usfirst.frc2016.robot2018;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.*;

import org.usfirst.frc2016.robot2018.POVTrigger;
import org.usfirst.frc2016.robot2018.commands.*;
/**
 * This class is the glue that binds the controls on the physical operator
 * interface to the commands and command groups that allow control of the robot.
 */
public class OI {
    //// CREATING BUTTONS
    // One type of button is a joystick button which is any button on a joystick.
    // You create one by telling it which joystick it's on and which button
    // number it is.
    // Joystick stick = new Joystick(port);
    // Button button = new JoystickButton(stick, buttonNumber);

    // There are a few additional built in buttons you can use. Additionally,
    // by subclassing Button you can create custom triggers and bind those to
    // commands the same as any other Button.

    //// TRIGGERING COMMANDS WITH BUTTONS
    // Once you have a button, it's trivial to bind it to a button in one of
    // three ways:

    // Start the command when the button is pressed and let it run the command
    // until it is finished as determined by it's isFinished method.
    // button.whenPressed(new ExampleCommand());

    // Run the command while the button is being held down and interrupt it once
    // the button is released.
    // button.whileHeld(new ExampleCommand());

    // Start the command when the button is released  and let it run the command
    // until it is finished as determined by it's isFinished method.
    // button.whenReleased(new ExampleCommand());


    // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DECLARATIONS
    public JoystickButton driveStraight;
    public Joystick driveLeft;
    public JoystickButton openArmButton;
    public JoystickButton shootButton;
    public JoystickButton armLowButton;
    public JoystickButton armMediumButton;
    public JoystickButton armHighButton;
    public JoystickButton loadPrefsButton;
    public JoystickButton armFloorButton;
    public JoystickButton armScaleButton;
    public Joystick operatorJoy;

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DECLARATIONS
    public POVTrigger POVFWDButton;
    public POVTrigger POVREVButton;
    
    public OI() {
        // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTRUCTORS

        operatorJoy = new Joystick(2);
        
        armScaleButton = new JoystickButton(operatorJoy, 8);
        armScaleButton.whenPressed(new ArmScale());
        armFloorButton = new JoystickButton(operatorJoy, 1);
        armFloorButton.whenPressed(new ArmFloor());
        loadPrefsButton = new JoystickButton(operatorJoy, 7);
        loadPrefsButton.whenPressed(new LoadPrefs());
        armHighButton = new JoystickButton(operatorJoy, 4);
        armHighButton.whenPressed(new ArmHigh());
        armMediumButton = new JoystickButton(operatorJoy, 3);
        armMediumButton.whenPressed(new ArmMedium());
        armLowButton = new JoystickButton(operatorJoy, 2);
        armLowButton.whenPressed(new ArmLow());
        shootButton = new JoystickButton(operatorJoy, 5);
        shootButton.whileHeld(new ShootCube());
        openArmButton = new JoystickButton(operatorJoy, 6);
        openArmButton.whileHeld(new OpenArm());
        driveLeft = new Joystick(0);
        
        driveStraight = new JoystickButton(driveLeft, 6);
        driveStraight.whileHeld(new DriveStraight());


        // SmartDashboard Buttons
        SmartDashboard.putData("TestArcDrive: test 36 15", new TestArcDrive(0, 12836));
        SmartDashboard.putData("TurnLeftOrRight: false", new TurnLeftOrRight(false));
        SmartDashboard.putData("DriveUsingFile", new DriveUsingFile2());

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTRUCTORS

        POVFWDButton = new POVTrigger(1);
//        POVFWDButton.whileHeld(new IntakeOut());
        POVREVButton = new POVTrigger(3);
//        POVREVButton.whileHeld(new CompBallIntake());
    }

    // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=FUNCTIONS
    public Joystick getDriveLeft() {
        return driveLeft;
    }

    public Joystick getOperatorJoy() {
        return operatorJoy;
    }


    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=FUNCTIONS
}

