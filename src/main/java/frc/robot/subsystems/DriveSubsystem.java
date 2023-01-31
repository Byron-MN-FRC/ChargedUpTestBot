// RobotBuilder Version: 5.0
//
// This file was generated by RobotBuilder. It contains sections of
// code that are automatically generated and assigned by robotbuilder.
// These sections will be updated in the future when you export to
// Java from RobotBuilder. Do not put any code or make any change in
// the blocks indicating autogenerated code or it will be lost on an
// update. Deleting the comments indicating the section will prevent
// it from being updated in the future.

// ROBOTBUILDER TYPE: Subsystem.

package frc.robot.subsystems;

import org.photonvision.PhotonCamera;
import org.photonvision.PhotonUtils;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.RemoteSensorSource;
import com.ctre.phoenix.motorcontrol.StatusFrame;
import com.ctre.phoenix.motorcontrol.SupplyCurrentLimitConfiguration;
import com.ctre.phoenix.motorcontrol.TalonFXInvertType;
import com.ctre.phoenix.motorcontrol.can.TalonFXConfiguration;
// BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=IMPORTS
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;
import com.ctre.phoenix.sensors.PigeonIMU;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;

// END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=IMPORTS
import com.ctre.phoenix.sensors.PigeonIMU_StatusFrame;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.DifferentialDriveOdometry;
import edu.wpi.first.math.kinematics.DifferentialDriveWheelSpeeds;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.DriveConstants;

/**
 *
 */
public class DriveSubsystem extends SubsystemBase {
    double initialRoll;
    double initialPitch;

    // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTANTS

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTANTS

    // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DECLARATIONS
    private WPI_TalonFX leftFollower;
    private WPI_TalonFX rightFollower;
    private WPI_TalonFX leftMaster;
    private WPI_TalonFX rightMaster;
    private DifferentialDrive differentialDrive;
    private PigeonIMU pigeon;

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DECLARATIONS
    private TalonFXInvertType _rightInvert = TalonFXInvertType.CounterClockwise; // Same as invert = "false"
    private TalonFXInvertType _leftInvert = TalonFXInvertType.Clockwise; // Same as invert = "true"
    private TalonFXConfiguration _rightConfig = new TalonFXConfiguration();
    private TalonFXConfiguration _leftConfig = new TalonFXConfiguration();
    // private WPI_PigeonIMU pigeon;
    private final DifferentialDriveOdometry odometry;

    final int kCountsPerRev = 2048;
    final double kGearRatio = 20.83;
    final double kWheelRadiusInches = 3;
    final int k100msPerSecond = 10;
    // Constants such as camera and target height stored. Change per robot and goal!
    final double CAMERA_HEIGHT_METERS = Units.inchesToMeters(23.5);
    final double TARGET_HEIGHT_METERS = Units.inchesToMeters(15.13);
    // Angle between horizontal and the camera.
    final double CAMERA_PITCH_RADIANS = Units.degreesToRadians(0);

    // How far from the target we want to be
    final double GOAL_RANGE_METERS = Units.feetToMeters(5);
    // Change this to match the name of your camera
    PhotonCamera camera = new PhotonCamera("Microsoft_LifeCam_HD-3000");

    // PID constants should be tuned per robot
    final double LINEAR_P = 0.3;
    final double LINEAR_D = 0.0;
    PIDController forwardController = new PIDController(LINEAR_P, 0, LINEAR_D);

    final double ANGULAR_P = 0.05;
    final double ANGULAR_D = 0.0;
    PIDController turnController = new PIDController(ANGULAR_P, 0, ANGULAR_D);
    private double pitchOffset;
    private double rollOffset;

    /**
    *
    */
    public DriveSubsystem() {
        // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTRUCTORS
        leftFollower = new WPI_TalonFX(2);

        rightFollower = new WPI_TalonFX(0);

        leftMaster = new WPI_TalonFX(3);

        rightMaster = new WPI_TalonFX(1);

        differentialDrive = new DifferentialDrive(leftMaster, rightMaster);
        addChild("differentialDrive", differentialDrive);
        differentialDrive.setSafetyEnabled(true);
        differentialDrive.setExpiration(0.1);
        differentialDrive.setMaxOutput(1.0);

        pigeon = new PigeonIMU(4);
        pigeon.configFactoryDefault();
        rollOffset = pigeon.getRoll();
        pitchOffset = pigeon.getPitch();

        // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTRUCTORS
        motorConfigFalcon();
        // pigeon = new WPI_PigeonIMU(4);
        odometry = new DifferentialDriveOdometry(Rotation2d.fromDegrees(pigeon.getFusedHeading()),
                nativeUnitsToDistanceMeters(leftMaster.getSelectedSensorPosition()),
                nativeUnitsToDistanceMeters(rightMaster.getSelectedSensorPosition()));

    }

    @Override
    public void periodic() {
        // This method will be called once per scheduler run
        // Update the odometry in the periodic block
        odometry.update(
                Rotation2d.fromDegrees(pigeon.getFusedHeading()),
                nativeUnitsToDistanceMeters(leftMaster.getSelectedSensorPosition()),
                nativeUnitsToDistanceMeters(rightMaster.getSelectedSensorPosition()));
        SmartDashboard.putNumber("pigeon Yaw ", pigeon.getYaw());
        SmartDashboard.putNumber("pigeon Pitch ", getPitch());
        SmartDashboard.putNumber("pigeon Roll ", getRoll());

    }

    @Override
    public void simulationPeriodic() {
        // This method will be called once per scheduler run when in simulation

    }

    // Put methods for controlling this subsystem
    // here. Call these from Commands.
    // Following methods were taken from
    // docs.wpilib.org/en/stable/docs/software/pathplanning/trajectory-tutorial/creating-drive-subsystem.html

    /**
     * Returns the currently-estimated pose of the robot.
     *
     * @return The pose.
     */
    public Pose2d getPose() {
        return odometry.getPoseMeters();
    }

    /**
     * Returns the current wheel speeds of the robot.
     *
     * @return The current wheel speeds.
     */
    public DifferentialDriveWheelSpeeds getWheelSpeeds() {
        return new DifferentialDriveWheelSpeeds(
                nativeUnitsToDistanceMeters(leftMaster.getSelectedSensorVelocity() * 10),
                nativeUnitsToDistanceMeters(rightMaster.getSelectedSensorVelocity() * 10));
    }

    /**
     * Resets the odometry to the specified pose.
     *
     * @param pose The pose to which to set the odometry.
     */
    public void resetOdometry(Pose2d pose) {
        resetEncoders();
        odometry.resetPosition(Rotation2d.fromDegrees(pigeon.getFusedHeading()),
                nativeUnitsToDistanceMeters(leftMaster.getSelectedSensorPosition()),
                nativeUnitsToDistanceMeters(rightMaster.getSelectedSensorPosition()), pose);
    }

    /**
     * Drives the robot using arcade controls.
     *
     * @param fwd the commanded forward movement
     * @param rot the commanded rotation
     */
    public void arcadeDrive(double fwd, double rot) {
        differentialDrive.arcadeDrive(fwd, rot);
    }

    /**
     * Controls the left and right sides of the drive directly with voltages.
     *
     * @param leftVolts  the commanded left output
     * @param rightVolts the commanded right output
     */
    public void tankDriveVolts(double leftVolts, double rightVolts) {
        leftMaster.setVoltage(leftVolts);
        rightMaster.setVoltage(rightVolts);
        differentialDrive.feed();
    }

    /** Resets the drive encoders to currently read a position of 0. */
    public void resetEncoders() {
        leftMaster.getSensorCollection().setIntegratedSensorPosition(0, DriveConstants.kTimeoutMs);
        rightMaster.getSensorCollection().setIntegratedSensorPosition(0, DriveConstants.kTimeoutMs);
    }

    /**
     * Gets the average distance of the two encoders.
     *
     * @return the average of the two encoder readings
     */
    public double getAverageEncoderDistance() {
        return (nativeUnitsToDistanceMeters(leftMaster.getSelectedSensorPosition()) +
                nativeUnitsToDistanceMeters(rightMaster.getSelectedSensorPosition())) / 2.0;
    }

    /**
     * Sets the max output of the drive. Useful for scaling the drive to drive more
     * slowly.
     *
     * @param maxOutput the maximum output to which the drive will be constrained
     */
    public void setMaxOutput(double maxOutput) {
        differentialDrive.setMaxOutput(maxOutput);
    }

    /** Zeroes the heading of the robot. */
    public void zeroHeading() {
        pigeon.setFusedHeading(0);
    }

    /**
     * Returns the heading of the robot.
     *
     * @return the robot's heading in degrees, from -180 to 180
     */
    public double getHeading() {
        return pigeon.getFusedHeading();
    }

    /**
     * Returns the turn rate of the robot.
     *
     * @return The turn rate of the robot, in degrees per second
     */
    public double getTurnRate() {
        double[] xyz_dps = new double[3];
        pigeon.getRawGyro(xyz_dps);
        return xyz_dps[2];
    }

    // Following configuration & caluclation methods were taken from CTRE examples
    // at https://github.com/CrossTheRoadElec/Phoenix-Examples-Languages
    public void motorConfigFalcon() {
        // Factory default all motors initially
        rightMaster.configFactoryDefault();
        rightFollower.configFactoryDefault();
        leftMaster.configFactoryDefault();
        leftFollower.configFactoryDefault();

        // Set Neutral Mode
        leftMaster.setNeutralMode(NeutralMode.Brake);
        leftFollower.setNeutralMode(NeutralMode.Brake);
        rightMaster.setNeutralMode(NeutralMode.Brake);
        rightFollower.setNeutralMode(NeutralMode.Brake);

        // Configure output and sensor direction
        // leftMaster.setInverted(_leftInvert);
        leftMaster.setInverted(true);
        leftFollower.setInverted(_leftInvert);
        // rightMaster.setInverted(_rightInvert);
        rightFollower.setInverted(_rightInvert);

        // configure the max current for motor. Thought is that
        // other motors will follow.
        double ampLimit = SmartDashboard.getNumber(DriveConstants.kAmpLimitName_DT, DriveConstants.kAmpLimit_DT);
        double ampPeakLimit = SmartDashboard.getNumber(DriveConstants.kAmpPeakLimitName_DT, DriveConstants.kAmpPeak_DT);

        SupplyCurrentLimitConfiguration currentLimitingFalcons = new SupplyCurrentLimitConfiguration(
                DriveConstants.kEnableCurrentLimiting_DT, ampLimit, ampPeakLimit, DriveConstants.kthreshholdTime);

        rightMaster.configSupplyCurrentLimit(currentLimitingFalcons);

        // Reset Pigeon Configs
        pigeon.configFactoryDefault();
        // Type

        // * Now that the Left sensor can be used by the master Talon, set up the Left
        // * (Aux) and Right (Master) distance into a single Robot distance as the
        // * Master's Selected Sensor 0.

        setRobotDistanceConfigs(_rightInvert, _rightConfig);
        // FPID for Distance
        _rightConfig.slot0.kF = DriveConstants.kGains_Distanc.kF;
        _rightConfig.slot0.kP = DriveConstants.kGains_Distanc.kP;
        _rightConfig.slot0.kI = DriveConstants.kGains_Distanc.kI;
        _rightConfig.slot0.kD = DriveConstants.kGains_Distanc.kD;
        _rightConfig.slot0.integralZone = DriveConstants.kGains_Distanc.kIzone;
        _rightConfig.slot0.closedLoopPeakOutput = DriveConstants.kGains_Distanc.kPeakOutput;

        // * Heading Configs
        _rightConfig.remoteFilter1.remoteSensorDeviceID = pigeon.getDeviceID(); // Pigeon Device ID
        _rightConfig.remoteFilter1.remoteSensorSource = RemoteSensorSource.Pigeon_Yaw; // This is for a Pigeon over CAN
        _rightConfig.auxiliaryPID.selectedFeedbackSensor = FeedbackDevice.RemoteSensor1; // Set as the Aux Sensor
        _rightConfig.auxiliaryPID.selectedFeedbackCoefficient = 3600.0 / DriveConstants.kPigeonUnitsPerRotation; // Convert
        // Yaw to
        // tenths of
        // a degree

        // * false means talon's local output is PID0 + PID1, and other side Talon is
        // PID0
        // * - PID1 This is typical when the master is the right Talon FX and using
        // Pigeon
        // *
        // * true means talon's local output is PID0 - PID1, and other side Talon is
        // PID0
        // * + PID1 This is typical when the master is the left Talon FX and using
        // Pigeon

        _rightConfig.auxPIDPolarity = false;
        // FPID for Heading
        _rightConfig.slot1.kF = DriveConstants.kGains_Turning.kF;
        _rightConfig.slot1.kP = DriveConstants.kGains_Turning.kP;
        _rightConfig.slot1.kI = DriveConstants.kGains_Turning.kI;
        _rightConfig.slot1.kD = DriveConstants.kGains_Turning.kD;
        _rightConfig.slot1.integralZone = DriveConstants.kGains_Turning.kIzone;
        _rightConfig.slot1.closedLoopPeakOutput = DriveConstants.kGains_Turning.kPeakOutput;

        // Config the neutral deadband.
        _leftConfig.neutralDeadband = DriveConstants.kNeutralDeadband;
        _rightConfig.neutralDeadband = DriveConstants.kNeutralDeadband;

        // *
        // * 1ms per loop. PID loop can be slowed down if need be. For example, - if
        // * sensor updates are too slow - sensor deltas are very small per update, so
        // * derivative error never gets large enough to be useful. - sensor movement is
        // * very slow causing the derivative error to be near zero.

        int closedLoopTimeMs = 1;
        rightMaster.configClosedLoopPeriod(0, closedLoopTimeMs, DriveConstants.kTimeoutMs);
        rightMaster.configClosedLoopPeriod(1, closedLoopTimeMs, DriveConstants.kTimeoutMs);

        // Motion Magic Configs
        _rightConfig.motionAcceleration = 9500; // (distance units per 100 ms) per second
        _rightConfig.motionCruiseVelocity = 17000; // distance units per 100 ms
        _rightConfig.motionCurveStrength = 3;

        // APPLY the config settings
        // leftMaster.configAllSettings(_leftConfig);
        leftFollower.configAllSettings(_leftConfig);
        // rightMaster.configAllSettings(_rightConfig);
        rightFollower.configAllSettings(_rightConfig);

        // Set status frame periods to ensure we don't have stale data

        // * These aren't configs (they're not persistant) so we can set these after the
        // * configs.

        rightMaster.setStatusFramePeriod(StatusFrame.Status_12_Feedback1, 20, DriveConstants.kTimeoutMs);
        rightMaster.setStatusFramePeriod(StatusFrame.Status_13_Base_PIDF0, 20, DriveConstants.kTimeoutMs);
        rightMaster.setStatusFramePeriod(StatusFrame.Status_14_Turn_PIDF1, 20, DriveConstants.kTimeoutMs);
        rightMaster.setStatusFramePeriod(StatusFrame.Status_10_Targets, 10, DriveConstants.kTimeoutMs);
        leftMaster.setStatusFramePeriod(StatusFrame.Status_2_Feedback0, 5, DriveConstants.kTimeoutMs);
        pigeon.setStatusFramePeriod(PigeonIMU_StatusFrame.CondStatus_9_SixDeg_YPR, 5, DriveConstants.kTimeoutMs);

        rightFollower.follow(rightMaster);
        leftFollower.follow(leftMaster);
        // WPI drivetrain classes assume by default left & right are opposite
        // - call this to apply + to both sides when moving forward
        // tankDrive.setRightSideInverted(false);

        // set on call from autonomous
        rightMaster.selectProfileSlot(DriveConstants.kSlot_Distanc, DriveConstants.PID_PRIMARY);
        rightMaster.selectProfileSlot(DriveConstants.kSlot_Turning, DriveConstants.PID_TURN);
    }

    void setRobotDistanceConfigs(TalonFXInvertType masterInvertType, TalonFXConfiguration masterConfig) {
        /**
         * Determine if we need a Sum or Difference.
         * 
         * The auxiliary Talon FX will always be positive in the forward direction
         * because it's a selected sensor over the CAN bus.
         * 
         * The master's native integrated sensor may not always be positive when forward
         * because sensor phase is only applied to *Selected Sensors*, not native sensor
         * sources. And we need the native to be combined with the aux (other side's)
         * distance into a single robot distance.
         */

        /*
         * THIS FUNCTION should not need to be modified. This setup will work regardless
         * of whether the master is on the Right or Left side since it only deals with
         * distance magnitude.
         */

        /* Check if we're inverted */
        if (masterInvertType == TalonFXInvertType.Clockwise) {
            /*
             * If master is inverted, that means the integrated sensor will be negative in
             * the forward direction.
             * 
             * If master is inverted, the final sum/diff result will also be inverted. This
             * is how Talon FX corrects the sensor phase when inverting the motor direction.
             * This inversion applies to the *Selected Sensor*, not the native value.
             * 
             * Will a sensor sum or difference give us a positive total magnitude?
             * 
             * Remember the Master is one side of your drivetrain distance and Auxiliary is
             * the other side's distance.
             * 
             * Phase | Term 0 | Term 1 | Result Sum: -1 *((-)Master + (+)Aux )| NOT OK, will
             * cancel each other out Diff: -1 *((-)Master - (+)Aux )| OK - This is what we
             * want, magnitude will be correct and positive. Diff: -1 *((+)Aux - (-)Master)|
             * NOT OK, magnitude will be correct but negative
             */

            masterConfig.diff0Term = FeedbackDevice.IntegratedSensor; // Local Integrated Sensor
            masterConfig.diff1Term = FeedbackDevice.RemoteSensor0; // Aux Selected Sensor
            masterConfig.primaryPID.selectedFeedbackSensor = FeedbackDevice.SensorDifference; // Diff0 - Diff1
        } else {
            /* Master is not inverted, both sides are positive so we can sum them. */
            masterConfig.sum0Term = FeedbackDevice.RemoteSensor0; // Aux Selected Sensor
            masterConfig.sum1Term = FeedbackDevice.IntegratedSensor; // Local IntegratedSensor
            masterConfig.primaryPID.selectedFeedbackSensor = FeedbackDevice.SensorSum; // Sum0 + Sum1
        }

        /*
         * Since the Distance is the sum of the two sides, divide by 2 so the total
         * isn't double the real-world value
         */
        masterConfig.primaryPID.selectedFeedbackCoefficient = 0.5;
    }

    private double nativeUnitsToDistanceMeters(double sensorCounts) {
        double motorRotations = (double) sensorCounts / kCountsPerRev;
        double wheelRotations = motorRotations / kGearRatio;
        double positionMeters = wheelRotations * (2 * Math.PI * Units.inchesToMeters(kWheelRadiusInches));
        return positionMeters;
    }

    /* 4859 methods */
    public void driveWithJoystick(Joystick driveJoystick) {

        double y = 0;
        double twist = 0;

        // double forwardSpeed;
        // double rotationSpeed;

        // forwardSpeed = -xbtRawButtonPresoxController.getRightY();

        if (driveJoystick.getRawButton(11)) {
            // Vision-alignment mode
            // Query the latest result from PhotonVision
            var result = camera.getLatestResult();

            if (result.hasTargets()) {
                // First calculate range
                double range = PhotonUtils.calculateDistanceToTargetMeters(
                        CAMERA_HEIGHT_METERS,
                        TARGET_HEIGHT_METERS,
                        CAMERA_PITCH_RADIANS,
                        Units.degreesToRadians(result.getBestTarget().getPitch()));
                SmartDashboard.putNumber("range=", range);

                // Use this range as the measurement we give to the PID controller.
                // -1.0 required to ensure positive PID controller effort _increases_ range
                y = -forwardController.calculate(range, GOAL_RANGE_METERS);
                SmartDashboard.putNumber("y = ", y);
                // Also calculate angular power
                // -1.0 required to ensure positive PID controller effort _increases_ yaw
                twist = -turnController.calculate(result.getBestTarget().getYaw(), 0);
                SmartDashboard.putNumber("yaw=", result.getBestTarget().getYaw());
                SmartDashboard.putNumber("twist = ", twist);
                // y=0;
            } else {
                // If we have no targets, stay still.
                y = 0;
                twist = 0;
            }
        } else {
            // Manual Driver Mode
            y = -driveJoystick.getY();
            twist = driveJoystick.getZ();
        }
        // System.out.println("y=" + y + " + twist=" + twist);
        SmartDashboard.putNumber("z = ", twist);
        SmartDashboard.putNumber("y = ", y);
        differentialDrive.arcadeDrive((y), -(twist), true);
    }

    public void autoBalanceDrive(Joystick driveJoystick) {
        forwardController.setP(.034);
        turnController.setP(0.16);
        double y;
        double z;
        // double y = driveJoystick.getY();
        // double twist = driveJoystick.getZ();

        double pitch = getRoll();
        double roll = getPitch();
        y = forwardController.calculate(pitch, 0);
        // pitch is current value and setpoint is desired value
        z = turnController.calculate(roll, 0);
        SmartDashboard.putNumber("z = ", z);
        SmartDashboard.putNumber("y = ", y);

        if (pitch > 0) {
            differentialDrive.arcadeDrive(y, z);
        } else {
            differentialDrive.arcadeDrive(y, -z);
        }
    }

    private double getPitch() {
        return pigeon.getPitch() - pitchOffset;
    }

    private double getRoll() {
        return pigeon.getRoll() - rollOffset;
    }

}
