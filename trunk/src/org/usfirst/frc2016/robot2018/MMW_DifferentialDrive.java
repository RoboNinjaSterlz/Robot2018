package org.usfirst.frc2016.robot2018;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.SpeedController;

public class MMW_DifferentialDrive extends DifferentialDrive {

	  public MMW_DifferentialDrive(SpeedController leftMotor, SpeedController rightMotor) {
		  super(leftMotor, rightMotor);
	  }
	  
	  public void pingMotorSafety() {
		  m_safetyHelper.feed();
	  }
		   
}
