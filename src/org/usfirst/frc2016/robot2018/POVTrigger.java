package org.usfirst.frc2016.robot2018;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.*;

/*
 * 			1
 * 		4		2
 * 			3
 */
public class POVTrigger extends Button {
	private int m_direction;
	private Joystick povJoy;
	
	POVTrigger(int direction) {
		m_direction = direction;
		povJoy=new Joystick(2);
	}
	public boolean get() {
    	int pov;
    	boolean returnValue;
    	
    	pov = (povJoy.getPOV());
		switch (pov) {
		
		/*
		 * This is the case where the POV is centered, stop the intake here
		 */
		case -1:
		case 90:
		case 270:
			returnValue = false;
			break;

		/*
		 * POV is pressed forward, cause roller to expell the ball.
		 */
		case 0:
		case 45:
		case 315:
			returnValue=m_direction==1;
			break;
		
		/*
		 * POV is back, cause the ball to enter the bot.
		 */
		case 180:
		case 135:
		case 225:
			returnValue=m_direction==3;
			break;
			

		/*
		 *  While this isn't obvious, there are positions between the above cases.
		 * If the operator drifts into one of these, the defalut code below
		 * prevents the roller from stopping. If the pov continues to move and
		 * lands on one of the above cases, the roller will change as needed.
		 */
		default:returnValue=false;
			
			break;
		}
		
	return returnValue;
	}
}
