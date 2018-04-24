package org.usfirst.frc2016.robot2018;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import org.usfirst.frc2016.robot2018.AutoPilot.*;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class AutoPilotRobot {
	AutoPilot autoPilot;
	String lastStatus;
	Timer driveUpdateTimer;
	int nDriveUpdateMs = 10;
	private String moveFilePath = "/c/";
	
	
	// sequence base names defined in AutoPilot.txt
    public static String selectSwitchFront = "* - Switch Front";
    public static String selectSwitchFrontBack = "** - Switch Front-Back";
	
	
	public AutoPilotRobot() {
		autoPilot = new AutoPilot();
		autoPilot.addMethods(this);
		
		loadFile();
	}
	
	public void loadFile() {
		autoPilot.loadFile(moveFilePath + "AutoPilot.txt");
		autoPilot.setTracing(true);
		SmartDashboard.putString("AutoPilotStatus", autoPilot.getStatus());
	}

	public enum ArmState {
		OPEN, CLOSE
	}

	public enum IntakeState {
		IN, OUT, STOP
	}

	public enum LiftPosition {
		FLOOR, LOW, MEDIUM, SCALE, HIGH
	}
	
	public void initialize(String selectName) {
		// get the game data
    	String rawGameData = DriverStation.getInstance().getGameSpecificMessage();
    	
    	// replace sequence of 'stars' with game data
    	String sequenceName = selectName;
    	sequenceName = sequenceName.replace("**", rawGameData.substring(0,2));
    	sequenceName = sequenceName.replace("*", rawGameData.substring(0,1));
    	
		//Robot.driveTrainSRX.resetEncoders();
		
		Double[] distances = getDriveDistances();
		
		Robot.driveTrainSRX.goToDistance(
				distances[AutoPilot.MOTOR_LEFT],
				distances[AutoPilot.MOTOR_RIGHT]);
		
		//autoPilot.setTracing(false);
		autoPilot.startSequence(sequenceName, distances);
		startTimerTask();   	
	}

	// Called repeatedly when this Command is scheduled to run
	public void execute() {

		Robot.driveTrainSRX.pingDifferentialDrive();
		SmartDashboard.putString("AutoPilotStatus", autoPilot.getStatus());

//		if (!autoPilot.isTracing()) {
//			String status = autoPilot.getStatus();
//			if (!Objects.equals(lastStatus, status)) {
//				lastStatus = status;
//				System.out.println(lastStatus);
//			}
//		}

	}

	// Make this return true when this Command no longer needs to run execute()
	public boolean isFinished() {
		return autoPilot.isFinished();
	}

	// Called once after isFinished returns true
	public void end() {
		autoPilot.end();
		shutdown();
	}

	// Called when another command which requires one or more of the same
	// subsystems is scheduled to run
	// --@Override
	public void interrupted() {
		autoPilot.interrupted();
		shutdown();
	}

	// called via reflection to get the current distance from the drive train
	@AutoPilotMethod(argHint = "")
	public Double[] getDriveDistances() {
		Double[] distances = new Double[AutoPilot.MOTOR_MAX];
		distances[AutoPilot.MOTOR_LEFT] = 0.0;
		distances[AutoPilot.MOTOR_RIGHT] = 0.0;
	
		distances[AutoPilot.MOTOR_LEFT] = Robot.driveTrainSRX.getLeftDistance();
		distances[AutoPilot.MOTOR_RIGHT] = Robot.driveTrainSRX.getRightDistance();
		
		return distances;
	}

	public void shutdown() {
		killTimerTask();
		Robot.cubePickup.autoEnd();
		Robot.driveTrainSRX.driveStop();
	}

	private void startTimerTask() {
		killTimerTask();
		driveUpdateTimer = new Timer();
		driveUpdateTimer.scheduleAtFixedRate(new DriveTimerTask(),
				nDriveUpdateMs, nDriveUpdateMs);
	}

	private void killTimerTask() {
		if (null != driveUpdateTimer) {
			driveUpdateTimer.cancel();
			driveUpdateTimer.purge();
			driveUpdateTimer = null;
		}
	}

	class DriveTimerTask extends TimerTask {
		@Override
		public void run() {
			if (autoPilot.isFinished()) {
				killTimerTask();
			} else {
				double[] driveDistances = autoPilot.execute();
				if (null != driveDistances) {
					
					Robot.driveTrainSRX.goToDistance(
							driveDistances[AutoPilot.MOTOR_LEFT],
							driveDistances[AutoPilot.MOTOR_RIGHT]);
				}
			}
		}
	}

	@AutoPilotMethod(argHint = "( Open|Close ) : moves the intake arms")
	public void intakeArms(ArmState newArmState) {

		switch (newArmState) {

		case OPEN:
			Robot.cubePickup.openArms();
			break;

		default:
		case CLOSE:
			Robot.cubePickup.closeArms();
			break;

		}
	}

	@AutoPilotMethod(argHint = "( In|Out|Stop ) : sets the intake wheels")
	public void intakeWheels(IntakeState newIntakeState) {

		switch (newIntakeState) {

		case IN:
			Robot.cubePickup.acquireCube();
			break;

		case OUT:
			Robot.cubePickup.autoEjectCube();
			break;

		default:
		case STOP:
			Robot.cubePickup.autoEnd();
			break;
		}
	}
	
	@AutoPilotMethod(argHint = "( In|Out|Stop ) : sets the lift arm position")
	public void liftTarget(LiftPosition newTarget) {
		Robot.arm.goToPreset(newTarget.ordinal());
	}
}
