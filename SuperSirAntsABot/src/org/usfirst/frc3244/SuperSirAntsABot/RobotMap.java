// RobotBuilder Version: 2.0
//
// This file was generated by RobotBuilder. It contains sections of
// code that are automatically generated and assigned by robotbuilder.
// These sections will be updated in the future when you export to
// Java from RobotBuilder. Do not put any code or make any change in
// the blocks indicating autogenerated code or it will be lost on an
// update. Deleting the comments indicating the section will prevent
// it from being updated in the future.


package org.usfirst.frc3244.SuperSirAntsABot;

// BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=IMPORTS
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.drive.MecanumDrive;

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=IMPORTS
import edu.wpi.first.wpilibj.livewindow.LiveWindow;

/**
 * The RobotMap is a mapping from the ports sensors and actuators are wired into
 * to a variable name. This provides flexibility changing wiring, makes checking
 * the wiring easier and significantly reduces the number of magic numbers
 * floating around.
 */
public class RobotMap {
    // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DECLARATIONS
    public static WPI_TalonSRX driveTrainMotor_Left_Front;
    public static WPI_TalonSRX driveTrainMotor_Left_Rear;
    public static WPI_TalonSRX driveTrainMotor_Right_Front;
    public static WPI_TalonSRX driveTrainMotor_Right_Rear;
    public static MecanumDrive driveTrainMecanumDrive1;
    public static SpeedController winchMotor;
    public static DoubleSolenoid winchDoubleSolenoid;
    public static WPI_TalonSRX scissorMotor_Right_Master;
    public static WPI_TalonSRX scissorMotor_Left_Slave;
    public static SpeedController intakeMotor_Right;
    public static SpeedController intakeMotor_Left;
    public static DoubleSolenoid intakeRollers;
    public static DoubleSolenoid intakeWrist;

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DECLARATIONS

    public static void init() {
        // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTRUCTORS
        driveTrainMotor_Left_Front = new WPI_TalonSRX(1);
        
        
        driveTrainMotor_Left_Rear = new WPI_TalonSRX(2);
        
        
        driveTrainMotor_Right_Front = new WPI_TalonSRX(3);
        
        
        driveTrainMotor_Right_Rear = new WPI_TalonSRX(4);
        
        
        driveTrainMecanumDrive1 = new MecanumDrive(driveTrainMotor_Left_Front, driveTrainMotor_Left_Rear,
              driveTrainMotor_Right_Front, driveTrainMotor_Right_Rear);
        LiveWindow.addActuator("DriveTrain", "Mecanum Drive 1", driveTrainMecanumDrive1);
        driveTrainMecanumDrive1.setSafetyEnabled(true);
        driveTrainMecanumDrive1.setExpiration(0.1);
        driveTrainMecanumDrive1.setMaxOutput(1.0);

        winchMotor = new VictorSP(0);
        LiveWindow.addActuator("Winch", "Motor", (VictorSP) winchMotor);
        winchMotor.setInverted(false);
        winchDoubleSolenoid = new DoubleSolenoid(0, 0, 1);
        LiveWindow.addActuator("Winch", "Double Solenoid", winchDoubleSolenoid);
        
        scissorMotor_Right_Master = new WPI_TalonSRX(0);
        
        
        scissorMotor_Left_Slave = new WPI_TalonSRX(5);
        
        
        intakeMotor_Right = new Spark(1);
        LiveWindow.addActuator("Intake", "Motor_Right", (Spark) intakeMotor_Right);
        intakeMotor_Right.setInverted(false);
        intakeMotor_Left = new Spark(2);
        LiveWindow.addActuator("Intake", "Motor_Left", (Spark) intakeMotor_Left);
        intakeMotor_Left.setInverted(false);
        intakeRollers = new DoubleSolenoid(0, 2, 3);
        LiveWindow.addActuator("Intake", "Rollers", intakeRollers);
        
        intakeWrist = new DoubleSolenoid(0, 4, 5);
        LiveWindow.addActuator("Intake", "Wrist", intakeWrist);
        

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTRUCTORS
    
        
    }
}