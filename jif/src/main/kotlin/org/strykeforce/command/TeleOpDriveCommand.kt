package org.strykeforce.command

import edu.wpi.first.wpilibj.command.Command
import org.strykeforce.robotComponents
import org.strykeforce.thirdcoast.swerve.SwerveDrive.DriveMode.TELEOP

const val DEADBAND = 0.08

class TeleOpDriveCommand : Command() {

    val drive = robotComponents.driveSubsystem()
    val controls = robotComponents.controls()

    init {
        requires(drive)
    }

    override fun initialize() {
        drive.driveMode = TELEOP
    }

    override fun execute() {
        val forward = controls.forward.applyDeadband(DEADBAND)
        val strafe = controls.strafe.applyDeadband(DEADBAND)
        val azimuth = controls.azimuth.applyDeadband(DEADBAND)
        drive.drive(forward, strafe, azimuth)
    }

    override fun isFinished() = false
}

private fun Double.applyDeadband(amount: Double) = if (Math.abs(this) < amount) 0.0 else this

