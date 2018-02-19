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

import org.usfirst.frc2016.robot2018.Defaults;
import org.usfirst.frc2016.robot2018.Robot;
import org.usfirst.frc2016.robot2018.RobotMap;
import org.usfirst.frc2016.robot2018.Config;

import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import com.ctre.phoenix.motorcontrol.*;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.LimitSwitchNormal;
import com.ctre.phoenix.motorcontrol.LimitSwitchSource;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
// BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=IMPORTS
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.Solenoid;

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=IMPORTS

public class Arm extends Subsystem {
	private final int NUM_PRESETS = 5;
	/*
	 * Values set by config
	 */
	public int presetPositions[] = new int[NUM_PRESETS];
	private int armReverseSoftLimit;
	private int armForwardSoftLimit;
	private double armP;
	private double armI;
	private double armD;
	private double armF;
	private int armCruiseVelocity;
	/* End config values */
	
	private final String ARMMOTOR = "Arm Motor";
	private final String ARMPOSITION = "Arm Position";
	private final String ARMPOSITIONERROR = "Arm Position Error";
	private final String ARMPRESETPOSITION = "Arm Position Preset";
	private final String ARMSCALEPOSITION = "Arm Scale Position";
	private final String ARMBRAKESTATE = "Arm Brake State";
	
	// Desired encoder count for positioning the armTaloner.
	private double desiredPosition = 0;

	// to know if the last command actually moved the arm
	private boolean didWeMove=true;

	// last preset position requested
	private int lastPreset;
	
	// How good does the position need to be
	private final double AbsoluteTolerance = 3;
	
	// Brake state for telemetry
	private boolean brakeState;

	// Labels for presets in robot prefs, config and on dashboard
	public final String[] ArmPositionLabels = { 
			"Floor",
			"Low",
			"Medium",
			"Scale",
			"High"
	};

	public final int
	FLOOR = 0,
	LOW = 1,
	MEDIUM = 2,
	SCALE = 3,
	HIGH = 4;
	
	/*
	 * Magic Motion vales
	 * 
	 * Mini Cim max no load speed 5840
	 * Mini Cim load speed 5840 * .85 = 4964
	 * Gear box 64:1 
	 * 4964/64 = 77.5 RPMs
	 * 77.5/60 = 1.29 RPS
	 * 4096 counts/rev * 1.29 RPS = 5295 Counts / second
	 * 5295/1000*100 = 529.5 Revs Per 100ms
	 * SRX intrnal speed is -1023 to +1023
	 * Feed forward = 1023/529.5 = 1.93
	 * .227
	 * 
	 * Arm travel approx 1000 counts
	 * Desired travel time 1 second
	 * 1000 Counts/second
	 * 1000/1000 * 100 = 100 counts / 100ms
	 * Cruise Velocity = 100
	 * 
	 * Plan on .5 seconds to accelerate and .5 to decelerate
	 * Acceleration = 200
	 */

	// BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTANTS

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTANTS

	// BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DECLARATIONS
    private final WPI_TalonSRX armTalon = RobotMap.armarmTalon;
    private final Solenoid armBrakeSolenoid = RobotMap.armarmBrakeSolenoid;

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DECLARATIONS


	// Put methods for controlling this subsystem
	// here. Call these from Commands.

	public Arm() {
		loadConfig(Robot.config);
		armTalon.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Absolute , 0, 0);
		armTalon.setSensorPhase(true); //!!!! Check this !!!!!
		armTalon.setInverted(true);
		armTalon.configAllowableClosedloopError(0, 0, 0);
		armTalon.setNeutralMode(NeutralMode.Brake);
		armTalon.configForwardLimitSwitchSource(
				LimitSwitchSource.FeedbackConnector,
				LimitSwitchNormal.NormallyOpen, 0);
		armTalon.configReverseLimitSwitchSource(
				LimitSwitchSource.FeedbackConnector,
				LimitSwitchNormal.NormallyOpen, 0);
		armTalon.configForwardSoftLimitThreshold(armForwardSoftLimit, 0);
		armTalon.configForwardSoftLimitEnable(true, 0);
		armTalon.configReverseSoftLimitThreshold(armReverseSoftLimit, 0);
		armTalon.configReverseSoftLimitEnable(true, 0);
		armTalon.clearStickyFaults(0);
		armTalon.setIntegralAccumulator(0, 0, 0);
		armTalon.selectProfileSlot(0, 0);
		armTalon.config_kP(0, armP, 0);
		armTalon.config_kI(0, armI, 0);
		armTalon.config_kD(0, armD, 0);
		armTalon.config_kF(0, armF, 0);
		armTalon.config_IntegralZone(0,80,0);
		/* Set relevant frame periods to be at least as fast as periodic rate*/
		armTalon.setStatusFramePeriod(StatusFrameEnhanced.Status_13_Base_PIDF0, 10, 0);
		armTalon.setStatusFramePeriod(StatusFrameEnhanced.Status_10_MotionMagic, 10, 0);
		
		/* set the peak and nominal outputs */
		armTalon.configNominalOutputForward(0, 0);
		armTalon.configNominalOutputReverse(0, 0);
		armTalon.configPeakOutputForward(.5, 0);
		armTalon.configPeakOutputReverse(-.5, 0);
		/* set acceleration and vcruise velocity - see documentation */
		armTalon.configMotionCruiseVelocity(armCruiseVelocity, 0);
		armTalon.configMotionAcceleration(200, 0);
		//armTalon.set(ControlMode.MotionMagic, presetPositions[HIGH]);
		//-goToPreset(HIGH);
		applyBrake();
	}


	// Returns true if the GearArm is in the home position
	public boolean isArmAtHome() {
		return (armTalon.getSensorCollection().isRevLimitSwitchClosed());
	}

	// Goes to the encoder count that is passed
	public void goTo(double height) {
		desiredPosition = height;
		//armTalon.set(ControlMode.Position, height);
		releaseBrake();
		armTalon.set(ControlMode.MotionMagic, height);
	}

	// Go to one of the preset positions
	public void goToPreset(int position) {

		didWeMove = false;
		if ((position >= 0) && (position <= presetPositions.length - 1)) {
			if (Math.abs(presetPositions[position] - desiredPosition) > AbsoluteTolerance ) {
				didWeMove=true;
			}
			goTo(presetPositions[position]);
			lastPreset=position;
		}
	}
	
	// Creep up
	public void incrementHeight() {
		desiredPosition++;
		goTo(desiredPosition);
	}

	// Creep down
	public void decrementHeight() {
		desiredPosition--;
		goTo(desiredPosition);
	}

	// Move arm relative to current postion
	public void adjustHeight( double adjust) {
		desiredPosition+=adjust;
		if (desiredPosition < armReverseSoftLimit) {
			desiredPosition = armReverseSoftLimit;
		}
		if (desiredPosition > armForwardSoftLimit) {
			desiredPosition = armForwardSoftLimit;
		}
		goTo(desiredPosition);
	}	

	// Returns true of the armTalon is at the desired position (done moving)
	public boolean isPositioned() {
		double positionError;
		positionError = Math.abs(desiredPosition - getPosition());
		/*
		 * The following is only valid for relative encoders
		if (desiredPosition == 0 && armTalon.getSensorCollection().isRevLimitSwitchClosed()) 
			armTalon.set(ControlMode.Position, 0);
		}
		 */
		if (positionError <= AbsoluteTolerance) {
			applyBrake();
			return true;
		}
		else {
			return false;
		}
	}

	// Returns the current position error
	public int getPositionError() {
		return armTalon.getClosedLoopError(0);
	}

	// Returns the current position
	public int getPosition() {
		return (int) armTalon.getSelectedSensorPosition(0);
	}

	public boolean ArmMoved() {
		return (didWeMove);
	}


	// Return the position by number
	public int currentPreset() {
		if (isPositioned()) {
			return lastPreset;
		}
		else {
			return -1;
		}
	}
	
	public void applyBrake() {
		//armTalon.set(ControlMode.PercentOutput, 0);
		armBrakeSolenoid.set(false);
		brakeState = true;
	}

	public void releaseBrake() {
		armBrakeSolenoid.set(true);
		brakeState = true;
	}

	// mostly for debugging updates the smart dashboard with position info
	public void periodic() {
		SmartDashboard.putNumber("armTalon Desired Pos", armTalon.getClosedLoopTarget(0));
		SmartDashboard.putNumber("Arm Position", getPosition());
		SmartDashboard.putNumber("Arm Position Error", getPositionError());
		SmartDashboard.putBoolean("Arm is Positioned", isPositioned());
		SmartDashboard.putBoolean("Did Move",ArmMoved());
		SmartDashboard.putNumber("Last Arm Position", lastPreset );
		/*
		 * The following is out for now. Add back if we need to tune the arm postion 
		
		if (lastPreset == HIGH) {
			double joyY = Robot.oi.operatorJoy.getY();

			if (Math.abs(joyY) <.05) {
				joyY = 0;
			}
			double position = presetPositions[HIGH]+joyY*1620/13;

			goTo(position);

		}
		*/
	}

	public void initDefaultCommand() {
		// BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DEFAULT_COMMAND


    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DEFAULT_COMMAND

	}
	public void addTelemetryHeaders() {
		Robot.currentMonitor.registerMonitorDevive(armTalon, ARMMOTOR);
		Robot.telem.addColumn(ARMPOSITION);
		Robot.telem.addColumn(ARMPOSITIONERROR);
		Robot.telem.addColumn(ARMPRESETPOSITION);
		Robot.telem.addColumn(ARMBRAKESTATE);
	}

	public void writeTelemetyValues() {
		Robot.telem.saveDouble(ARMPOSITION,getPosition());
		Robot.telem.saveDouble(ARMPOSITIONERROR, getPositionError());
		Robot.telem.saveInteger(ARMPRESETPOSITION, lastPreset);
		Robot.telem.saveBoolean(ARMBRAKESTATE, brakeState);
	}
	
	public void loadConfig(Config config) {
		presetPositions[FLOOR] = config.getInt("ArmPosition"+ArmPositionLabels[FLOOR],Defaults.ARMPOSITION0);
		presetPositions[LOW] = config.getInt("ArmPosition"+ArmPositionLabels[LOW], Defaults.ARMPOSITION1);
		presetPositions[MEDIUM] = config.getInt("ArmPosition"+ArmPositionLabels[MEDIUM], Defaults.ARMPOSITION2);
		presetPositions[SCALE] = config.getInt("ArmPosition"+ArmPositionLabels[SCALE],Defaults.ARMPOSITION3);
		presetPositions[HIGH] = config.getInt("ArmPosition"+ArmPositionLabels[HIGH],Defaults.ARMPOSITION4);
		armReverseSoftLimit = config.getInt("ArmReverseSoftLimit", Defaults.REVERSESOFTLIMIT);
//		armTalon.configReverseSoftLimitThreshold(armReverseSoftLimit, 0);
		armForwardSoftLimit = config.getInt("ArmForwardSoftLimit", Defaults.FORWARDSOFTLIMIT);
//		armTalon.configForwardSoftLimitThreshold(armForwardSoftLimit, 0);
		armP = config.getDouble("ArmP",Defaults.ARM_P);
		armI = config.getDouble("ArmI",Defaults.ARM_I);
		armD = config.getDouble("ArmD",Defaults.ARM_D);
		armF = config.getDouble("ArmF",Defaults.ARM_F);
		armCruiseVelocity = config.getInt("ArmCruiseVelocity", Defaults.ARMCRUISEVELOCITY);
		/*
		armTalon.config_kP(0, armP, 0);
		armTalon.config_kI(0, armI, 0);
		armTalon.config_kD(0, armD, 0);
*/	}

}

