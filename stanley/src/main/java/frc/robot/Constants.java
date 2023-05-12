package frc.robot;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.LimitSwitchSource;
import com.ctre.phoenix.motorcontrol.StatorCurrentLimitConfiguration;
import com.ctre.phoenix.motorcontrol.SupplyCurrentLimitConfiguration;
import com.ctre.phoenix.motorcontrol.can.TalonFXConfiguration;
import com.ctre.phoenix.motorcontrol.can.TalonSRXConfiguration;
import com.ctre.phoenix.sensors.SensorVelocityMeasPeriod;
import edu.wpi.first.math.Matrix;
import edu.wpi.first.math.VecBuilder;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.numbers.N1;
import edu.wpi.first.math.numbers.N3;
import edu.wpi.first.math.trajectory.TrajectoryConfig;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.RobotController;
import java.util.ArrayList;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;

public class Constants {
    public static final int kTalonConfigTimeout = 10; // ms

    public static final class DriveConstants {
        public static final double gimpDefault = 0.2;
        public static final double kMaxSpeedMetersPerSecond = 3.889; // practice bot 3.889
        public static final double kWheelDiameterInches = 3.0 * (506.5 / 500.0);
        static final double kDriveMotorOutputGear = 30; // practice bot: 22
        static final double kDriveInputGear = 44; // 48
        static final double kBevelInputGear = 15;
        static final double kBevelOutputGear = 45; // 45

        // Drive Constants
        // public static final double kWheelDiameterInches = 3.0 * (563.5 / 500.0); // practice bot
        // public static final Pose2d kOdometryZeroPosBlue =
        //     new Pose2d(new Translation2d(1.80, 5.097), new Rotation2d());
        // public static final Pose2d kOdometryZeroPosRed =
        //     new Pose2d(
        //         new Translation2d(16.540988 - 1.80, 0.39), new Rotation2d());

        public static final double kShelfMovePercent = 0.8;
        public static final double kShelfYawPercent = 0.2;
        public static final double kPlaceMovePercent = 0.2;
        public static final double kPlaceYawPercent = 0.2;

        // Drive Base Size and Gearing
        public static final double kRobotWidth = 0.495; // practice bot: 0.625 //Old: .5
        public static final double kRobotLength = 0.62; // practice bot: 0.625 //Old:.615

        // Drive Base Size and Gearing

        public static final double kMaxOmega =
            (kMaxSpeedMetersPerSecond / Math.hypot(kRobotWidth / 2.0, kRobotLength / 2.0))
                / 2.0; // wheel locations below

        public static final double kDriveGearRatio =
            (kDriveMotorOutputGear / kDriveInputGear) * (kBevelInputGear / kBevelOutputGear);
        // public static double kMaxSpeedToAutoDrive = 1.0; // FIXME WRoNG VAL 1.5
        // public static double kPathErrorThreshold = 0.04; // FIXME WRONG VAL 0.03
        // public static double kPathErrorOmegaThresholdDegrees = 5; // FIXME WRONG VAL

        public static Translation2d[] getWheelLocationMeters() {
        final double x = kRobotLength / 2.0; // front-back, was ROBOT_LENGTH
        final double y = kRobotWidth / 2.0; // left-right, was ROBOT_WIDTH
        Translation2d[] locs = new Translation2d[4];
        locs[0] = new Translation2d(x, y); // left front
        locs[1] = new Translation2d(x, -y); // right front
        locs[2] = new Translation2d(-x, y); // left rear
        locs[3] = new Translation2d(-x, -y); // right rear
        return locs;
        }

        // Teleop Drive Constants
        //    public static final double kDeadbandXLock = 0.2;
        public static final double kDeadbandAllStick = 0.075;
        //    public static final double kCloseEnoughTicks = 10.0;
        public static final double kRateLimitFwdStr = 3.5; // 2
        public static final double kRateLimitYaw = 3; // 3
        //    public static final double kExpoScaleMoveFactor = 0.6; // .6
        // public static final double kRateLimitMove = 0.3;
        public static final double kExpoScaleYawFactor = 0.75;

        public static final double kPulseAutoBalanceTime = 0.2;
        public static final double kPauseAutoBalanceTime = 1.0;
        public static final double kPulseSpeed = 0.5;
        public static final double kHoldSpeed = 0.1;
        public static final double kSettleTime = 1.0;

        public static TalonSRXConfiguration getAzimuthTalonConfig() {
        // constructor sets encoder to Quad/CTRE_MagEncoder_Relative
        TalonSRXConfiguration azimuthConfig = new TalonSRXConfiguration();

        azimuthConfig.primaryPID.selectedFeedbackCoefficient = 1.0;
        azimuthConfig.auxiliaryPID.selectedFeedbackSensor = FeedbackDevice.None;

        azimuthConfig.forwardLimitSwitchSource = LimitSwitchSource.Deactivated;
        azimuthConfig.reverseLimitSwitchSource = LimitSwitchSource.Deactivated;

        azimuthConfig.continuousCurrentLimit = 10;
        azimuthConfig.peakCurrentDuration = 0;
        azimuthConfig.peakCurrentLimit = 0;
        azimuthConfig.slot0.kP = 10.0;
        azimuthConfig.slot0.kI = 0.0;
        azimuthConfig.slot0.kD = 100.0;
        azimuthConfig.slot0.kF = 1.0;
        azimuthConfig.slot0.integralZone = 0;
        azimuthConfig.slot0.allowableClosedloopError = 0;
        azimuthConfig.slot0.maxIntegralAccumulator = 0;
        azimuthConfig.motionCruiseVelocity = 800;
        azimuthConfig.motionAcceleration = 10_000;
        azimuthConfig.velocityMeasurementWindow = 64;
        azimuthConfig.velocityMeasurementPeriod = SensorVelocityMeasPeriod.Period_100Ms;
        azimuthConfig.voltageCompSaturation = 12;
        azimuthConfig.voltageMeasurementFilter = 32;
        azimuthConfig.neutralDeadband = 0.04;
        return azimuthConfig;
        }
        // Drive Falcon Config
        public static TalonFXConfiguration getDriveTalonConfig() {
        TalonFXConfiguration driveConfig = new TalonFXConfiguration();
        driveConfig.supplyCurrLimit.currentLimit = 40;
        driveConfig.supplyCurrLimit.triggerThresholdCurrent = 45;
        driveConfig.supplyCurrLimit.triggerThresholdTime = 1.0;
        driveConfig.supplyCurrLimit.enable = true;
        driveConfig.statorCurrLimit.enable = false;
        driveConfig.slot0.kP = 0.16; // 0.16
        driveConfig.slot0.kI = 0.0002;
        driveConfig.slot0.kD = 0.000;
        driveConfig.slot0.kF = 0.047;
        driveConfig.slot0.integralZone = 500;
        driveConfig.slot0.maxIntegralAccumulator = 150_000;
        driveConfig.slot0.allowableClosedloopError = 0;
        driveConfig.velocityMeasurementPeriod = SensorVelocityMeasPeriod.Period_100Ms;
        driveConfig.velocityMeasurementWindow = 64;
        driveConfig.voltageCompSaturation = 12;
        driveConfig.neutralDeadband = 0.01;
        driveConfig.voltageMeasurementFilter = 32;
        return driveConfig;
        }
        // Holonomic Controller Constants
        public static final double kPHolonomic = 0.25; // 6 0.25
        public static final double kIHolonomic = 0.0000;
        public static final double kDHolonomic = 0.00; // kPHolonomic/100
        public static final double kIMin = 0.0;
        public static final double kIMax = 0.0;

        public static final double kPOmega = 4.5;
        public static final double kIOmega = 0.0;
        public static final double kDOmega = 0.0;
        //    public static final double kMaxVelOmega = kMaxOmega / 2.0;
        public static final double kMaxAccelOmega = 5.0; // 3.14

        // Azimuth Talon Config
        public static SupplyCurrentLimitConfiguration getAzimuthSupplyCurrentLimit() {
        return new SupplyCurrentLimitConfiguration(true, 10, 15, 0.04);
        }
    }
}
