package org.usfirst.frc2016.robot2018.commands;

import org.usfirst.frc2016.robot2018.Robot;
import org.usfirst.frc2016.robot2018.commands.AutoPilot.AutoPilotMethod;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class AutoPilotCommand extends Command {

	private String moveFilePath = "/c/";
	/** Default: /c folder. */
	// --private String moveFilePath = "src\\testDrive\\";

	AutoPilot autoPilot;
	String lastStatus;
	Timer driveUpdateTimer;
	int nDriveUpdateMs = 10;

	public enum ArmState {
		OPEN, CLOSE
	}

	public enum IntakeState {
		IN, OUT, STOP
	}

	public enum LiftPosition {
		FLOOR, LOW, MEDIUM, SCALE, HIGH
	}

	public AutoPilotCommand() {
		autoPilot = new AutoPilot(this);
		driveUpdateTimer = new Timer("DriveUpdateThread", true);
		// autoPilot.listCommands();

		autoPilot.loadFile(moveFilePath + "AutoPilot.txt");
		SmartDashboard.putString("AutoPilotStatus", autoPilot.getStatus());

		if (!autoPilot.isTracing()) {
			lastStatus = autoPilot.getStatus();
			System.out.println(lastStatus);
		}
	}

	// Called just before this Command runs the first time
	@Override
	protected void initialize() {
		Robot.driveTrainSRX.resetEncoders();
		autoPilot.setTracing(false);
		autoPilot.startSequence("2Cube-L", getDriveDistances());
		startTimerTask();
	}

	// Called repeatedly when this Command is scheduled to run
	@Override
	public void execute() {

		Robot.driveTrainSRX.pingDifferentialDrive();
		SmartDashboard.putString("AutoPilotStatus", autoPilot.getStatus());

		if (!autoPilot.isTracing()) {
			String status = autoPilot.getStatus();
			if (!Objects.equals(lastStatus, status)) {
				lastStatus = status;
				System.out.println(lastStatus);
			}
		}

	}

	// Make this return true when this Command no longer needs to run execute()
	// --@Override
	protected boolean isFinished() {
		return autoPilot.isFinished();
	}

	// Called once after isFinished returns true
	// --@Override
	protected void end() {
		autoPilot.setTracing(true);
		autoPilot.end();
		shutdown();
	}

	// Called when another command which requires one or more of the same
	// subsystems is scheduled to run
	// --@Override
	protected void interrupted() {
		autoPilot.interrupted();
		shutdown();
	}

	// called via reflection to get the current distance from the drive train
	private Double[] getDriveDistances() {
		Double[] distances = new Double[AutoPilot.MOTOR_MAX];
		distances[AutoPilot.MOTOR_LEFT] = 0.0;
		distances[AutoPilot.MOTOR_RIGHT] = 0.0;
		
		Robot.driveTrainSRX.resetEncoders();
		distances[AutoPilot.MOTOR_LEFT] = Robot.driveTrainSRX.getLeftDistance();
		distances[AutoPilot.MOTOR_RIGHT] = Robot.driveTrainSRX.getRightDistance();
		return distances;
	}

	private void shutdown() {
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

	@AutoPilotMethod(argHint = "( Floor|Low|Medium|Scale|High ) : target for the lift arm")
	public void liftTarget(LiftPosition newTarget) {
		Robot.arm.goToPreset(newTarget.ordinal());
	}
}
