package org.strykeforce.command

import edu.wpi.first.wpilibj.command.Command
import org.strykeforce.robotComponents

class MotionCommand(val direction: Double, val distance: Int) : Command() {

    val drive = robotComponents.driveSubsystem()

    init {
        requires(drive)
    }

    override fun initialize() {
        drive.motionTo(direction,  distance)
    }

    override fun isFinished(): Boolean = drive.isMotionFinished

    override fun end() {
        drive.endMotion()
    }
}