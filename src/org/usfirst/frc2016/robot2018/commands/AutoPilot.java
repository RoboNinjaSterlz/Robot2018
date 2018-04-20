package org.usfirst.frc2016.robot2018.commands;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.lang.System;
import java.util.regex.*;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.*;

public class AutoPilot {

	public static final int MOTOR_LEFT = 0;
	public static final int MOTOR_RIGHT = 1;
	public static final int MOTOR_MAX = 2;

	static final int HIST_SIZE = 50;

	private Object parentObject;
	private Method parentMethodGetDistances;

	private final String leftDriveName = "L";
	private final String rightDriveName = "R";
	private final String defaultSequenceName = "****DefaultSequenceName****";
	private final double minMoveValue = .05;

	private long nanoTimeStart; // nanoseconds
	private long nanoTimeLast;
	private int[] updateHistogram = new int[HIST_SIZE];

	private Map<String, AutoPilotNamedSequence> mapSequences;
	private String currentSequenceName = null;

	private ArrayList<AutoPilotAction> actionList;
	private Integer actionListIndex;
	private AutoPilotAction currentAction;

	private MotorPosition posLeft;
	private MotorPosition posRight;
	private Double[] moveOffset = new Double[MOTOR_MAX];

	private double cruiseVel = 100;
	private double accel = 200;
	private double tankWidth = 26;
	private double turnRadius = 30;
	private double moveSequenceTime = 0;
	private double actionSequenceTime = 0;

	private String status;
	private Boolean tracing = true;
	private Boolean finished = false;

	public enum MoveEnd {
		GO, STOP
	}

	public enum MoveDirection {
		FORWARD, BACKWARD
	}

	public AutoPilot(Object parent) {
		parentObject = parent;
	}

	public void setTracing(boolean trace) {
		this.tracing = trace;
	}

	public boolean isTracing() {
		return tracing;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String text) {
		status = text;
		traceMessage(text);
	}

	public void setStatus(String fmt, Object... objects) {
		setStatus(String.format(fmt, objects));
	}

	public double getElapsedSeconds() {
		return (double) (System.nanoTime() - nanoTimeStart) / 1e9;
	}

	// Called repeatedly when the parent command is running
	public double[] execute() {

		double[] distances = null;
		try {
			long nanoTimeCurrent = System.nanoTime();
			Double moveSequenceTime = (double) (nanoTimeCurrent - nanoTimeStart) / 1e9;

			distances = updateDistances(moveSequenceTime);

			processCommands(moveSequenceTime);

			Double lastIntervalMs = (double) (nanoTimeCurrent - nanoTimeLast) / 1e6;

			int histIndex = (int)Math.round(lastIntervalMs);
			if (histIndex >= HIST_SIZE)
				histIndex = HIST_SIZE - 1;
			if (histIndex < 0)
				histIndex = 0;
			updateHistogram[histIndex]++;
			
			nanoTimeLast = nanoTimeCurrent;

//			if (tracing) {
//				traceMessage("%7.3f (+%4.1f) %s %s", moveSequenceTime, lastIntervalMs, posLeft.toString(), posRight.toString());
//			}

		} catch (AutoPilotException e) {
			stop();
			setStatus(e.getMessage());
			distances = null;
		}

		return distances;
	}

	public double[] updateDistances(double moveSequenceTime) {

		double[] distances = new double[MOTOR_MAX];

		distances[MOTOR_LEFT] = posLeft.updatePosition(moveSequenceTime) + moveOffset[MOTOR_LEFT];
		distances[MOTOR_RIGHT] = posRight.updatePosition(moveSequenceTime) + moveOffset[MOTOR_RIGHT];

		return distances;
	}

	public double getLeftDistance() {
		return posLeft.getDistance() + moveOffset[MOTOR_LEFT];
	}

	public double getRightDistance() {
		return posRight.getDistance() + moveOffset[MOTOR_RIGHT];
	}

	// Make this return true when parent Command no longer needs to run
	// execute()
	public boolean isFinished() {
		return finished;
	}

	// Called once after isFinished returns true
	public void end() {
		shutdown();
	}

	// Called when another command which requires one or more of the same
	// subsystems is scheduled to run
	public void interrupted() {
		shutdown();
	}

	private void shutdown() {
		finished = true;
		if (isTracing()) {
			int sum = 0;
			int total = 0;
			traceMessage("Update interval histogram");
			traceMessage("  UpdateMs,Count");
			for (int i = 0; i < HIST_SIZE; i++) {
				int count = updateHistogram[i];
				if (count > 0) {
					traceMessage("   %d, %d", i, count);
					sum += i * count;
					total += count;
				}
			}
			if (total > 0) {
				traceMessage(" Average: %.1f", (double)sum/(double)total);
			}
		}
	}

	public void traceMessage(String msg) {
		if (tracing) {
			System.out.println(msg);
		}
	}

	public void traceMessage(String fmt, Object... objects) {
		if (tracing) {
			traceMessage(String.format(fmt, objects));
		}
	}

	@AutoPilotMethod(argHint = "( ) : turn on tracing")
	public void enableTracing() {
		tracing = true;
	}

	@SuppressWarnings("resource")
	public void loadFile(String filePath) {

		tracing = true;
		setStatus("AutoPilot loading '%s'", filePath);
		tracing = false;
		// traceMessage("Working Directory1 = " +
		// System.getProperty("user.dir"));

		finished = false;
		nanoTimeStart = System.nanoTime();
		nanoTimeLast = nanoTimeStart;
		
		cruiseVel = 100;
		accel = 200;
		tankWidth = 26;
		turnRadius = 30;
		Integer lineNumber = 0;
		BufferedReader inputReader = null;
		mapSequences = new HashMap<>();

		try {
			// Object[] startSequenceParams = new Object[] { new
			// Double[DRIVE_MOTOR_MAX] };
			// thisMethodStartSequence = findPrivateMethod(this,
			// "startSequence", null);

			parentMethodGetDistances = findPrivateMethod(parentObject, "getDriveDistances", null);
			if (null == parentMethodGetDistances)
				throw new AutoPilotException("Parent object '%s' missing method 'Double [] getDriveDistances()'",
						parentObject.getClass().getName());

			initializeSequence(defaultSequenceName);

			String fileLine;
			inputReader = new BufferedReader(new FileReader(filePath));

			// regex pattern to obtain method name and argument list
			Pattern funcPattern = Pattern.compile("([a-zA-Z0-9]+)\\(([ ,.'a-zA-Z0-9\\-\\\"]*)\\)");

			while (!finished && (fileLine = inputReader.readLine()) != null) {
				lineNumber++;

				// strip comments
				int slashes = fileLine.indexOf("//");
				if (slashes >= 0)
					fileLine = fileLine.substring(0, slashes);
				fileLine = fileLine.trim();

				// ignore full comment and blank lines
				if (fileLine.length() == 0)
					continue;

				// split command and string of all args
				Matcher funcMatcher = funcPattern.matcher(fileLine);
				if (!funcMatcher.matches() || funcMatcher.groupCount() != 2) {
					throw new AutoPilotException("missing command name or parens");
				}

				String cmdName = funcMatcher.group(1);
				String[] stringArgs = funcMatcher.group(2).split(",");

				// trim all the arguments
				for (int argIndex = 0; argIndex < stringArgs.length; argIndex++) {
					stringArgs[argIndex] = stringArgs[argIndex].trim();
				}

				String fileLineTrace = String.format("%3d: %s( %s )", lineNumber, cmdName,
						String.join(", ", stringArgs));

				Method actionMethod = findMethod(this, cmdName);
				if (null != actionMethod) {

					// methods in this class are executed for path planning
					currentAction = new AutoPilotAction(fileLineTrace, moveSequenceTime);
					traceMessage(">> %.2f %s", currentAction.getScheduledTime(), currentAction.getSourceText());

					Object[] objectArgs = methodConvertArgs(actionMethod, stringArgs);
					invokeMethod(this, actionMethod, objectArgs);

				} else {

					actionMethod = findMethod(parentObject, cmdName);
					if (null == actionMethod) {
						throw new AutoPilotException("command '%s' not found", cmdName);
					}

					// methods from other classes are just added to the schedule
					currentAction = new AutoPilotAction(fileLineTrace, actionSequenceTime);
					traceMessage(">> %.2f %s", currentAction.getScheduledTime(), currentAction.getSourceText());

					Object[] objectArgs = methodConvertArgs(actionMethod, stringArgs);
					currentAction.setMethod(parentObject, actionMethod, objectArgs);

					actionSequenceTime = moveSequenceTime;
				}

				actionListInsert(currentAction);
				currentAction = null;
			}

			newSequence(null);

			inputReader.close();
			double loadTime = (System.nanoTime() - nanoTimeStart) / 1e6;
			setStatus("%d sequences  Load time %.1f ms", mapSequences.size(), loadTime);
			if (tracing) {
				for (Map.Entry<String, AutoPilotNamedSequence> entry : mapSequences.entrySet()) {
					traceMessage("   %4.1f: %s", entry.getValue().getExecTime(), entry.getKey());
				}
			}

		} catch (Exception e) {
			setStatus("Error Line %d: %s", lineNumber, e.getMessage());
			finished = true;
		}

		if (null != inputReader) {
			try {
				inputReader.close();
			} catch (IOException e) {
				traceMessage("Error closeing file " + e.getMessage());
			}
		}

		actionListIndex = 0;
		moveSequenceTime = 0;
		actionSequenceTime = 0;
		nanoTimeStart = System.nanoTime();
		nanoTimeLast = nanoTimeStart;
		finished = false;
	}

	private void actionListInsert(AutoPilotAction action) {
		// keep the action list sorted in time order
		if (null != action) {

			int insertIndex = actionList.size() - 1;
			for (; insertIndex >= 0; insertIndex--) {
				if (actionList.get(insertIndex).getScheduledTime() <= action.getScheduledTime()) {
					break;
				}
			}
			insertIndex++;
			actionList.add(insertIndex, action);
		}
	}

	private Object[] methodConvertArgs(Method method, String[] stringArgs) throws AutoPilotException {
		Class<?>[] paramList = method.getParameterTypes();
		Object[] objectArgs = new Object[paramList.length];

		if (stringArgs.length < paramList.length) {
			throw new AutoPilotException("missing arguments");
		}

		// convert parameters to the required types
		for (int argIndex = 0; argIndex < paramList.length; argIndex++) {

			String argText = stringArgs[argIndex];
			Class<?> param = paramList[argIndex];
			String paramName = param.getName();

			if (Objects.equals(paramName, "double")) {

				objectArgs[argIndex] = Double.parseDouble(argText);

			} else if (Objects.equals(paramName, "integer")) {

				objectArgs[argIndex] = Integer.parseInt(argText);

			} else if (Objects.equals(paramName, "java.lang.String")) {

				// strings must be surrounded by quotes
				if (argText.length() < 2 || argText.charAt(0) != '\"' || argText.charAt(argText.length() - 1) != '\"') {
					throw new AutoPilotException("parameter type '%s' is not implemented", param.getName());
				}
				argText = argText.substring(1, argText.length() - 1);
				objectArgs[argIndex] = argText;

			} else if (param.isEnum()) {

				argText = argText.toLowerCase();
				for (Object enumFind : param.getEnumConstants()) {
					String enumText = enumFind.toString().toLowerCase();
					if (Objects.equals(argText, enumText)) {
						objectArgs[argIndex] = enumFind;
						break;
					}
				}
				if (null == objectArgs[argIndex])
					throw new AutoPilotException("enumeration value %s not found in %s", stringArgs[argIndex],
							Arrays.asList(param.getEnumConstants()));

			} else {
				throw new AutoPilotException("parameter type '%s' is not implemented", param.getName());
			}
		}
		return objectArgs;
	}

	protected Method findMethod(Object classInstance, String methodName) {

		String findName = methodName.toLowerCase();

		for (Method method : classInstance.getClass().getMethods()) {
			String matchName = method.getName().toLowerCase();
			AutoPilotMethod ac = method.getAnnotation(AutoPilotMethod.class);

			if (null != ac && Objects.equals(findName, matchName)) {
				return method;
			}
		}

		return null;
	}

	protected Method findPrivateMethod(Object classInstance, String findName, Object[] args) throws AutoPilotException {

		Method method = null;
		Class<?>[] classArray = null;
		if (null != args) {

			// convert supplied argument list to types
			classArray = new Class<?>[args.length];
			for (int i = 0; i < args.length; i++) {
				classArray[i] = args[i].getClass();
			}
		}
		try {

			method = classInstance.getClass().getDeclaredMethod(findName, classArray);

		} catch (NoSuchMethodException e) {
			throw new AutoPilotException("Cannot locate private method '%s", e.getMessage());

		} catch (SecurityException e) {
			throw new AutoPilotException("SecurityException '%s", e.getMessage());
		}

		return method;
	}

	public Object invokeMethod(Object instance, Method meth, Object[] args) throws AutoPilotException {
		// invoke the method
		Object returnObject = null;
		try {

			meth.setAccessible(true);
			returnObject = meth.invoke(instance, args);

		} catch (IllegalAccessException e) {
			throw new AutoPilotException("IllegalAccessException calling %s", getMethodHint(meth));

		} catch (IllegalArgumentException e) {
			throw new AutoPilotException("IllegalArgumentException calling %s", getMethodHint(meth));

		} catch (InvocationTargetException e) {
			throw new AutoPilotException("%s - %s", meth.getName(), e.getCause().getMessage());
		}

		return returnObject;
	}

	protected String getMethodHint(Method method) {
		AutoPilotMethod ac = method.getAnnotation(AutoPilotMethod.class);
		return String.format("%s %s", method.getName(), (null == ac) ? "(???)" : ac.argHint());
	}

	protected void listCommands(Object classInstance) {
		traceMessage("//");
		Class<?> clazz = classInstance.getClass();
		String name = clazz.getTypeName();
		String[] names = name.split("\\.");
		traceMessage("// %s Commands:", names.length > 0 ? names[names.length - 1] : name);

		for (Method method : clazz.getMethods()) {
			name = method.getName();
			AutoPilotMethod ac = method.getAnnotation(AutoPilotMethod.class);
			if (null != ac) {
				name = name.substring(0, 1).toUpperCase() + name.substring(1);
				traceMessage("//   %s%s", name, ac.argHint());
			}
		}
	}

	@AutoPilotMethod(argHint = "( ) : lists all of the commands")
	public void listCommands() {
		if (tracing) {
			listCommands(this);
			listCommands(parentObject);
		}
	}

	public void startSequence(Double[] offsetDistances) throws AutoPilotException {
		// set drive starting offsets
		updateHistogram = new int[HIST_SIZE];
		actionListIndex = 0;
		moveSequenceTime = 0;
		actionSequenceTime = 0;

		posLeft.start(offsetDistances[MOTOR_LEFT]);
		posRight.start(offsetDistances[MOTOR_RIGHT]);
		
		nanoTimeStart = System.nanoTime();
		nanoTimeLast = nanoTimeStart;
		finished = false;
	}

	private void initializeSequence(String sequenceName) throws AutoPilotException {
		if (null == sequenceName)
			sequenceName = defaultSequenceName;

		currentSequenceName = sequenceName;
		moveSequenceTime = 0;
		actionSequenceTime = 0;
		posLeft = new MotorPosition(leftDriveName, moveSequenceTime);
		posRight = new MotorPosition(rightDriveName, moveSequenceTime);
		actionList = new ArrayList<AutoPilotAction>();

		AutoPilotAction init = new AutoPilotAction("--: Start '" + sequenceName + "'", 0.0);
		// init.setMethod(this, findPrivateMethod(this, "startSequence", null),
		// null);
		actionList.add(init);
	}

	@AutoPilotMethod(argHint = "( \"sequenceName\" ) : start a new named sequence")
	public void newSequence(String sequenceName) throws AutoPilotException {

		if (defaultSequenceName != currentSequenceName) {
			AutoPilotAction end = new AutoPilotAction("--: end '" + currentSequenceName + "'", moveSequenceTime);
			actionList.add(end);

			mapSequences.put(currentSequenceName,
					new AutoPilotNamedSequence(currentSequenceName, actionList, posLeft, posRight));
		}

		initializeSequence(sequenceName);
		// don't add to action list
		currentAction = null;
	}

	@AutoPilotMethod(argHint = "( \"sequenceName\" ) : adds an existing sequence to the current one")
	public void addSequence(String seqeunceName) throws AutoPilotException {

		AutoPilotNamedSequence namedSequence = mapSequences.get(seqeunceName);
		if (namedSequence == null) {
			new AutoPilotException("Sequence '%' not found", seqeunceName);
		}

		double newMoveSequenceTime = moveSequenceTime;
		for (AutoPilotAction action : namedSequence.getActionList()) {
			AutoPilotAction newAction = new AutoPilotAction(action, moveSequenceTime);
			actionList.add(newAction);
			newMoveSequenceTime = Math.max(newMoveSequenceTime, newAction.getScheduledTime());
		}

		double leftStartDistance = posLeft.getEndDistance();
		for (MoveSegment moveSegment : namedSequence.getPosLeft().moveSegmentList) {
			MoveSegment newMoveSegment = new MoveSegment(moveSegment, moveSequenceTime, leftStartDistance);
			posLeft.addMoveSegment(newMoveSegment);
			newMoveSequenceTime = Math.max(newMoveSequenceTime, newMoveSegment.ptLast.time);
		}

		double rightStartDistance = posRight.getEndDistance();
		for (MoveSegment moveSegment : namedSequence.getPosRight().moveSegmentList) {
			MoveSegment newMoveSegment = new MoveSegment(moveSegment, moveSequenceTime, rightStartDistance);
			posRight.addMoveSegment(newMoveSegment);
			newMoveSequenceTime = Math.max(newMoveSequenceTime, newMoveSegment.ptLast.time);
		}

		moveSequenceTime = newMoveSequenceTime;
		actionSequenceTime = newMoveSequenceTime;
		// don't add to action list
		currentAction = null;
	}

	public boolean startSequence(String sequenceName, Double[] startingDistance) {

		AutoPilotNamedSequence namedSequence = mapSequences.get(sequenceName);
		if (namedSequence == null) {
			setStatus("Sequence '%s' not found", sequenceName);
			finished = true;
			return false;
		}

		if (startingDistance != null) {
			moveOffset[MOTOR_LEFT] = startingDistance[MOTOR_LEFT];
			moveOffset[MOTOR_RIGHT] = startingDistance[MOTOR_RIGHT];
		}

		currentSequenceName = sequenceName;
		moveSequenceTime = 0;
		actionSequenceTime = 0;
		posLeft = namedSequence.getPosLeft();
		posRight = namedSequence.getPosRight();
		actionList = namedSequence.getActionList();
		finished = false;
		nanoTimeStart = System.nanoTime();
		nanoTimeLast = nanoTimeStart;

		return true;
	}

	@AutoPilotMethod(argHint = "( ) : end processing of the file")
	public void stop() {
		finished = true;
	}

	@AutoPilotMethod(argHint = "( deltaSeconds ) : overlap next action with movement, can be negative to overlap with last move")
	public void overlapNextAction(double deltaSeconds) {
		actionSequenceTime += deltaSeconds;
		currentAction.setScheduledTime(actionSequenceTime);
	}

	@AutoPilotMethod(argHint = "( accel ) : sets maximum movement acceleration")
	public void setAccel(double accel) {
		this.accel = accel;
	}

	@AutoPilotMethod(argHint = "( vel ) : sets movement cruise velocity")
	public void setCruiseVel(double cruiseVel) {
		this.cruiseVel = cruiseVel;
	}

	@AutoPilotMethod(argHint = "( radius ) : sets turn radius")
	public void setTurnRadius(double turnRadius) {
		this.turnRadius = turnRadius;
	}

	@AutoPilotMethod(argHint = "( tankWidth ) : sets the width of the tank drive")
	public void setTankWidth(double tankWidth) {
		this.tankWidth = tankWidth;
	}

	public void executeZeroPosition() throws AutoPilotException {
		// make the current drive position the 'zero' position of the following
		// moves
		Double[] currentDistances = (Double[]) invokeMethod(parentObject, parentMethodGetDistances, null);

		moveOffset[MOTOR_LEFT] = currentDistances[MOTOR_LEFT] - posLeft.getDistance();
		moveOffset[MOTOR_RIGHT] = currentDistances[MOTOR_RIGHT] - posRight.getDistance();
	}

	@AutoPilotMethod(argHint = "( ) : use the current position as the move starting location")
	public void zeroPosition() throws AutoPilotException {
		// when runnning, call zeroPositionRuntime to make the current drive
		// position effectively 'zero'
		currentAction.setMethod(this, findPrivateMethod(this, "executeZeroPosition", null), null);
	}

	@AutoPilotMethod(argHint = "( pauseSeconds ) : pause while stopped")
	public void movePause(double delaySeconds) throws AutoPilotException {
		MovePoint leftLastEndPt = posLeft.getLastEnd(moveSequenceTime);
		MovePoint rightLastEndPt = posRight.getLastEnd(moveSequenceTime);

		if (Math.abs(leftLastEndPt.vel) > minMoveValue || Math.abs(rightLastEndPt.vel) > minMoveValue) {
			throw new AutoPilotException("can only pause when stopped");
		}

		moveEnd(delaySeconds);
	}

	@AutoPilotMethod(argHint = "( deltaPos, Go|Stop ) : straight move, negative deltaPos moves backwards")
	public void move(double deltaPos, MoveEnd endState) throws AutoPilotException {

		double endVel = (endState != MoveEnd.GO) ? 0 : cruiseVel;
		double timeL = posLeft.addTarget(moveSequenceTime, accel, cruiseVel, endVel, deltaPos);
		double timeR = posRight.addTarget(moveSequenceTime, accel, cruiseVel, endVel, deltaPos);

		moveEnd(Math.max(timeL, timeR));
	}

	@AutoPilotMethod(argHint = "( moveLength, jogLength, Go|Stop ) : moves with a side offset")
	public void jogMove(double deltaPos, double jogDelta, MoveEnd endState) throws AutoPilotException {

		if (endState != MoveEnd.STOP) {
			throw new AutoPilotException("only 'Stop' is currently supported");
		}

		double moveLen = Math.abs(deltaPos);
		double moveDir = (deltaPos > 0) ? 1 : -1;

		double jogLen = Math.abs(jogDelta);

		double shortLen = 0.5 * (moveLen - jogLen);
		if (shortLen < 0) {
			throw new AutoPilotException("offset length must be less than total move length");
		}

		double longLen = 0.5 * (moveLen + jogLen);
		double lenRatio = shortLen / longLen;

		double launchTime = cruiseVel / accel;

		double launchLongAccel = accel;
		double launchLongLen = 0.5 * launchLongAccel * launchTime * launchTime;

		double launchShortAccel = launchLongAccel * lenRatio;
		double launchShortLen = 0.5 * launchShortAccel * launchTime * launchTime;
		launchShortLen = launchLongLen * lenRatio;

		double coastLongVel = cruiseVel;
		double coastShortVel = launchShortAccel * launchTime;
		coastShortVel = cruiseVel * lenRatio;

		double swapAccel = accel;
		double swapTime = (coastLongVel - coastShortVel) / swapAccel;
		double swapLen = swapTime * 0.5 * (coastShortVel + coastLongVel);

		double coastTotalLen = moveLen - launchLongLen - swapLen - launchShortLen;
		double coastLongLen = coastTotalLen / (1 + lenRatio);
		double coastShortLen = coastLongLen * lenRatio;

		if (coastLongLen < 0 || coastShortLen < 0) {
			throw new AutoPilotException("move too short or delta too big to perform");
		}

		double coastTime = coastLongLen / coastLongVel;
		coastTime = coastShortLen / coastShortVel;

		double moveTime = 2 * (launchTime + coastTime) + swapTime;

		MotorPosition s2 = jogDelta > 0 ? posLeft : posRight;
		MotorPosition s1 = jogDelta > 0 ? posRight : posLeft;

		MovePoint ptLast1 = s1.getLastEnd(moveSequenceTime);
		MovePoint ptLast2 = s2.getLastEnd(moveSequenceTime);

		double targetPos1 = ptLast1.pos + deltaPos;
		double targetPos2 = ptLast2.pos + deltaPos;

		// accel both sides to target velocities
		ptLast1 = s1.addMoveSegment("launch", ptLast1, moveDir * launchLongAccel, launchTime);
		ptLast2 = s2.addMoveSegment("launch", ptLast2, moveDir * launchShortAccel, launchTime);

		// coast
		ptLast1 = s1.addMoveSegment("cruise", ptLast1, 0, coastTime);
		ptLast2 = s2.addMoveSegment("cruise", ptLast2, 0, coastTime);

		// swap velocities
		ptLast1 = s1.addMoveSegment("swap", ptLast1, -moveDir * swapAccel, swapTime);
		ptLast2 = s2.addMoveSegment("swap", ptLast2, moveDir * swapAccel, swapTime);

		// coast
		ptLast1 = s1.addMoveSegment("cruise", ptLast1, 0, coastTime);
		ptLast2 = s2.addMoveSegment("cruise", ptLast2, 0, coastTime);

		// brake
		ptLast1 = s1.addMoveSegment("brake", ptLast1, -moveDir * launchShortAccel, launchTime);
		ptLast2 = s2.addMoveSegment("brake", ptLast2, -moveDir * launchLongAccel, launchTime);

		s1.checkMoveTarget(ptLast1, targetPos1);
		s2.checkMoveTarget(ptLast2, targetPos2);

		moveEnd(moveTime);
	}

	@AutoPilotMethod(argHint = "( degrees, Forward|Backward, Go|Stop ) : turns along a radius")
	public void turn(double degrees, MoveDirection moveDir, MoveEnd endState) throws AutoPilotException {
		MovePoint leftLastEndPt = posLeft.getLastEnd(moveSequenceTime);
		MovePoint rightLastEndPt = posRight.getLastEnd(moveSequenceTime);
		double moveTime = 0;

		if (Math.abs(leftLastEndPt.vel) < minMoveValue && Math.abs(rightLastEndPt.vel) < minMoveValue) {

			if (MoveEnd.STOP == endState) {
				moveTime = addTurnStopped(degrees, moveDir);
			} else {
				moveTime = addTurnAccel(degrees, moveDir, endState);
			}

		} else if (Math.abs(leftLastEndPt.vel) - cruiseVel < minMoveValue
				&& Math.abs(rightLastEndPt.vel) - cruiseVel < minMoveValue) {

			if (MoveEnd.STOP == endState) {
				moveTime = addTurnAccel(degrees, moveDir, endState);
			} else {
				// moveTime = addTurnCoasting(moveSequenceTime, degrees,
				// moveForward);
			}

		} else {
			throw new AutoPilotException("addTurn error: can either turn while at coast vel or stopped");
		}

		moveEnd(moveTime);
	}

	// not moving: spin by move each side in opposite directions
	double addTurnStopped(double degrees, MoveDirection moveDirection) throws AutoPilotException {

		double timeL = 0;
		double timeR = 0;
		double endVel = 0;
		double moveLen = Math.PI * tankWidth * Math.abs(degrees) / 360;

		if (MoveDirection.BACKWARD == moveDirection)
			moveLen = -moveLen;

		if (degrees > 0) {
			timeL = posLeft.addTarget(moveSequenceTime, accel, cruiseVel, endVel, -moveLen);
			timeR = posRight.addTarget(moveSequenceTime, accel, cruiseVel, endVel, moveLen);
		} else {
			timeL = posLeft.addTarget(moveSequenceTime, accel, cruiseVel, endVel, moveLen);
			timeR = posRight.addTarget(moveSequenceTime, accel, cruiseVel, endVel, -moveLen);
		}

		return Math.max(timeL, timeR);
	}

	/**
	 * Adds a turn that is either accelerating from 'stop to cruise' or from
	 * 'cruise to stop' using the current turn radius, acceleration, and turn
	 * arc length parameters
	 * 
	 * @param degrees
	 *            number of degrees turn while moving
	 * @param moveDirection
	 *            main direction of travel
	 * @param endState
	 *            end velocity
	 * @return time needed for the move
	 * @throws AutoPilotException
	 */
	double addTurnAccel(double degrees, MoveDirection moveDirection, MoveEnd endState) throws AutoPilotException {

		double longLen = 2 * Math.PI * (turnRadius + 0.5 * tankWidth) * Math.abs(degrees) / 360;
		double shortLen = 2 * Math.PI * (turnRadius - 0.5 * tankWidth) * Math.abs(degrees) / 360;
		double moveDir = (MoveDirection.FORWARD == moveDirection) ? 1 : -1;

		double launchTime = cruiseVel / accel;
		double launchLen = moveLength(accel, launchTime);

		if (shortLen < launchLen) {
			longLen += launchLen - shortLen;
			shortLen = launchLen;
		}

		double longCoastLen = longLen - launchLen;
		double longCoastTime = longCoastLen / cruiseVel;
		double moveTime = launchTime + longCoastTime;

		double shortLaunchTime = 2 * shortLen / cruiseVel;
		double shortAccel = cruiseVel / shortLaunchTime;
		// double shortLaunchLen = moveLength(shortAccel, shortLaunchTime);

		double shortPauseTime = moveTime - shortLaunchTime;

		MotorPosition shortMotor = degrees > 0 ? posRight : posLeft;
		MotorPosition longMotor = degrees > 0 ? posLeft : posRight;

		MovePoint shortPtLast = shortMotor.getLastEnd(moveSequenceTime);
		MovePoint longPtLast = longMotor.getLastEnd(moveSequenceTime);

		double shortTargetPos = shortPtLast.pos + moveDir * shortLen;
		double longTargetPos = longPtLast.pos + moveDir * longLen;

		if (MoveEnd.GO == endState) {
			// launching from stopped state
			shortPtLast = shortMotor.addMoveSegment("pause", shortPtLast, 0, shortPauseTime);
			shortPtLast = shortMotor.addMoveSegment("launch", shortPtLast, moveDir * shortAccel, shortLaunchTime);
			longPtLast = longMotor.addMoveSegment("launch", longPtLast, moveDir * accel, launchTime);
			longPtLast = longMotor.addMoveSegment("cruise", longPtLast, 0, longCoastTime);
		} else {
			// stopping from cruise velocity
			shortPtLast = shortMotor.addMoveSegment("brake", shortPtLast, -moveDir * shortAccel, shortLaunchTime);
			shortPtLast = shortMotor.addMoveSegment("pause", shortPtLast, 0, shortPauseTime);
			longPtLast = longMotor.addMoveSegment("cruise", longPtLast, 0, longCoastTime);
			longPtLast = longMotor.addMoveSegment("brake", longPtLast, -moveDir * accel, launchTime);
		}

		shortMotor.checkMoveTarget(shortPtLast, shortTargetPos);
		longMotor.checkMoveTarget(longPtLast, longTargetPos);

		return moveTime;
	}

	private void moveEnd(double moveTime) {
		moveSequenceTime += moveTime;
		actionSequenceTime = moveSequenceTime;
	}

	public static double moveLength(double accel, double time) {
		return 0.5 * accel * time * time;
	}

	public static double moveLength(double accel, double vel, double time) {
		return vel * time + 0.5 * accel * time * time;
	}

	private void processCommands(double timeNow) throws AutoPilotException {

		while (actionListIndex < actionList.size()) {

			currentAction = actionList.get(actionListIndex);
			if (currentAction.scheduledTime > timeNow) {
				currentAction = null;
				break;
			}
			actionListIndex++;

			setStatus("'%s': %5.2f %s", 
					null == currentSequenceName ? "??" : currentSequenceName, 
					timeNow, currentAction.getSourceText());
			
			currentAction.invokeActionMethod();
		}
		finished = actionListIndex >= actionList.size();
	}

	public class AutoPilotNamedSequence {
		private ArrayList<AutoPilotAction> actionList;
		private String name;
		private MotorPosition posLeft;
		private MotorPosition posRight;

		public AutoPilotNamedSequence(String newName, ArrayList<AutoPilotAction> newActionList,
				MotorPosition newPosLeft, MotorPosition newPosRight) {
			name = newName;
			actionList = newActionList;
			posLeft = newPosLeft;
			posRight = newPosRight;
		}

		double getExecTime() {
			double execTime = 0;
			if (null != actionList && actionList.size() > 0) {
				execTime = Math.max(execTime, actionList.get(actionList.size() - 1).scheduledTime);
			}
			if (null != posLeft) {
				execTime = Math.max(execTime, posLeft.getExecTime());
			}
			if (null != posRight) {
				execTime = Math.max(execTime, posRight.getExecTime());
			}
			return execTime;
		}

		String getName() {
			return name;
		}

		ArrayList<AutoPilotAction> getActionList() {
			return actionList;
		}

		MotorPosition getPosLeft() {
			return posLeft;
		}

		MotorPosition getPosRight() {
			return posRight;
		}
	}

	// helper class to contain the parsed command file information
	public class AutoPilotAction {
		private String sourceText;
		private double scheduledTime;
		private Object classInstance;
		private Method method;
		private Object[] objectArgs;

		public AutoPilotAction(String sourceText, double scheduledTime) {
			setSource(sourceText, scheduledTime);
			setMethod(null, null, null);
		}

		public AutoPilotAction(AutoPilotAction action, double timeOffset) {
			sourceText = action.sourceText;
			scheduledTime = action.scheduledTime + timeOffset;
			classInstance = action.classInstance;
			method = action.method;
			objectArgs = action.objectArgs;
		}

		public String getSourceText() {
			return sourceText;
		}

		public double getScheduledTime() {
			return scheduledTime;
		}

		public void setScheduledTime(double scheduledTime) {
			this.scheduledTime = scheduledTime;
		}

		public void setSource(String sourceText, double scheduledTime) {
			this.sourceText = sourceText;
			this.scheduledTime = scheduledTime;
		}

		public void setMethod(Object classInstance, Method method, Object[] objectArgs) {
			this.classInstance = classInstance;
			this.method = method;
			this.objectArgs = objectArgs;
		}

		public void invokeActionMethod() throws AutoPilotException {

			if (null != classInstance && null != method) {
				invokeMethod(classInstance, method, objectArgs);
			}
		}
	}

	// Move profiles for a single motor
	public class MotorPosition {
		// private final double minMoveTime = 0.001;
		private String name;
		private ArrayList<MoveSegment> moveSegmentList;
		private MovePoint ptLast;
		private int moveSegmentIndex;
		private int lastCheckedSize;

		public MotorPosition(String motorName, double startTime) {
			name = motorName;
			ptLast = new MovePoint();
			ptLast.time = startTime;

			moveSegmentList = new ArrayList<MoveSegment>();

			MoveSegment msInit = new MoveSegment("init");
			msInit.accel = 0;
			msInit.ptStart.set(ptLast);
			msInit.ptEnd.set(ptLast);
			moveSegmentList.add(msInit);

			lastCheckedSize = moveSegmentList.size();
			start(0);
		}

		public double getExecTime() {
			return moveSegmentList.get(moveSegmentList.size() - 1).ptEnd.time;
		}

		public double getDistance() {
			return ptLast.pos;
		}

		public double updatePosition(double sequenceTime) {
			MoveSegment moveSeq = null;

			// advance segment if needed
			while (moveSegmentIndex < moveSegmentList.size()) {
				moveSeq = moveSegmentList.get(moveSegmentIndex);
				if (moveSeq.ptEnd.time > sequenceTime) {
					break;
				}
				moveSegmentIndex++;
			}

			// calculate location within segment
			if (moveSeq != null) {
				MovePoint pt = moveSeq.getPoint(sequenceTime);
				ptLast.set(pt);
			}

			return getDistance();
		}

		public String toString() {
			double accel = 0;
			String desc = "-";
			if (moveSegmentIndex < moveSegmentList.size()) {
				MoveSegment ms = moveSegmentList.get(moveSegmentIndex);
				accel = ms.accel;
				desc = ms.desc.substring(0, 1);

			}
			return String.format(" %s%s:%2d %7.1f %7.1f %7.1f %7.1f", name, desc, moveSegmentIndex, accel, ptLast.vel,
					ptLast.pos, getDistance());
		}

		public void start(double sequenceOffset) {
			moveSegmentIndex = 0;
			MoveSegment ms = moveSegmentList.get(moveSegmentIndex);
			ptLast.set(ms.ptStart);
		}

		protected MovePoint getLastEnd(double startTime) {
			MovePoint ptLastEnd = moveSegmentList.get(moveSegmentList.size() - 1).ptEnd;
			startTime = Math.max(startTime, ptLastEnd.time);

			if (ptLastEnd.time < startTime) {
				// need a filler segment for the time gap
				ptLastEnd = addMoveSegment("fill", ptLastEnd, 0, startTime - ptLastEnd.time);
			}
			return ptLastEnd;
		}

		public double getEndVelocity() {
			return moveSegmentList.get(moveSegmentList.size() - 1).ptEnd.vel;
		}

		public double getEndDistance() {
			return moveSegmentList.get(moveSegmentList.size() - 1).ptEnd.pos;
		}

		public void checkMoveTarget(MovePoint ptLastEnd, double targetPos) throws AutoPilotException {

			// sanity check for the profile generation:
			// the expected target must match the point generated from the move
			// profiler
			if (Math.abs(targetPos - ptLastEnd.pos) > minMoveValue) {

				// remove entries since the last distance check
				while (moveSegmentList.size() > lastCheckedSize) {
					moveSegmentList.remove(moveSegmentList.size() - 1);
				}

				throw new AutoPilotException("Move path error %s: move end should be %.2f, path at %.2f", name,
						targetPos, ptLastEnd.pos);
			}

			// remove small roundoff errors
			ptLastEnd.pos = targetPos;

			// save size of checked moves
			lastCheckedSize = moveSegmentList.size();
		}

		public double addTargetByVelocity(double startTime, double maxAccel, double maxVel, double deltaPos)
				throws AutoPilotException {

			// ignore a zero length move
			if (Math.abs(deltaPos) < minMoveValue)
				return 0;

			// get the last segment
			MovePoint ptLastEnd = getLastEnd(startTime);

			// calc the move parameters
			double endVel = (deltaPos > 0) ? maxVel : -maxVel;
			double targetPos = ptLastEnd.pos + deltaPos;
			double moveTime = 2 * deltaPos / (ptLastEnd.vel + endVel);
			double moveAccel = (endVel - ptLastEnd.vel) / moveTime;

			if (Math.abs(moveAccel) > maxAccel) {
				throw new AutoPilotException("Move path error: move accel (%.0f) exceeds max accel (%.0f)",
						Math.abs(moveAccel), maxAccel);
			}

			ptLastEnd = addMoveSegment("vel", ptLastEnd, moveAccel, moveTime);

			checkMoveTarget(ptLastEnd, targetPos);
			return moveTime;
		}

		public double addTargetByTime(double startTime, double maxAccel, double moveTime, double deltaPos)
				throws AutoPilotException {

			// ignore a zero length move
			if (Math.abs(deltaPos) < minMoveValue)
				return 0;

			if (moveTime < minMoveValue) {
				throw new AutoPilotException("addTargetByTime: Move path error: move time too small");
			}

			// get the last segment
			MovePoint ptLastEnd = getLastEnd(startTime);

			// calculate the move parameters
			double targetPos = ptLastEnd.pos + deltaPos;
			double targetVel = 2 * deltaPos / moveTime - ptLastEnd.vel;
			double moveAccel = (targetVel - ptLastEnd.vel) / moveTime;

			if (Math.abs(moveAccel) > maxAccel) {
				throw new AutoPilotException("Move path error: move accel (%.0f) exceeds max accel (%.0f)",
						Math.abs(moveAccel), maxAccel);
			}

			// add the segment
			ptLastEnd = addMoveSegment("time", ptLastEnd, moveAccel, moveTime);

			checkMoveTarget(ptLastEnd, targetPos);
			return moveTime;
		}

		public double addTarget(double startTime, double maxAccel, double coastVel, double endVel, double deltaPos)
				throws AutoPilotException {

			// ignore a zero length move
			if (Math.abs(deltaPos) < minMoveValue)
				return 0;

			// get the last segment
			MovePoint ptLastEnd = getLastEnd(startTime);

			// calc the move parameters
			double targetPos = ptLastEnd.pos + deltaPos;
			double moveLen = Math.abs(deltaPos);
			double moveDirection = deltaPos > 0 ? 1 : -1;

			double launchAccel = moveDirection * maxAccel;
			double launchTime = Math.abs((moveDirection * coastVel - ptLastEnd.vel) / maxAccel);
			double launchLen = moveLength(maxAccel, launchTime);
			if (launchLen < minMoveValue) {
				// already moving at coastVel
				launchAccel = 0;
				launchLen = 0;
				launchTime = 0;
			}
			launchLen += Math.abs(ptLastEnd.vel) * launchTime;

			double brakeAccel = -moveDirection * maxAccel;
			double brakeTime = Math.abs((coastVel - endVel) / maxAccel);
			double brakeLen = moveLength(maxAccel, brakeTime);
			if (brakeLen < minMoveValue) {
				// move continuing with the next segment
				brakeAccel = 0;
				brakeLen = 0;
				brakeTime = 0;
			}
			brakeLen += endVel * brakeTime;

			double coastLen = moveLen - launchLen - brakeLen;
			double coastTime = coastLen / coastVel;
			if (coastLen > -minMoveValue) {
				// move needs a coast section
				if (launchLen > minMoveValue) {
					// accelerate (or de-accel) to new velocity
					ptLastEnd = addMoveSegment("launch", ptLastEnd, launchAccel, launchTime);
				}

				// constant velocity coast section
				if (coastLen > minMoveValue) {
					ptLastEnd = addMoveSegment("cruise", ptLastEnd, 0, coastTime);
				}

				if (brakeLen > minMoveValue) {
					// 'brake' to end velocity
					ptLastEnd = addMoveSegment("brake", ptLastEnd, brakeAccel, brakeTime);
				}

				double totalTime = launchTime + coastTime + brakeTime;
				checkMoveTarget(ptLastEnd, targetPos);
				return totalTime;
			}

			if (endVel < minMoveValue && Math.abs(ptLastEnd.vel) < minMoveValue) {
				// short move bounded by acceleration
				launchLen = moveLen / 2;
				launchTime = Math.sqrt(2 * launchLen / maxAccel);
				brakeLen = launchLen;
				brakeTime = launchTime;
				// accelerate (or de-accel) to new velocity
				ptLastEnd = addMoveSegment("launch", ptLastEnd, launchAccel, launchTime);
				// 'brake' to end velocity
				ptLastEnd = addMoveSegment("brake ", ptLastEnd, brakeAccel, brakeTime);

				double totalTime = launchTime + brakeTime;
				checkMoveTarget(ptLastEnd, targetPos);
				return totalTime;
			}

			double aveVel = Math.abs(0.5 * (endVel * moveDirection + ptLastEnd.vel));
			if (aveVel > minMoveValue) {
				//
				launchTime = Math.abs(moveLen / aveVel);
				launchAccel = (endVel - ptLastEnd.vel) / launchTime;

				if (Math.abs(launchAccel) > maxAccel) {
					throw new AutoPilotException("Move path error: move accel (%.0f) exceeds max accel (%.0f)",
							Math.abs(launchAccel), maxAccel);
				}
				ptLastEnd = addMoveSegment("ramp", ptLastEnd, launchAccel, launchTime);
				checkMoveTarget(ptLastEnd, targetPos);
				return launchTime;
			}

			throw new AutoPilotException("Path planning failed from %.1f to %.1f", deltaPos, targetPos);
		}

		public MovePoint addMoveSegment(String desc, MovePoint ptLastEnd, double accel, double segmentTime) {
			MoveSegment msNew = new MoveSegment(desc, ptLastEnd, accel, segmentTime);
			return addMoveSegment(msNew);
		}

		public MovePoint addMoveSegment(MoveSegment msNew) {
			moveSegmentList.add(msNew);
			String msg = String.format("  %s %s", name, msNew.toString());
			traceMessage(msg);
			return msNew.ptEnd;
		}
	}

	// Movement between two states
	public class MoveSegment {
		public String desc;
		public double accel;
		public MovePoint ptStart;
		public MovePoint ptEnd;
		public MovePoint ptLast;

		public MoveSegment(String segmentDesc) {
			desc = segmentDesc;
			accel = 0;
			ptStart = new MovePoint();
			ptEnd = new MovePoint();
			ptLast = new MovePoint();
		}

		public MoveSegment(MoveSegment ms, double timeOffset, double distanceOffset) {
			desc = ms.desc;
			accel = ms.accel;
			ptStart = new MovePoint(ms.ptStart, timeOffset, distanceOffset);
			ptEnd = new MovePoint(ms.ptEnd, timeOffset, distanceOffset);
			ptLast = new MovePoint(ms.ptEnd, timeOffset, distanceOffset);
		}

		public MoveSegment(String segmentDesc, MovePoint ptLastEnd, double segmentAccel, double segmentSeconds) {
			desc = segmentDesc;
			accel = segmentAccel;
			ptStart = new MovePoint(ptLastEnd);
			ptEnd = new MovePoint();
			ptEnd.time = ptStart.time + segmentSeconds;
			ptEnd.vel = ptStart.vel + accel * segmentSeconds;
			ptEnd.pos = ptStart.pos + ptStart.vel * segmentSeconds + 0.5 * accel * segmentSeconds * segmentSeconds;
			ptLast = new MovePoint(ptEnd);
		}

		public MovePoint getPoint(double time) {
			if (time <= ptStart.time) {
				ptLast.set(ptStart);
			} else if (time >= ptEnd.time) {
				ptLast.set(ptEnd);
			} else {
				double dt = time - ptStart.time;
				ptLast.time = time;
				ptLast.vel = ptStart.vel + accel * dt;
				ptLast.pos = ptStart.pos + ptStart.vel * dt + 0.5 * accel * dt * dt;
			}
			return ptLast;
		}

		public String toString() {
			return String.format("%6.6s  T(%4.1f %4.1f)  A%4.0f  V(%6.1f %6.1f)  D(%6.1f %6.1f)", desc, ptStart.time,
					ptEnd.time, accel, ptStart.vel, ptEnd.vel, ptStart.pos, ptEnd.pos);
		}
	}

	// Movement state at given time
	public class MovePoint {
		public double time;
		public double vel;
		public double pos;

		public MovePoint() {
			time = 0;
			vel = 0;
			pos = 0;
		}

		public MovePoint(MovePoint mp, double timeOffset, double distanceOffset) {
			time = mp.time + timeOffset;
			vel = mp.vel;
			pos = mp.pos + distanceOffset;
		}

		public MovePoint(MovePoint pt) {
			time = pt.time;
			vel = pt.vel;
			pos = pt.pos;
		}

		public void set(MovePoint pt) {
			time = pt.time;
			vel = pt.vel;
			pos = pt.pos;
		}

		public String toString() {
			return String.format("T:%7.3f V:%7.1f P:%7.1f", time, vel, pos);
		}
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	public @interface  AutoPilotMethod {
		String argHint();
	}

	public class AutoPilotException extends Exception {

		private static final long serialVersionUID = -6982331850819279678L;

		public AutoPilotException(String msg) {
			super(msg);
		}

		public AutoPilotException(String fmt, Object... objects) {
			super(String.format(fmt, objects));
		}

		public AutoPilotException(String message, Throwable throwable) {
			super(message, throwable);
		}
	}
}
