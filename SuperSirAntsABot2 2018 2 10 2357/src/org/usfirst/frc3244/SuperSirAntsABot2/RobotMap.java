// RobotBuilder Version: 2.0
//
// This file was generated by RobotBuilder. It contains sections of
// code that are automatically generated and assigned by robotbuilder.
// These sections will be updated in the future when you export to
// Java from RobotBuilder. Do not put any code or make any change in
// the blocks indicating autogenerated code or it will be lost on an
// update. Deleting the comments indicating the section will prevent
// it from being updated in the future.


package org.usfirst.frc3244.SuperSirAntsABot2;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.Timer;
// BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=IMPORTS
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.VictorSP;

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=IMPORTS
import edu.wpi.first.wpilibj.livewindow.LiveWindow;

/**
 * The RobotMap is a mapping from the ports sensors and actuators are wired into
 * to a variable name. This provides flexibility changing wiring, makes checking
 * the wiring easier and significantly reduces the number of magic numbers
 * floating around.
 */
public class RobotMap {
	
	public static WPI_TalonSRX drivemotor_Front_Left;
    public static WPI_TalonSRX drivemotor_Front_Right;
    public static WPI_TalonSRX drivemotor_Back_Left;
    public static WPI_TalonSRX drivemotor_Back_Right;
    public static AHRS ahrs;
    public static ADXRS450_Gyro adrxs450_Gyro;
    
    // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DECLARATIONS
    public static SpeedController intakeMotor_Right;
    public static SpeedController intakeMotor_Left;
    public static WPI_TalonSRX winchmotor;
    public static WPI_TalonSRX scissorMotor_Right;
    public static WPI_TalonSRX scissorMotor_Left;

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DECLARATIONS
    
    //PDP Channles
    public static final int DRIVE_BACK_LEFT_PDP 	= 14;	//CAN4
    public static final int DRIVE_FRONT_LEFT_PDP 	= 13; 	//CAN1
    public static final int DRIVE_BACK_RIGHT_PDP 	= 1; 	//CAN5
	public static final int DRIVE_FRONT_RIGHT_PDP 	= 12;	//CAN2
	public static final int WINCH_PDP 				= 3;	//CAN7
	public static final int INTAKE_Right_PDP 		= 4;	//pwm0
	public static final int INTAKE_Left_PDP 		= 5;	//pwm1
	public static final int SCISSOR_LEFT_PDP		= 15;	//CAN3
	public static final int SCISSOR_RIGHT_PDP		= 2;	//CAN6
	
	
    //public static boolean isCompetitionBot = true;
    public static boolean isCompetitionBot = true;
    
    //Start Code to use the NorticSpeedControler
    public enum RobotDriveTrainSettings {
    	FORWARD_BACKWARD_FACTOR(1,.5),
    	ROTATION_FACTOR(1.25,.5),
     	STRAFE_FACTOR(2,.75);
	
		private final double m_competitionSetting;
		private final double m_practiceSetting;
		
		public double get() {
			return isCompetitionBot ? m_competitionSetting : m_practiceSetting; 
		}
		
		RobotDriveTrainSettings(double competitionSetting, double practiceSetting) {
	    	m_competitionSetting = competitionSetting;
	    	m_practiceSetting = practiceSetting;
		}
	}
    public static void init() {
    	drivemotor_Front_Left = new WPI_TalonSRX(2);
        drivemotor_Front_Right = new WPI_TalonSRX(1);
        drivemotor_Back_Left = new WPI_TalonSRX(4);        
        drivemotor_Back_Right = new WPI_TalonSRX(5);
        
    // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTRUCTORS
        intakeMotor_Right = new VictorSP(0);
        LiveWindow.addActuator("Intake", "Motor_Right", (VictorSP) intakeMotor_Right);
        intakeMotor_Right.setInverted(false);
        intakeMotor_Left = new VictorSP(1);
        LiveWindow.addActuator("Intake", "Motor_Left", (VictorSP) intakeMotor_Left);
        intakeMotor_Left.setInverted(false);
        winchmotor = new WPI_TalonSRX(7);
        
        
        scissorMotor_Right = new WPI_TalonSRX(3);
        
        
        scissorMotor_Left = new WPI_TalonSRX(6);
        
        

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTRUCTORS
      //Create Gyro
        try {
        	System.out.println("Hello Tying to INIT Navx");
            /* Communicate w/navX MXP via the MXP SPI Bus.                                     */
            /* Alternatively:  I2C.Port.kMXP, SerialPort.Port.kMXP or SerialPort.Port.kUSB     */
            /* See http://navx-mxp.kauailabs.com/guidance/selecting-an-interface/ for details. */
        
            //ahrs = new AHRS(SPI.Port.kMXP); 
        	ahrs = new AHRS(I2C.Port.kMXP);
        	//ahrs = new AHRS(SerialPort.Port.kUSB);
            //ahrs = new AHRS(I2C.Port.kOnboard); 
        	//ahrs = new AHRS(I2C.Port.kOnboard,(byte)200);
            
        } catch (RuntimeException ex ) {
        	System.out.println("Hello from the Navx runtimeExcept");
            DriverStation.reportError("Error instantiating navX MXP:  " + ex.getMessage(), true);
        }
        
        try{
        	DriverStation.reportWarning("Init ADXRS450_Gyro", false);
        	adrxs450_Gyro = new ADXRS450_Gyro();
        }catch (RuntimeException ex ) {
        	System.out.println("Hello from the adrxs450_Gyro runtimeExcept");
            DriverStation.reportError("Error instantiating adrxs450_Gyro:  " + ex.getMessage(), true);
        }
            Timer.delay(2);
    }
}
