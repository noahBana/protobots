package org.strykeforce

import edu.wpi.first.wpilibj.TimedRobot
import edu.wpi.first.wpilibj.command.Scheduler
import org.strykeforce.motion.MotionProfile
import org.strykeforce.motion.dumpCsv
import java.io.File

const val ZERO_ME = "/home/lvuser/zero.me"

class Jif : TimedRobot() {

    val swerveDrive = robotComponents.swerveDrive()
    val telemetryService = robotComponents.telemetryService()

    override fun robotInit() {
        // save azimuth zero positions by creating file named in ZERO_ME
        if (File(ZERO_ME).delete()) swerveDrive.saveAzimuthPositions()
        swerveDrive.zeroAzimuthEncoders()

        telemetryService.register(swerveDrive)
        telemetryService.start()
        robotComponents.controls() // kick
    }

    override fun teleopInit() {
        swerveDrive.stop()
    }

    override fun teleopPeriodic() {
        Scheduler.getInstance().run()
    }
}
