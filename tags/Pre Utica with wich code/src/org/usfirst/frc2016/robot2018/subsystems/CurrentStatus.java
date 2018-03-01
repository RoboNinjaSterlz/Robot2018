package org.usfirst.frc2016.robot2018.subsystems;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

public class CurrentStatus {
	String	name;
	WPI_TalonSRX talon;
	boolean fail;
	int     failCount;
	double  amps;
	double  throttle;

	public CurrentStatus() {

	}

	public static CurrentStatus[] currentStatusSet(int size) {
		CurrentStatus[] p=new CurrentStatus[size];
		for (int i=0; i<size; i++) {
			p[i] = new CurrentStatus();
			p[i].name = "";
			p[i].talon = null;
			p[i].fail=false;
			p[i].failCount = 0;
			p[i].throttle = 0;
		}
		return p;
	}
}
