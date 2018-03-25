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
import edu.wpi.first.wpilibj.Timer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import org.usfirst.frc2016.robot2018.Robot;

/**
 *
 */
public class DriveUsingFile extends Command {
	private String moveFileName = "centermove";    /** Default: centermove_L.csv */
	
	private String moveFilePath = "/c";				/** Default: /c folder. */
	//-- private String filePath= "..\\robot2018\\trunk\\";
	private final String csvSplitBy = ",";
	private final String leftName = "L";
	private final String rightName = "R";
	
	public Double secondsPerExecute = .02;
	
	public Double velocityLimit = 20.0;
	public Double accelLimit = 10.0;
	public Double distanceLimit = 150.0;
	
	//** public Timer elapsedTime;
	public long startNanoTime; // nanoseconds
	public double moveStartTime;  // seconds

	
	private ArrayList<AutoSegment> moveList;
	private Integer moveListIndex;
	private Integer executeCount;
	private MotorPosition posLeft;
	private MotorPosition posRight;

	private Boolean failed = false;
	private Boolean finished = false;
	
	private enum AutoCmd { NotFound, Left, Right, ResetPos, Close, Open, PullIn, Eject, StopIntake, ArmFloor, ArmHigh, Stop }
	
	public enum ProfileState { Idle, Start, Coast, Stop, SpdUp, SpdDn }
	
	// BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=VARIABLE_DECLARATIONS

	// END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=VARIABLE_DECLARATIONS

	// BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTRUCTOR
	public DriveUsingFile() {
        requires(Robot.driveTrainSRX);

		// END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTRUCTOR
		// BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=VARIABLE_SETTING

		// END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=VARIABLE_SETTING
		// BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=REQUIRES
        requires(Robot.driveTrainSRX);

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=REQUIRES
		posLeft = new MotorPosition(leftName);
		posRight = new MotorPosition(rightName);
		moveList = new ArrayList<AutoSegment>();
	}

	// Called just before this Command runs the first time
	@Override
	protected void initialize() {
		String filePath = moveFilePath + "/" + moveFileName + "_" + Character.toString(Robot.gameData) + ".csv" ;
		System.out.println("MoveUsingFile(): INFO: Loading points from file [" + filePath + "]");
		System.out.println("Working Directory1 = " + System.getProperty("user.dir"));
		finished = false;
		failed = false;
		
		try{
			posLeft = new MotorPosition(leftName);
			posRight = new MotorPosition(rightName);
			moveList = new ArrayList<AutoSegment>();
			moveList.add(new AutoSegment(-1, 0.0, AutoCmd.ResetPos, "Initialize", posLeft, posRight));
			
			double moveResetTime = 0;
			String fileLine;
			Integer lineNumer = 0;
			BufferedReader in = new BufferedReader(new FileReader(filePath));
			
			while ((fileLine = in.readLine()) != null) {
				lineNumer++;
				fileLine = fileLine.trim();
				if(fileLine.length() == 0 || fileLine.charAt(0) == '#')
					continue;
				
				String[] fields = fileLine.split(csvSplitBy);

				if (fields.length < 2){
					System.out.println("DriveUsingFile: ****ERROR Line " + lineNumer.toString() + " is not a comment (#) but does not have a pipe ("+csvSplitBy+") delimiter!");
					failed=true;
				}
				
				double time = Double.parseDouble(fields[0].trim());
				
				String cmdName = fields[1].trim(); 
				AutoCmd cmd = AutoCmd.NotFound;
				for(AutoCmd cmdFind : AutoCmd.values())	{
					if (Objects.equals(cmdName,cmdFind.name())) {
						cmd = cmdFind;
						break;
					}
				}
				
				if(cmd==AutoCmd.NotFound)
				{
					System.out.println("DriveUsingFile: ****ERROR Line " + lineNumer.toString() + " command '" + fields[1] + "' not found");
					failed=true;					
				}
				
				// { NotFound, Left, Right, ResetPos, Close, Open, PullIn, Eject, StopIntake, ArmFloor, ArmHigh, Stop }
				switch(cmd)
				{
				case Left:
				case Right:
					if (fields.length < 5) {
						System.out.println("DriveUsingFile: ****ERROR Line " + lineNumer.toString() + " Left/Right is missing accel,targetVel,targetPos fields!");
						failed=true;
					}
					double a = Double.parseDouble(fields[2].trim());
					double v = Double.parseDouble(fields[3].trim());
					double d = Double.parseDouble(fields[4].trim());
					if (cmd == AutoCmd.Left) {
						posLeft.addTarget(time-moveResetTime, a, v, d);
					}
					else {
						posRight.addTarget(time-moveResetTime, a, v, d);
					}
					
					moveList.add(new AutoSegment(lineNumer, time, cmd, fileLine));
					break;
					
				case ResetPos:
					moveResetTime = time;
					posLeft = new MotorPosition(leftName);
					posRight = new MotorPosition(rightName);
					moveList.add(new AutoSegment(lineNumer, time, cmd, fileLine, posLeft, posRight));
					break;
										
				default:
					moveList.add(new AutoSegment(lineNumer, time, cmd, fileLine));
					break;
				}

			}
			in.close();
			
		} catch (IOException e) {
			System.out.println("DriveUsingFile(): ****ERROR: Failed to load the file " + this.moveFileName  + 
					"   Exception:" + e + "  Reason:" + e.getMessage() );
			failed = true;
		}


		executeCount = 0;
		moveListIndex = 0;
		startNanoTime = System.nanoTime() - 1;
		//** elapsedTime = new Timer();
		//** elapsedTime.start();
		
		processCommands(0.0);
	}

	// Called repeatedly when this Command is scheduled to run
	@Override
	protected void execute() {
		executeCount++;
		
		Double tickNow = executeCount * secondsPerExecute;
		//** Double timerNow = elapsedTime.get();
		Double timerNow = (double)(System.nanoTime() - startNanoTime) / 1e9;
		
		processCommands(timerNow);
		updateDriveTrain(timerNow);
		
		System.out.printf("%5.2f (%.2f) %s %s\n", timerNow, tickNow, posLeft.toString(), posRight.toString());
	}

	// Make this return true when this Command no longer needs to run execute()
	@Override
	protected boolean isFinished() {
		return (failed || finished);
	}

	// Called once after isFinished returns true
	@Override
	protected void end() {
		shutdown();
	}

	// Called when another command which requires one or more of the same
	// subsystems is scheduled to run
	@Override
	protected void interrupted() {
		shutdown();
	}
	
	private void shutdown()
	{
		finished = true;
		failed = true;
		Robot.cubePickup.autoEnd();
		Robot.driveTrainSRX.driveStop();
	}

	private void updateDriveTrain(double time)
	{
		double moveTime = time - moveStartTime;
		
		double leftDistance = posLeft.updatePosition(moveTime);
		double rightDistance = posRight.updatePosition(moveTime);
		if (!failed)
		{
			Robot.driveTrainSRX.goToDistance(rightDistance, leftDistance);
			Robot.driveTrainSRX.goToDistance(leftDistance, rightDistance);
		}
		Robot.driveTrainSRX.pingDifferentialDrive();
	}
	
	private void executeResetPos(AutoSegment autoSeg, double timeNow)
	{
		moveStartTime = timeNow;
		posLeft = autoSeg.posLeft;
		posRight = autoSeg.posRight;
	
		//-- posLeft.start(100);
		//-- posRight.start(100);
		posLeft.start(Robot.driveTrainSRX.getLeftDistance());
		posRight.start(Robot.driveTrainSRX.getRightDistance());
	}

	private void processCommands(double timeNow) {
		
		while(moveListIndex < moveList.size()) {
			AutoSegment autoSeg = moveList.get(moveListIndex);
			if (autoSeg.time > timeNow) {
				break;
			}
			moveListIndex++;
			System.out.printf(">> %.2f %2d: %s\n", timeNow, autoSeg.lineNumber, autoSeg.fileLine);
			
			// NotFound, Left, Right, ResetPos, Close, Open, PullIn, Eject, StopIntake, ArmFloor, ArmHigh
			switch(autoSeg.command)
			{
			default:
				System.out.println("processCommands: " + autoSeg.command.name() + " not handled");
				break;
				
			case Left:
				break;
				
			case Right:
				break;	
				
			case ResetPos:
				executeResetPos(autoSeg, timeNow);
				break;
				
			case Close:
				Robot.cubePickup.closeArms();
				break;
				
			case Open:
				Robot.cubePickup.openArms();
				break;
				
			case PullIn:
				Robot.cubePickup.acquireCube();
				break;
				
			case Eject:
				Robot.cubePickup.autoEjectCube();
				break;
				
			case StopIntake:
				Robot.cubePickup.autoEnd();
				break;
				
			case ArmFloor:
				Robot.arm.goToPreset(Robot.arm.FLOOR);
				break;
				
			case ArmHigh:
				Robot.arm.goToPreset(Robot.arm.HIGH);
				break;
				
			case Stop:
				moveListIndex = moveList.size();
				break;
			}
			
		}
		finished = moveListIndex >= moveList.size();
	}


	// helper class to contain the parsed command file information
	public class AutoSegment {
		public int lineNumber;
		public String fileLine;
		public double time;
		public AutoCmd command;
		public MotorPosition posLeft; 
		public MotorPosition posRight; 
		
		public AutoSegment(int lineCount, double t, AutoCmd cmd, String lineData, MotorPosition left, MotorPosition right)
		{
			lineNumber = lineCount;
			fileLine = lineData;
			time = t; 
			command = cmd;
			posLeft = left;
			posRight = right;
		}
		
		public AutoSegment(int lineCount, double t, AutoCmd cmd, String lineData)
		{
			lineNumber = lineCount;
			fileLine = lineData;
			time = t; 
			command = cmd;
		}
	}

	// Move profiles for a single motor
	public class MotorPosition
	{
		//private final double minMoveTime = 0.001;
		private String name;
		private ArrayList<MoveSegment> moveSegmentList;
		private MovePoint ptLast;
		private double offsetPos;
		private int moveSegmentIndex;
		
		public MotorPosition(String motorName)
		{
			name = motorName;
			moveSegmentList = new ArrayList<MoveSegment>();
			ptLast = new MovePoint(0,0,0);
			start(0);
		}

		public double getDistance()
		{
			return offsetPos + ptLast.pos;
		}
		
		public double updatePosition(double moveTime) {
			MoveSegment moveSeq = null;
			
			// advance segment if needed
			while(moveSegmentIndex < moveSegmentList.size()) {
				moveSeq = moveSegmentList.get(moveSegmentIndex);
				if (moveSeq.ptEnd.time > moveTime) {
					break;
				}
				moveSegmentIndex++;
			}
			
			// calculate location within segment
			if (moveSeq != null) {
				MovePoint pt = moveSeq.getPoint(moveTime);
				ptLast.set(pt);				
			}
			
			return getDistance();
		}
		
		public String toString()
		{
			double accel = 0;
			if (moveSegmentIndex < moveSegmentList.size()) {
				accel = moveSegmentList.get(moveSegmentIndex).accel;
			}
			return String.format("    %s:%2d %5.1f %5.1f %7.1f %7.1f", name, moveSegmentIndex, accel, ptLast.vel, ptLast.pos, getDistance());
		}
		
		public void start(double offset) {
			offsetPos = offset;
			moveSegmentIndex = 0;
			if (moveSegmentList.size()==0) {
				moveSegmentList.add(new MoveSegment());
			}
			MoveSegment ms = moveSegmentList.get(moveSegmentIndex);
			ptLast.set(ms.ptStart);
		}
		
		public void addTarget(double startTime, double maxAccel, double maxVel, double targetPos)
		{
			// initialize start/end points with default values
			MovePoint ptTargetStart = new MovePoint(startTime, 0, 0);
			
			// look for a move containing the start time
			int startIndex = 0;
			for (startIndex=0; startIndex < moveSegmentList.size(); startIndex++) {
				if (moveSegmentList.get(startIndex).ptEnd.time > startTime) {
					break;
				}
			}
			
			if (startIndex < moveSegmentList.size()) {
				// new target is changing the plan
				// update the old segment to end at the new start time
				MoveSegment msOld = moveSegmentList.get(startIndex);
				
				// get the velocity and position for the given time
				ptTargetStart.set(msOld.getPoint(startTime));
				
				// set the existing segment to end at the new start point
				msOld.ptEnd.set(ptTargetStart);
				
				// remove the remaining segments, usually the coast/brake from a previous target
				startIndex++;
				for(int trimIndex = moveSegmentList.size() - 1; trimIndex >= startIndex; trimIndex--) {
					moveSegmentList.remove(trimIndex);
				}
			}
			
			// calc the move parameters
			double deltaPos = targetPos - ptTargetStart.pos;
			double moveLen = Math.abs(deltaPos);
			double dir = deltaPos > 0 ? 1 : -1;
			double deltaVel = dir * maxVel - ptTargetStart.vel;
			
			double accelTime = Math.abs(deltaVel / maxAccel);
			double accelLen = 0.5 * maxAccel * accelTime * accelTime;
			
			double brakeTime = Math.abs(maxVel / maxAccel);
			double brakeLen = 0.5 * maxAccel * brakeTime * brakeTime;
			
			double coastLen = moveLen - accelLen - brakeLen;
			double coastTime = coastLen / maxVel;

			if (0 == deltaPos)
			{
				// zero length move
				return;
			}
			
			if (coastLen > 0) {
				// long move: accel, coast speed, brake.
				MoveSegment msAccel = new MoveSegment();
				msAccel.accel = deltaVel > 0 ? maxAccel : -maxAccel;
				msAccel.ptStart.set(ptTargetStart);
				msAccel.ptEnd.time = msAccel.ptStart.time + accelTime;
				msAccel.ptEnd.vel = dir * maxVel;
				msAccel.ptEnd.pos = msAccel.ptStart.pos + dir * accelLen;
				moveSegmentList.add(msAccel);
				
				MoveSegment msCoast = new MoveSegment();
				msCoast.accel = 0;
				msCoast.ptStart.set(msAccel.ptEnd);
				msCoast.ptEnd.time = msCoast.ptStart.time + coastTime;
				msCoast.ptEnd.vel = dir * maxVel;
				msCoast.ptEnd.pos = msCoast.ptStart.pos + dir * coastLen;
				moveSegmentList.add(msCoast);				
				
				MoveSegment msBrake = new MoveSegment();
				msBrake.accel = dir * -maxAccel;
				msBrake.ptStart.set(msCoast.ptEnd);
				msBrake.ptEnd.time = msBrake.ptStart.time + brakeTime;
				msBrake.ptEnd.vel = 0;
				msBrake.ptEnd.pos = targetPos;
				moveSegmentList.add(msBrake);
				
				return;
			}
			
			if (ptTargetStart.vel == 0) {
				// short move from stop, equal accel/brake time & distance
				accelTime = Math.sqrt(2 * moveLen / maxAccel);
				accelLen = moveLen / 2;
				
				MoveSegment msAccel = new MoveSegment();
				msAccel.accel = dir * maxAccel;
				msAccel.ptStart.set(ptTargetStart);
				msAccel.ptEnd.time = msAccel.ptStart.time + accelTime;
				msAccel.ptEnd.vel = msAccel.ptStart.vel + dir * maxAccel * accelTime;
				msAccel.ptEnd.pos = msAccel.ptStart.pos + dir * accelLen;
				moveSegmentList.add(msAccel);	
				
				MoveSegment msBrake = new MoveSegment();
				msBrake.accel = dir * -maxAccel;
				msBrake.ptStart.set(msAccel.ptEnd);
				msBrake.ptEnd.time = msBrake.ptStart.time + accelTime;
				msBrake.ptEnd.vel = 0;
				msBrake.ptEnd.pos = targetPos;
				moveSegmentList.add(msBrake);
				
				return;				
			}
			
			
			// short or unknown movement, just 'jump' there
			MoveSegment msJump = new MoveSegment();
			msJump.accel = 0;
			msJump.ptStart.set(ptTargetStart);
			msJump.ptStart.pos = targetPos;
			msJump.ptEnd.time = ptTargetStart.time;
			msJump.ptEnd.vel = ptTargetStart.vel;
			msJump.ptEnd.pos = targetPos;
			moveSegmentList.add(msJump);
			return;
		}
	}

	// Movement between two states
	public class MoveSegment {
		public double accel;
		public MovePoint ptStart;
		public MovePoint ptEnd;
		public MovePoint ptLast;
		
		public MoveSegment() {
			ptStart = new MovePoint(0,0,0);
			ptEnd = new MovePoint(0,0,0);
			ptLast = new MovePoint(0,0,0);
		}
		
		public MovePoint getPoint(double time)
		{
			if (time <= ptStart.time) {
				ptLast.set(ptStart);
			}
			else if (time >= ptEnd.time) {
				ptLast.set(ptEnd);
			}
			else {
				double dt = time - ptStart.time;
				ptLast.time = time;
				ptLast.vel = ptStart.vel + accel * dt;
				ptLast.pos = ptStart.pos + ptStart.vel * dt + 0.5 * accel * dt * dt;
			}
			return ptLast;
		}
	}
		
	// Movement state at given time
	public class MovePoint {
		public double time;
		public double vel;
		public double pos;
		
		public MovePoint(double t, double v, double p) {
			time = t;
			vel = v;
			pos = p;
		}
		
		public void set(MovePoint pt)
		{
			time = pt.time;
			vel = pt.vel;
			pos = pt.pos;
		}
	}
}