package org.strykeforce.subsystem

import edu.wpi.first.wpilibj.DriverStation
import edu.wpi.first.wpilibj.command.Subsystem
import mu.KotlinLogging
import org.strykeforce.command.TeleOpDriveCommand
import org.strykeforce.thirdcoast.swerve.SwerveDrive
import org.strykeforce.thirdcoast.swerve.SwerveDrive.DriveMode.TELEOP
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DriveSubsystem @Inject constructor(val swerve: SwerveDrive) : Subsystem() {

    private val logger = KotlinLogging.logger {}

    var driveMode: SwerveDrive.DriveMode = TELEOP
        set(value) = swerve.setDriveMode(value)

    fun drive(forward: Double, strafe: Double, azimuth: Double) = swerve.drive(forward, strafe, azimuth)

    fun zeroGyroYaw() {
        swerve.gyro.zeroYaw()
        val msg = "reset gyro zero"
        logger.warn(msg)
        DriverStation.reportWarning(msg, false)
    }

    override fun initDefaultCommand() {
        defaultCommand = TeleOpDriveCommand()
    }
}