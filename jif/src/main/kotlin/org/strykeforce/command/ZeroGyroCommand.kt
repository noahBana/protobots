package org.strykeforce.command

import edu.wpi.first.wpilibj.command.InstantCommand
import org.strykeforce.robotComponents

class ZeroGyroCommand : InstantCommand() {

    val drive = robotComponents.driveSubsystem()

    init {
        requires(drive)
    }

    override fun initialize() = drive.zeroGyroYaw()
}