// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import org.strykeforce.telemetry.TelemetryController;
import org.strykeforce.telemetry.TelemetryService;

import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.wpilibj2.command.RunCommand;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;
import frc.robot.Constants.DriveConstants;
import frc.robot.subsystems.DriveSubsystem;

/**
 * This class is where the bulk of the robot should be declared. Since Command-based is a
 * "declarative" paradigm, very little robot logic should actually be handled in the {@link Robot}
 * periodic methods (other than the scheduler calls). Instead, the structure of the robot (including
 * subsystems, commands, and button mappings) should be declared here.
 */
public class RobotContainer {
  private final static double kJoystickDeadband = 0.1;
  private final TelemetryService telemetryService = new TelemetryService(TelemetryController::new);
 
  // The robot's subsystems and commands are defined here...
  private final DriveSubsystem driveSubsystem;
  private final Joystick joystick = new Joystick(0);

  /** The container for the robot. Contains subsystems, OI devices, and commands. */
  public RobotContainer() {
    driveSubsystem = new DriveSubsystem(telemetryService);
    // Configure the button bindings

    driveSubsystem.setDefaultCommand(new RunCommand(
      () -> {
        double vx = getLeftX() * -DriveConstants.kMaxSpeedMetersPerSecond;
        double vy = getLeftY() * -DriveConstants.kMaxSpeedMetersPerSecond;
        double omega = getRightY() * -DriveConstants.kMaxOmega;
        driveSubsystem.drive(vx, vy, omega);
      }, driveSubsystem));

    // telemetryService.register(driveSubsystem);
    // telemetryService.start();
    configureButtonBindings();
  }

  /**
   * Use this method to define your button->command mappings. Buttons can be created by
   * instantiating a {@link GenericHID} or one of its subclasses ({@link
   * edu.wpi.first.wpilibj.Joystick} or {@link XboxController}), and then passing it to a {@link
   * edu.wpi.first.wpilibj2.command.button.JoystickButton}.
   */
  private void configureButtonBindings() {
    new JoystickButton(joystick, InterlinkButton.RESET.id).whenPressed(driveSubsystem::resetGyro, driveSubsystem);
    // new JoystickButton(joystick, InterlinkButton.X.id).whenPressed(driveSubsystem.gimpMultiplier(1));
    new JoystickButton(joystick, InterlinkButton.HAMBURGER.id).whenPressed(() -> {driveSubsystem.resetOdometry(new Pose2d());},driveSubsystem);
  }

  //Interlink Controller Mapping
  public enum Axis{
    RIGHT_X(1),
    RIGHT_Y(0),
    LEFT_X(2),
    LEFT_Y(5),
    TUNER(6),
    LEFT_BACK(4),
    RIGHT_BACK(3);

    private final int id;
    Axis(int id){
      this.id = id;
    }
  }

  public enum Shoulder{
    RIGHT_DOWN(2),
    LEFT_DOWN(4),
    LEFT_UP(5);

    private final int id;
    Shoulder(int id){
      this.id = id;
    }
  }

  public enum Toggle{
    LEFT_TOGGLE(1);
    
    private final int id;
    Toggle(int id){
      this.id = id;
    }
  }

  public enum InterlinkButton{
    RESET(3),
    HAMBURGER(14),
    X(15),
    UP(16),
    DOWN(17);

    private final int id;
    InterlinkButton(int id){
      this.id = id;
    }
  }

  public enum Trim{
    LEFT_Y_POS(7),
    LEFT_Y_NEG(6),
    LEFT_X_POS(8),
    LEFT_X_NEG(9),
    RIGHT_X_POS(10),
    RIGHT_X_NEG(11),
    RIGHT_Y_POS(12),
    RIGHT_Y_NEG(13);

    private final int id;
    Trim(int id){
      this.id = id;
    }
  }

  public double getLeftX(){
    double val = joystick.getRawAxis(Axis.LEFT_X.id);
    if(Math.abs(val) < kJoystickDeadband) return 0.0;
    return val;
  }

  public double getLeftY(){
    double val = joystick.getRawAxis(Axis.LEFT_Y.id);
    if(Math.abs(val) < kJoystickDeadband) return 0.0;
    return val;
  }

  public double getRightY(){
    double val = joystick.getRawAxis(Axis.RIGHT_Y.id);
    if(Math.abs(val) < kJoystickDeadband) return 0.0;
    return val;
  }

}