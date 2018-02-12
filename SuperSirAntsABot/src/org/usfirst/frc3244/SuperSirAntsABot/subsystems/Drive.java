// RobotBuilder Version: 2.0
//
// This file was generated by RobotBuilder. It contains sections of
// code that are automatically generated and assigned by robotbuilder.
// These sections will be updated in the future when you export to
// Java from RobotBuilder. Do not put any code or make any change in
// the blocks indicating autogenerated code or it will be lost on an
// update. Deleting the comments indicating the section will prevent
// it from being updated in the future.


package org.usfirst.frc3244.SuperSirAntsABot.subsystems;

import org.usfirst.frc3244.SuperSirAntsABot.Constants;
import org.usfirst.frc3244.SuperSirAntsABot.Robot;
import org.usfirst.frc3244.SuperSirAntsABot.RobotMap;
import org.usfirst.frc3244.SuperSirAntsABot.commands.*;
import org.usfirst.frc3244.SuperSirAntsABot.util.Utils;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.sensors.PigeonIMU;
import com.ctre.phoenix.motorcontrol.*;
//import com.ctre.CANTalon;
//import com.ctre.CANTalon.FeedbackDevice;
//import com.ctre.CANTalon.TalonControlMode;
import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *Code from FRC #1519 (Mechanical Mayhem)
 *https://www.chiefdelphi.com/forums/showthread.php?t=141120
 *
 *With Team 3244 Granite City Gearheads
 *
 *modifications:
 *Added 
 *	set all the Talon feedback Devices
 *	Configure Nominal Output Voltage
 *	Configure Peak Output Voltage
 *	set all the Talon SRX encoders to reverse
 *	Disable Field Oriantated if Gyro Fails
 *	Added private boolean m_preserveHeading_Enable = false; 
 *
 *Deleted 
 *	non relevant Ultra sonic code
 *
 *Might add
 *	Code to set Quad encoders
 *
 *Changed Gyro to use the NavX Mini
 *
 *We are Allowing rotation while crawling
 *
 *2018 Porting over to 2018 WPI_TalonSRX
 *
 */

/**
 *
 */
public class Drive extends Subsystem {
	
	//private boolean MISSING_METHOD;
	private PigeonIMU headingIMU;
    //private AHRS headingGyro;// = RobotMap.ahrs;
    private ADXRS450_Gyro headingGyro_BCK;// = RobotMap.adrxs450_Gyro;
    
    private final WPI_TalonSRX front_Left = RobotMap.driveTrainMotor_Left_Front;
    private final WPI_TalonSRX front_Right = RobotMap.driveTrainMotor_Right_Front;
    private final WPI_TalonSRX back_Left = RobotMap.driveTrainMotor_Left_Rear;
    private final WPI_TalonSRX back_Right = RobotMap.driveTrainMotor_Right_Rear;
    
 // member variables for Mecanum drive
 	private static final int kMaxNumberOfMotors = 4;
 	private final int m_invertedMotors[] = new int[kMaxNumberOfMotors];
 	private static final int kFrontLeft = 0;
 	private static final int kFrontRight = 1;
 	private static final int kBackLeft = 2;
 	private static final int kBackRight = 3;


 	// create objects needed for independent control of each wheel
 	private WPI_TalonSRX[] m_talons = new WPI_TalonSRX[kMaxNumberOfMotors];
 	private double m_wheelSpeeds[] = new double[kMaxNumberOfMotors];
 	private double m_zeroPositions[] = new double[kMaxNumberOfMotors];

 	private boolean m_useVoltageRamp = true;
 	private double m_voltageRampRate = 36.0;//48.0; // in volts/second
 	private boolean m_breakMode = true;
 	private boolean m_fieldOrientedDrive = false;

 	private int m_iterationsSinceRotationCommanded = 0;
 	private double m_desiredHeading = 0.0;
 	private boolean m_drivingAutoInTeleop = false;
 	
 	// driving scaling factors
 	private static final double FORWARD_BACKWARD_FACTOR = RobotMap.RobotDriveTrainSettings.FORWARD_BACKWARD_FACTOR.get(); //FORWARD_BACKWARD_FACTOR = 1.0;
 	private static final double ROTATION_FACTOR = RobotMap.RobotDriveTrainSettings.ROTATION_FACTOR.get(); //ROTATION_FACTOR = 1.25;
 	private static final double STRAFE_FACTOR = RobotMap.RobotDriveTrainSettings.STRAFE_FACTOR.get(); //STRAFE_FACTOR = 2.0;
 	private static final double SLOW_FACTOR = 0.35; // scaling factor for (normal) "slow mode" .35
 	private static final double CRAWL_INPUT = 0.35; // "crawl" is a gentle control input
 	public static final double ALIGN_SPEED = 0.10;

 	// member variables to support closed loop mode
 	private boolean m_closedLoopMode = true;
 	private ControlMode m_closedLoopMode2018;
 	private double m_maxWheelSpeed = 417; // // 2016 = 445; //(10.5 Gear box = 445)//360(12.75 gear box);//550.0;     // empirically measured around 560 to 580	
 	private double m_encoderUnitsPerRev = 4096;
 	
 	// Ramp rates in Seconds
 	private double m_closedLoopRamp_sec = .25;
 	private double m_openLoopRamp_sec = 0.0;
 	
 	//
 	private boolean m_preserveHeading_Enable = true; 
 	private int m_preserveHeading_Iterations = 50;//5 Original Driver Didn't like the snappy action
 	private double kP_preserveHeading_Telepo = 0.005; // 0.025; Original Driver Didn't like the snappy action
 	private double kP_preserveHeading_Auto = 0.025; // 0.025
 	private boolean reportERROR_ONS = false;
 	
 	private boolean m_Craling = false;
	
  
 	double [] xyz_dps = new double [3];
 	double m_currentAngle;
	boolean m_angleIsGood;
	double m_currentAngularRate;
    // Put methods for controlling this subsystem
    // here. Call these from Commands.

 	
	
    public Drive() {
    	int talonIndex = 0;

    	// construct the talons
    	m_talons[kFrontLeft] = front_Left;
    	m_talons[kFrontRight] = front_Right;
    	m_talons[kBackLeft] = back_Left;   //Physically Front Left
    	m_talons[kBackRight] = back_Right;

    	// set all Talon SRX encoder values to zero
		for (talonIndex = 0; talonIndex < kMaxNumberOfMotors; talonIndex++) {
			//m_talons[talonIndex].setPosition(0);
			m_talons[talonIndex].setSelectedSensorPosition(0, 0, Constants.kTimeoutMs);
		}
		
		// set all the Talon feedback Devices
		for (talonIndex = 0; talonIndex < kMaxNumberOfMotors; talonIndex++) {
			//m_talons[talonIndex].setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Relative);
			m_talons[talonIndex].configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, Constants.kTimeoutMs);
		}
		
		// Configure Nominal Output Voltage
		for (talonIndex = 0; talonIndex < kMaxNumberOfMotors; talonIndex++) {
			//m_talons[talonIndex].configNominalOutputVoltage(+0.0f, -0.0f);
			m_talons[talonIndex].configNominalOutputForward(0, Constants.kTimeoutMs);
			m_talons[talonIndex].configNominalOutputReverse(0, Constants.kTimeoutMs);
		}
		
		// Configure Peak Output Voltage
		for (talonIndex = 0; talonIndex < kMaxNumberOfMotors; talonIndex++) {
			//m_talons[talonIndex].configPeakOutputVoltage(+12.0f, -12.0f);
			m_talons[talonIndex].configPeakOutputForward(1, Constants.kTimeoutMs);
			m_talons[talonIndex].configPeakOutputReverse(-1, Constants.kTimeoutMs);
		}
		
		// set all the Talon SRX encoders to reverce
		for (talonIndex = 0; talonIndex < kMaxNumberOfMotors; talonIndex++) {
			//m_talons[talonIndex].reverseSensor(true);
			m_talons[talonIndex].setSensorPhase(true);
		}

		// put all Talon SRX into brake mode
		for (talonIndex = 0; talonIndex < kMaxNumberOfMotors; talonIndex++) {
			//m_talons[talonIndex].enableBrakeMode(m_breakMode);
			m_talons[talonIndex].setNeutralMode(NeutralMode.Brake);
			
		}

		// ensure ramp rate set accordingly
		if (m_useVoltageRamp) {
			for (talonIndex = 0; talonIndex < kMaxNumberOfMotors; talonIndex++) {
				//m_talons[talonIndex].setVoltageRampRate(m_voltageRampRate);
				m_talons[talonIndex].configClosedloopRamp(m_closedLoopRamp_sec, Constants.kTimeoutMs);
			}
		} else {
			// clear all voltage ramp rates
			for (talonIndex = 0; talonIndex < kMaxNumberOfMotors; talonIndex++) {
				//m_talons[talonIndex].setVoltageRampRate(0.0);
				m_talons[talonIndex].configClosedloopRamp(m_openLoopRamp_sec, Constants.kTimeoutMs);
			}
		}
		// Also need to set up the "inverted motors" array for the mecanum drive
		// code
		m_invertedMotors[kFrontLeft] = 1;
		m_invertedMotors[kFrontRight] = -1;
		m_invertedMotors[kBackLeft] = 1;
		m_invertedMotors[kBackRight] = -1;
    }
    
    public void init() {
		// complete initialization here that can't be performed in constructor
		// (some calls can't be made in constructor because other objects don't
		// yet exist)

		// Set up the TalonSRX closed loop / open loop mode for each wheel
		if (m_closedLoopMode) {
			setClosedLoopMode();
		} else {
			setOpenLoopMode();
		}
	}

	public void initDefaultCommand() {
		// Set the default command for a subsystem here.
		// setDefaultCommand(new Command());
	}
	
	public double getMaxWheelSpeed() {
		return (m_maxWheelSpeed);
	}

	public void zeroDistanceTraveled() {
		int talonIndex = 0;
		// record current positions as "zero" for all of the Talon SRX encoders
		for (talonIndex = 0; talonIndex < kMaxNumberOfMotors; talonIndex++) {
			m_zeroPositions[talonIndex] = m_talons[talonIndex].getSelectedSensorPosition(0)/4096;
		}
	}

	public double getDistanceTraveled() {
		int talonIndex = 0;
		double tempDistance = 0;
		// add up the absolute value of the distances from each individual wheel
		for (talonIndex = 0; talonIndex < kMaxNumberOfMotors; talonIndex++) {
			tempDistance += Math.abs((m_talons[talonIndex].getSelectedSensorPosition(0)/4096)
					- m_zeroPositions[talonIndex]);
		}
		return (tempDistance);
	}

	public void setWheelPIDF() {
		int talonIndex = 0;
		double wheelP = 0.5;//1.02;//RobotPreferences.getWheelP();
		double wheelI = 0.0;//RobotPreferences.getWheelI();
		double wheelD = 0.0;//RobotPreferences.getWheelD();
		double wheelF = 0.4158; // 0.337;// 0.337 for 10.75 gear box 0.416 for 12.7;//RobotPreferences.getWheelF();

		// set the PID values for each individual wheel
		for (talonIndex = 0; talonIndex < kMaxNumberOfMotors; talonIndex++) {
			//m_talons[talonIndex].setPID(wheelP, wheelI, wheelD, wheelF, 0, m_voltageRampRate, 0);
			m_talons[talonIndex].config_kP(0, wheelP, 0);
			m_talons[talonIndex].config_kI(0, wheelI, 0);
			m_talons[talonIndex].config_kD(0, wheelD, 0);
			m_talons[talonIndex].config_kF(0, wheelF, 0);
		}
		DriverStation.reportError("setWheelPIDF: " + wheelP + " " + wheelI
				+ " " + wheelD + " " + wheelF + "\n", false);
	}
	
	@Override
    public void periodic() {
		getPigeonIMU();
	}
	
	private void getPigeonIMU() {
		/* some temps for Pigeon API */
		PigeonIMU.GeneralStatus genStatus = new PigeonIMU.GeneralStatus();
		PigeonIMU.FusionStatus fusionStatus = new PigeonIMU.FusionStatus();
		
		/* grab some input data from Pigeon and gamepad*/
		headingIMU.getGeneralStatus(genStatus);
		headingIMU.getRawGyro(xyz_dps);
		headingIMU.getFusedHeading(fusionStatus);
		m_currentAngle = fusionStatus.heading;
		m_angleIsGood = (headingIMU.getState() == PigeonIMU.PigeonState.Ready) ? true : false;
		m_currentAngularRate = xyz_dps[2];
	}
	
	public void setgyroOffset(double adjustment){
		headingIMU.setFusedHeading(adjustment, 10); /* reset heading, angle measurement wraps at plus/minus 23,040 degrees (64 rotations) */
		//headingGyro.setAngleAdjustment(adjustment);
		//headingGyro_BCK.setAngledAdjustimenet(adjustment); // Not available
	}
	
	public double getHeading() {
		double heading;
		if(m_angleIsGood) {//headingGyro.isConnected()){
			heading = headingIMU.getFusedHeading();
			//heading = headingGyro.getAngle();
		}else{
			heading = 0;//headingGyro_BCK.getAngle() + headingGyro.getAngleAdjustment();//Try to use the Back up Gyro with the angle Adjustment
		}
		
		return heading;
		//return headingGyro.getFusedHeading();
	}

	public void resetHeadingGyro() {
		headingIMU.setFusedHeading(0.0, 10);
		//headingGyro.reset();
		headingGyro_BCK.reset();
		m_desiredHeading = 0.0;
	}

	public void clearDesiredHeading() {
		m_desiredHeading = getHeading();
	}
	
	public void setdesiredHeading(double heading){
		m_desiredHeading = heading;
	}

	public void recalibrateHeadingGyro() {
		headingIMU.setFusedHeading(0.0, 10);
		//headingGyro.reset();
		headingGyro_BCK.reset();
//		headingGyro.free();
//		headingGyro = new AnalogGyro(RobotMap.HEADING_GYRO);
//		m_desiredHeading = 0.0;
	}
	

	public void setFieldOrientedDrive(boolean enable){
		
			m_fieldOrientedDrive = enable;
			SmartDashboard.putBoolean("Field Oriented Drive", m_fieldOrientedDrive);
			
	}
	
	public void toggleFieldOrientedDrive() {
		m_fieldOrientedDrive = !m_fieldOrientedDrive;
		SmartDashboard.putBoolean("Field Oriented Drive",
				m_fieldOrientedDrive);
	}

	public void setClosedLoopMode() {
		m_closedLoopMode2018 = ControlMode.Velocity;
		
		int talonIndex = 0;
		m_closedLoopMode = true;
		setWheelPIDF();
		/*for (talonIndex = 0; talonIndex < kMaxNumberOfMotors; talonIndex++) {
			m_talons[talonIndex].changeControlMode(TalonControlMode.Speed);
			m_talons[talonIndex].enableControl();
			
		}*/
		
	}

	public void setOpenLoopMode() {
		m_closedLoopMode2018 = ControlMode.PercentOutput;
		/*
		int talonIndex = 0;
		m_closedLoopMode = false;
		for (talonIndex = 0; talonIndex < kMaxNumberOfMotors; talonIndex++) {
			m_talons[talonIndex].changeControlMode(TalonControlMode.PercentVbus);
			m_talons[talonIndex].enableControl();
		}
		*/
	}
	
	public int getLoopMode(int talonIndex){
		if(talonIndex < kMaxNumberOfMotors){
			//return m_talons[talonIndex].getControlMode().getValue();
			return m_talons[talonIndex].getControlMode().value;
		}else{
			return 0;
		}
		
	}

	public void toggleClosedLoopMode() {
		if (!m_closedLoopMode) {
			setClosedLoopMode();
		} else {
			setOpenLoopMode();
		}
	}
	
	public void set_PreserveHeading(boolean set){
		if(set){
			m_preserveHeading_Enable = true;
			m_iterationsSinceRotationCommanded = 0;
		}else{
			m_preserveHeading_Enable = false;
		}
	
		
		//******* Per Driver request m_preserveHeading_Enable = false;
		//m_preserveHeading_Enable = false;
	}
	
	public void updateSmartDashboard() {

		if (Robot.DEBUG) {
			SmartDashboard.putNumber("Front Left SRX Position",
					(m_talons[kFrontLeft].getSelectedSensorPosition(0)/4096) - m_zeroPositions[kFrontLeft]);
			SmartDashboard.putNumber("Front Right SRX Position",
					-((m_talons[kFrontRight].getSelectedSensorPosition(0)/4096) - m_zeroPositions[kFrontRight]));
			SmartDashboard.putNumber("Back Left SRX Position",
					(m_talons[kBackLeft].getSelectedSensorPosition(0)/4096) - m_zeroPositions[kBackLeft]);
			SmartDashboard.putNumber("Back Right SRX Position",
					-((m_talons[kBackRight].getSelectedSensorPosition(0)/4096) - m_zeroPositions[kBackRight]));

			SmartDashboard.putNumber("Front Left SRX Speed",
					m_talons[kFrontLeft].getSelectedSensorVelocity(0));
			SmartDashboard.putNumber("Front Right SRX Speed",
					-m_talons[kFrontRight].getSelectedSensorVelocity(0));
			SmartDashboard.putNumber("Back Left SRX Speed",
					m_talons[kBackLeft].getSelectedSensorVelocity(0));
			SmartDashboard.putNumber("Back Right SRX Speed",
					-m_talons[kBackRight].getSelectedSensorVelocity(0));
			
			/*
			SmartDashboard.putNumber("Front Left SRX Position",
					m_talons[kFrontLeft].getPosition() - m_zeroPositions[kFrontLeft]);
			SmartDashboard.putNumber("Front Right SRX Position",
					-(m_talons[kFrontRight].getPosition() - m_zeroPositions[kFrontRight]));
			SmartDashboard.putNumber("Back Left SRX Position",
					m_talons[kBackLeft].getPosition() - m_zeroPositions[kBackLeft]);
			SmartDashboard.putNumber("Back Right SRX Position",
					-(m_talons[kBackRight].getPosition() - m_zeroPositions[kBackRight]));

			SmartDashboard.putNumber("Front Left SRX Speed",
					m_talons[kFrontLeft].getSpeed());
			SmartDashboard.putNumber("Front Right SRX Speed",
					-m_talons[kFrontRight].getSpeed());
			SmartDashboard.putNumber("Back Left SRX Speed",
					m_talons[kBackLeft].getSpeed());
			SmartDashboard.putNumber("Back Right SRX Speed",
					-m_talons[kBackRight].getSpeed());
			*/
			
			SmartDashboard.putNumber("FL Desired Speed",
					m_wheelSpeeds[kFrontLeft]);
			SmartDashboard.putNumber("FR Desired Speed",
					-m_wheelSpeeds[kFrontRight]);
			SmartDashboard.putNumber("BL Desired Speed",
					m_wheelSpeeds[kBackLeft]);
			SmartDashboard.putNumber("BR Desired Speed",
					-m_wheelSpeeds[kBackRight]);
			
			SmartDashboard.putNumber("Front Left SRX Close loop Error",
					m_talons[kFrontLeft].getClosedLoopError(0));
			SmartDashboard.putNumber("Front Right SRX Close loop Error",
					-m_talons[kFrontRight].getClosedLoopError(0));
			SmartDashboard.putNumber("Back Left SRX Close loop Error",
					m_talons[kBackLeft].getClosedLoopError(0));
			SmartDashboard.putNumber("Back Right SRX Close loop Error",
					-m_talons[kBackRight].getClosedLoopError(0));

			SmartDashboard.putNumber("Front Left Current",
					Robot.pdp.getCurrent(Constants.DRIVE_FRONT_LEFT_PDP));
			SmartDashboard.putNumber("Front Right Current",
					Robot.pdp.getCurrent(Constants.DRIVE_FRONT_RIGHT_PDP));
			SmartDashboard.putNumber("Back Left Current",
					Robot.pdp.getCurrent(Constants.DRIVE_BACK_LEFT_PDP));
			SmartDashboard.putNumber("Back Right Current",
					Robot.pdp.getCurrent(Constants.DRIVE_BACK_RIGHT_PDP));

			SmartDashboard.putNumber("Front Left Output Voltage",
					m_talons[kFrontLeft].getMotorOutputVoltage());
			SmartDashboard.putNumber("Front Right Output Voltage",
					-m_talons[kFrontRight].getMotorOutputVoltage());
			SmartDashboard.putNumber("Back Left Output Voltage",
					m_talons[kBackLeft].getMotorOutputVoltage());
			SmartDashboard.putNumber("Back Right Output Voltage",
					-m_talons[kBackRight].getMotorOutputVoltage());

			SmartDashboard.putNumber("Gyro",
					Utils.twoDecimalPlaces(headingIMU.getFusedHeading()));//headingGyro.getFusedHeading()));
			
			//SmartDashboard.putBoolean(  "IMU_Connected",        headingGyro.isConnected());
            //SmartDashboard.putBoolean(  "IMU_IsCalibrating",    headingGyro.isCalibrating());
            //SmartDashboard.putNumber(   "IMU_Yaw",              headingGyro.getYaw());
            
			SmartDashboard.putNumber("Desired Heading", m_desiredHeading);

			//SmartDashboard.putBoolean("Turbo Mode", Robot.oi.driveTurboMode());
			SmartDashboard.putBoolean("Closed Loop Mode", m_closedLoopMode);
			SmartDashboard.putBoolean("Field Oriented Drive",
					m_fieldOrientedDrive);


		}
	}
	
	/**
	 * Normalize all wheel speeds if the magnitude of any wheel is greater than
	 * 1.0.
	 */
	private void normalizeAndScaleWheelSpeeds() {
		int i;
		double tempMagnitude;
		double maxMagnitude;

		// find maxMagnitude
		maxMagnitude = Math.abs(m_wheelSpeeds[0]);
		for (i = 1; i < kMaxNumberOfMotors; i++) {
			tempMagnitude = Math.abs(m_wheelSpeeds[i]);
			if (tempMagnitude > maxMagnitude) {
				maxMagnitude = tempMagnitude;
			}
		}

		// if any wheel has a magnitude greater than 1.0, reduce all to fit in
		// range
		if (maxMagnitude > 1.0) {
			for (i = 0; i < kMaxNumberOfMotors; i++) {
				m_wheelSpeeds[i] = m_wheelSpeeds[i] / maxMagnitude;
			}
		}

		// if in closedLoopMode, scale wheels to be speeds, rather than power
		// percentage
		if (m_closedLoopMode) {
			for (i = 0; i < kMaxNumberOfMotors; i++) {
				/* Speed mode */
	        	/* 4096 Units/Rev * 500 RPM / 600 100ms/min in either direction: velocity setpoint is in units/100ms */
				m_wheelSpeeds[i] = m_wheelSpeeds[i] * m_maxWheelSpeed * m_encoderUnitsPerRev / 600;
			}
		}
	}


	/**
	 * Correct any inverted motors
	 */
	private void correctInvertedMotors() {
		int i;

		for (i = 0; i < kMaxNumberOfMotors; i++) {
			m_wheelSpeeds[i] = m_wheelSpeeds[i] * m_invertedMotors[i];
		}
	}

	/**
	 * Rotate a vector in Cartesian space.
	 */
	protected static double[] rotateVector(double x, double y, double angle) {
		double cosA = Math.cos(angle * (3.14159 / 180.0));
		double sinA = Math.sin(angle * (3.14159 / 180.0));
		double out[] = new double[2];
		out[1] = x * cosA - y * sinA;
		out[0] = x * sinA + y * cosA;
		return out;
	}
	
	/**
	 * Drive method for Mecanum wheeled robots.
	 *
	 * A method for driving with Mecanum wheeled robots. There are 4 wheels on
	 * the robot, arranged so that the front and back wheels are toed in 45
	 * degrees. When looking at the wheels from the top, the roller axles should
	 * form an X across the robot.
	 *
	 * This is designed to be directly driven by joystick axes.
	 *
	 * @param x
	 *            The speed that the robot should drive in the X direction.
	 *            [-1.0..1.0]
	 * @param y
	 *            The speed that the robot should drive in the Y direction.
	 *            [-1.0..1.0]
	 * @param rotation
	 *            The rate of rotation for the robot that is completely
	 *            independent of the translation. [-1.0..1.0]
	 */
	public void mecanumDriveTeleop(double xIn, double yIn, double rotation) {

		// check for the presence of the special "crawl" commands and do those
		// if commanded
		if (Robot.oi.crawlBackward()) {
			xIn = 0.0;
			yIn = -CRAWL_INPUT;
			rotation = rotation * .5;
			m_Craling = true;
		}else if (Robot.oi.crawlForward()) {
			xIn = 0.0;
			yIn = CRAWL_INPUT;
			rotation = rotation * .5;
			m_Craling = true;
		}else if (Robot.oi.crawlRight()) {
			xIn = CRAWL_INPUT;
			yIn = 0.0;
			rotation = rotation * .5;
			m_Craling = true;
		}else if (Robot.oi.crawlLeft()) {
			xIn = -CRAWL_INPUT;
			yIn = 0.0;
			rotation = rotation * .5;
			m_Craling = true;
		}else{
			m_Craling = false;
		}

		//Disable Field Oriantated if Gyro Fails
		boolean IMU_Connected = true;//headingGyro.isConnected();
		if(!IMU_Connected){
			m_preserveHeading_Enable = false;
			m_fieldOrientedDrive = false;
			SmartDashboard.putBoolean("Field Oriented Drive", m_fieldOrientedDrive);
			if(!reportERROR_ONS){
				DriverStation.reportError("Lost Gyro - Forcing Robot Oriantated " + "\n", false);
				reportERROR_ONS = true;
			}
			
		}
		
		// Compensate for gyro angle if field-oriented drive is desired
		if (m_fieldOrientedDrive) {
			// rotate the vector to be "robot-centric"
			double rotated[] = rotateVector(yIn, xIn, getHeading());
			xIn = rotated[0];
			yIn = rotated[1];
		}

		// check to see if forward/back, strafe, and rotation are being
		// commanded.
		// values with magnitude < 0.07 are just "centering noise" and set to
		// 0.0
		if ((-0.07 < xIn) && (xIn < 0.07)) {
			xIn = 0.0;
		}
		if ((-0.07 < yIn) && (yIn < 0.07)) {
			yIn = 0.0;
		}
		if ((-0.07 < rotation) && (rotation < 0.07)) {
			rotation = 0.0;
		}

		// scale inputs to compensate for misbalance of speeds in different
		// directions
		xIn = xIn * STRAFE_FACTOR;
		
		yIn = yIn * FORWARD_BACKWARD_FACTOR;
		
		rotation = rotation * ROTATION_FACTOR;

		// apply "slowFactor" if not in "Turbo Mode"
		if (!Robot.oi.driveTurboMode() || !m_Craling) {
			xIn = xIn * 1.0;//SLOW_FACTOR;
			yIn = yIn * 1.0;//SLOW_FACTOR;
			rotation = rotation * SLOW_FACTOR;
		}

		// update count of iterations since rotation last commanded
		if ((-0.01 < rotation) && (rotation < 0.01)) {
			// rotation is practically zero, so just set it to zero and
			// increment iterations
			rotation = 0.0;
			m_iterationsSinceRotationCommanded++;
		} else {
			// rotation is being commanded, so clear iteration counter
			m_iterationsSinceRotationCommanded = 0;
		}

		// preserve heading when recently stopped commanding rotations
		if (m_iterationsSinceRotationCommanded == m_preserveHeading_Iterations) {
			m_desiredHeading = getHeading();
		} else if (m_iterationsSinceRotationCommanded > m_preserveHeading_Iterations) {
			if(m_preserveHeading_Enable){
				rotation = (m_desiredHeading - getHeading()) * kP_preserveHeading_Telepo; 
				SmartDashboard.putNumber("MaintainHeaading ROtation", rotation);
			}
		}

		// if no directions being commanded and driving in teleop, then let the "AutoInTeleop" take place
		if (m_drivingAutoInTeleop) {
			if(Math.abs(xIn)+Math.abs(yIn)>0){
				//We had three instances that the Drive train did not respond to commands
				//So if this is the problem we can fix it.
				DriverStation.reportWarning("Can Not Drive Tele-op, m_drivingAutoInTeleop is active", false);
			}
			return;
		}
		
		mecanumDriveCartesian(xIn, yIn, rotation);
	}

	public void mecanumDriveAutonomous(double xIn, double yIn, double rotation,
			double heading) {
		m_desiredHeading = heading;

		// preserve heading if no rotation is commanded
		if ((-0.01 < rotation) && (rotation < 0.01)) {
			rotation = (m_desiredHeading - getHeading()) * kP_preserveHeading_Auto; //In Auto keep the snappy action
		}
		mecanumDriveCartesian(xIn, yIn, rotation);
	}
	
	public void mecanumDriveAutoInTeleopFinished() {
		m_drivingAutoInTeleop = false;
	}
	
	public void mecanumDriveAutoInTeleop(double xIn, double yIn, double rotation) {
		m_drivingAutoInTeleop = true;
		
		// update count of iterations since rotation last commanded
		if ((-0.01 < rotation) && (rotation < 0.01)) {
			// rotation is practically zero, so just set it to zero and
			// increment iterations
			rotation = 0.0;
			m_iterationsSinceRotationCommanded++;
		} else {
			// rotation is being commanded, so clear iteration counter
			m_iterationsSinceRotationCommanded = 0;
		}

		// preserve heading when recently stopped commanding rotations
		if (m_preserveHeading_Enable && m_iterationsSinceRotationCommanded == 5) {
			m_desiredHeading = getHeading();
		} else if (m_iterationsSinceRotationCommanded > 5) {
			if(m_preserveHeading_Enable){
				rotation = (m_desiredHeading - getHeading()) * kP_preserveHeading_Auto; //In Auto keep the snappy action
			}
		}
		
		mecanumDriveCartesian(xIn, yIn, rotation);
	}

	public void mecanumDriveCartesian(double xIn, double yIn, double rotation) {
		int talonIndex = 0;

		m_wheelSpeeds[kFrontLeft] = xIn + yIn + rotation;
		m_wheelSpeeds[kFrontRight] = -xIn + yIn - rotation;
		m_wheelSpeeds[kBackLeft] = -xIn + yIn + rotation;
		m_wheelSpeeds[kBackRight] = xIn + yIn - rotation;

		normalizeAndScaleWheelSpeeds();
		correctInvertedMotors();

		// want to do all the sets immediately after one another to minimize
		// delay between commands
		// set all Talon SRX encoder values to zero
		for (talonIndex = 0; talonIndex < kMaxNumberOfMotors; talonIndex++) {
			//m_talons[talonIndex].set(m_wheelSpeeds[talonIndex]);
			m_talons[talonIndex].set(m_closedLoopMode2018, m_wheelSpeeds[talonIndex]);		
		}

	}
}

