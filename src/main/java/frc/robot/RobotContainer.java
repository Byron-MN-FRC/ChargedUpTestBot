// RobotBuilder Version: 5.0
//
// This file was generated by RobotBuilder. It contains sections of
// code that are automatically generated and assigned by robotbuilder.
// These sections will be updated in the future when you export to
// Java from RobotBuilder. Do not put any code or make any change in
// the blocks indicating autogenerated code or it will be lost on an
// update. Deleting the comments indicating the section will prevent
// it from being updated in the future.

// ROBOTBUILDER TYPE: RobotContainer.

package frc.robot;

import java.util.List;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.RamseteController;
import edu.wpi.first.math.controller.SimpleMotorFeedforward;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.trajectory.Trajectory;
import edu.wpi.first.math.trajectory.TrajectoryConfig;
import edu.wpi.first.math.trajectory.TrajectoryGenerator;
import edu.wpi.first.math.trajectory.constraint.DifferentialDriveVoltageConstraint;
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Command.InterruptionBehavior;
import edu.wpi.first.wpilibj2.command.RamseteCommand;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;
import frc.robot.Constants.AutoConstants;
import frc.robot.Constants.DriveConstants;
import frc.robot.commands.AutonomousCommand;
import frc.robot.commands.DriveWithJoystick;
import frc.robot.commands.Intake;
import frc.robot.commands.ToggleClaw;
import frc.robot.commands.driveLift;
import frc.robot.commands.getMuchHigher;
import frc.robot.subsystems.Claw;
import frc.robot.subsystems.DriveSubsystem;
import frc.robot.subsystems.IntakeSubsystem;
import frc.robot.subsystems.lift;

// END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=IMPORTS

/**
 * This class is where the bulk of the robot should be declared.  Since Command-based is a
 * "declarative" paradigm, very little robot logic should actually be handled in the {@link Robot}
 * periodic methods (other than the scheduler calls).  Instead, the structure of the robot
 * (including subsystems, commands, and button mappings) should be declared here.
 */
public class RobotContainer {

  private static RobotContainer m_robotContainer = new RobotContainer();

    // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DECLARATIONS
// The robot's subsystems
    public final IntakeSubsystem m_intakeSubsystem = new IntakeSubsystem();
    public final Claw m_claw = new Claw();
    public final lift m_lift = new lift();
    public final DriveSubsystem m_driveSubsystem = new DriveSubsystem();

// Joysticks
private final Joystick joystick2 = new Joystick(1);
private final Joystick driveJoystick = new Joystick(0);

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DECLARATIONS

  
  // A chooser for autonomous commands
  SendableChooser<Command> m_chooser = new SendableChooser<>();

  /**
  * The container for the robot.  Contains subsystems, OI devices, and commands.
  */
  private RobotContainer() {
        // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=SMARTDASHBOARD
    // Smartdashboard Subsystems


    // SmartDashboard Buttons
    SmartDashboard.putData("Autonomous Command", new AutonomousCommand());
    SmartDashboard.putData("getMuchHigher", new getMuchHigher( m_lift ));

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=SMARTDASHBOARD
    // Configure the button bindings
    configureButtonBindings();

    // Configure default commands
        // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=SUBSYSTEM_DEFAULT_COMMAND
    m_lift.setDefaultCommand(new driveLift( m_lift ));
    m_driveSubsystem.setDefaultCommand(new DriveWithJoystick( m_driveSubsystem ));


    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=SUBSYSTEM_DEFAULT_COMMAND

    // Configure autonomous sendable chooser
        // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=AUTONOMOUS

    m_chooser.setDefaultOption("Autonomous Command", new AutonomousCommand());

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=AUTONOMOUS
    
    SmartDashboard.putData("Auto Mode", m_chooser);
  }

  public static RobotContainer getInstance() {
    return m_robotContainer;
  }

  /**
   * Use this method to define your button->command mappings.  Buttons can be created by
   * instantiating a {@link GenericHID} or one of its subclasses ({@link
   * edu.wpi.first.wpilibj.Joystick} or {@link XboxController}), and then passing it to a
   * {@link edu.wpi.first.wpilibj2.command.button.JoystickButton}.
   */
  private void configureButtonBindings() {
        // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=BUTTONS
// Create some buttons
final JoystickButton button3extendArm = new JoystickButton(driveJoystick, 3);        
button3extendArm.onTrue(new getMuchHigher( m_lift ).withInterruptBehavior(InterruptionBehavior.kCancelSelf));
                        
final JoystickButton button2intake = new JoystickButton(driveJoystick, 2);        
button2intake.onTrue(new Intake( m_intakeSubsystem ).withInterruptBehavior(InterruptionBehavior.kCancelSelf));
                        
final JoystickButton btnToggleClaw = new JoystickButton(driveJoystick, 1);        
btnToggleClaw.onTrue(new ToggleClaw( m_claw ).withInterruptBehavior(InterruptionBehavior.kCancelSelf));
                        


    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=BUTTONS
  }

    // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=FUNCTIONS
public Joystick getdriveJoystick() {
        return driveJoystick;
    }

public Joystick getjoystick2() {
        return joystick2;
    }


    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=FUNCTIONS

  /**
   * Use this to pass the autonomous command to the main {@link Robot} class.
   *
   * @return the command to run in autonomous
  */
  public Command getAutonomousCommand() {
    // The selected command will be run in autonomous
   // The selected command will be run in autonomous
    //return m_chooser.getSelected();
    // Create a voltage constraint to ensure we don't accelerate too fast
    var autoVoltageConstraint =
       new DifferentialDriveVoltageConstraint(
           new SimpleMotorFeedforward(
               DriveConstants.ksVolts,
               DriveConstants.kvVoltSecondsPerMeter,
               DriveConstants.kaVoltSecondsSquaredPerMeter),
               DriveConstants.kDriveKinematics,
              10);

    // Create config for trajectory
    TrajectoryConfig config =
      new TrajectoryConfig
      (
               AutoConstants.kMaxSpeedMetersPerSecond,
               AutoConstants.kMaxAccelerationMetersPerSecondSquared)
           // Add kinematics to ensure max speed is actually obeyed
           .setKinematics(DriveConstants.kDriveKinematics)
           // Apply the voltage constraint
           .addConstraint(autoVoltageConstraint);

    // An example trajectory to follow.  All units in meters.
    Trajectory exampleTrajectory =
       TrajectoryGenerator.generateTrajectory(
           // Start at the origin facing the +X direction
           new Pose2d(0, 0, new Rotation2d(0)),
           // Pass through these two interior waypoints, making an 's' curve path
           List.of(new Translation2d(1, 1), new Translation2d(2, -1)),
           // End 3 meters straight ahead of where we started, facing forward
           new Pose2d(3, 0, new Rotation2d(0)),
           // Pass config
           config);

    RamseteCommand ramseteCommand =
       new RamseteCommand(
           exampleTrajectory,
           m_driveSubsystem::getPose,
           new RamseteController(AutoConstants.kRamseteB, AutoConstants.kRamseteZeta),
           new SimpleMotorFeedforward(
               DriveConstants.ksVolts,
               DriveConstants.kvVoltSecondsPerMeter,
               DriveConstants.kaVoltSecondsSquaredPerMeter),
           DriveConstants.kDriveKinematics,
           m_driveSubsystem::getWheelSpeeds,
           new PIDController(DriveConstants.kPDriveVel, 0, 0),
           new PIDController(DriveConstants.kPDriveVel, 0, 0),
           // RamseteCommand passes volts to the callback
           m_driveSubsystem::tankDriveVolts,
           m_driveSubsystem);

   // Reset odometry to the starting pose of the trajectory.
   m_driveSubsystem.resetOdometry(exampleTrajectory.getInitialPose());

   // Run path following command, then stop at the end.
   return ramseteCommand.andThen(() -> m_driveSubsystem.tankDriveVolts(0, 0));

  } 
}
