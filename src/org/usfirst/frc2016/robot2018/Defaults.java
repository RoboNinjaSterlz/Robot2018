/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

//package edu.wpi.first.wpilibj.templates;
package org.usfirst.frc2016.robot2018;

/**
 *
 * @author Montagna
 */
// This class provides the system defaults
// These should be updated after tuning the cRio NV RAM incase we need to change
// the cRio
public class Defaults {
    
    // Drive train
    public static final double DRIVETRAIN_VOLTAGE_LIMIT_DEFAULT = .95;
    public static final double DRIVETRAIN_RAMP_INCREMENT = .2;
    public static final double DRIVETRAIN_P = .15;
    public static final double DRIVETRAIN_I = 0;
    public static final double DRIVETRAIN_D = 0;
    public static final double DRIVETRAIN_F = .6;

    // Arm presets
    public static final int ARMPOSITION0 = 2214;
    public static final int ARMPOSITION1 = 2300;
    public static final int ARMPOSITION2 = 2800;
    public static final int ARMPOSITION3 = 3300;
    public static final int REVERSESOFTLIMIT = 2210;
    public static final int FORWARDSOFTLIMIT = 3450;
    public static final double ARM_P = 1;
    public static final double ARM_I = 0;
    public static final double ARM_D = 0;

    // Wheel Speeds
    public static final double WHEELSPEED_SHOOT = 1;
    public static final double WHEELSPEED_IN = .5;
    public static final double WHEELSPEED_OUT = .5;
    public static final double WHEELSPEED_ROTATE = .3;


    // Autonomous Time Defaults
     public static final double AUT_SHOOTING_DELAY = .5;

     // Autonomous Other Defaults
     public static final double AUT_ENCODER_DISTANCE_10FT = 100; //Distance in inches
     public static final double AUT_RIGHT_Y = -.8;
     public static final double AUT_LEFT_Y = .8;


}

