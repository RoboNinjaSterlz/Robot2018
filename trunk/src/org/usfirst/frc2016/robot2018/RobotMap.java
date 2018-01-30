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

// BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=IMPORTS
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=IMPORTS
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.DigitalInput;

/**
 * The RobotMap is a mapping from the ports sensors and actuators are wired into
 * to a variable name. This provides flexibility changing wiring, makes checking
 * the wiring easier and significantly reduces the number of magic numbers
 * floating around.
 */
public class RobotMap {

	// Button Defines
	public static final int
    // Button to load prefs
	PREFS_BUTTON = 10;
//    public static RobotDrive drivetrainSRXRobotDrive;

    // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DECLARATIONS
    public static SpeedController winchWinchSpark;
    public static PowerDistributionPanel currentMonitorPowerDistributionPanel1;
    public static WPI_TalonSRX driveTrainSRXTalonDriveLeft2;
    public static WPI_TalonSRX driveTrainSRXTalonDriveLeft3;
    public static WPI_TalonSRX driveTrainSRXTalonDriveRight2;
    public static WPI_TalonSRX driveTrainSRXTalonDriveRight3;
    public static WPI_TalonSRX driveTrainSRXTalonDriveLeft1;
    public static WPI_TalonSRX driveTrainSRXTalonDriveRight1;
    public static DifferentialDrive driveTrainSRXDifferentialDrive;

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DECLARATIONS
    public static DigitalInput gearInDetector;
    public static DigitalInput didWeGrab;
    public static void init() {
        // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTRUCTORS
        winchWinchSpark = new Talon(9);
        LiveWindow.addActuator("Winch", "WinchSpark", (Talon) winchWinchSpark);
        winchWinchSpark.setInverted(false);
        currentMonitorPowerDistributionPanel1 = new PowerDistributionPanel(0);
        LiveWindow.addSensor("CurrentMonitor", "PowerDistributionPanel 1", currentMonitorPowerDistributionPanel1);
        
        driveTrainSRXTalonDriveLeft2 = new WPI_TalonSRX(1);
        driveTrainSRXTalonDriveLeft3 = new WPI_TalonSRX(2);
        driveTrainSRXTalonDriveRight2 = new WPI_TalonSRX(4);
        driveTrainSRXTalonDriveRight3 = new WPI_TalonSRX(5);
        driveTrainSRXTalonDriveLeft1 = new WPI_TalonSRX(0);
        driveTrainSRXTalonDriveRight1 = new WPI_TalonSRX(3);
        
        driveTrainSRXDifferentialDrive = new DifferentialDrive(driveTrainSRXTalonDriveLeft1, driveTrainSRXTalonDriveRight1);
        LiveWindow.addActuator("DriveTrainSRX", "DifferentialDrive", driveTrainSRXDifferentialDrive);
        driveTrainSRXDifferentialDrive.setSafetyEnabled(true);
        driveTrainSRXDifferentialDrive.setExpiration(0.1);
        driveTrainSRXDifferentialDrive.setMaxOutput(0.95);


    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTRUCTORS

/*
        drivetrainSRXRobotDrive = new RobotDrive(driveTrainSRXTalonDriveLeft1, driveTrainSRXTalonDriveRight1);
        drivetrainSRXRobotDrive.setSafetyEnabled(true);
        drivetrainSRXRobotDrive.setExpiration(0.1);
        drivetrainSRXRobotDrive.setSensitivity(0.5);
        drivetrainSRXRobotDrive.setMaxOutput(0.95);
        drivetrainSRXRobotDrive.setInvertedMotor(RobotDrive.MotorType.kRearLeft, true);
*/        
        
        gearInDetector = new DigitalInput(4);
        didWeGrab = new DigitalInput(5);
    }
}
