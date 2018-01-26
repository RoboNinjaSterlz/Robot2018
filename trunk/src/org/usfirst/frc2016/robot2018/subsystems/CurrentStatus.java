package org.usfirst.frc2016.robot2018.subsystems;

public class CurrentStatus {
	boolean fail;
	int     failCount;
	double  throttle;

	public CurrentStatus() {

	}

	public static CurrentStatus[] currentStatusSet(int size) {
		CurrentStatus[] p=new CurrentStatus[size];
		for (int i=0; i<size; i++) {
			p[i] = new CurrentStatus();
			p[i].fail=false;
			p[i].failCount = 0;
			p[i].throttle = 0;
		}
		return p;
	}
}
