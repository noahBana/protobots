package org.strykeforce.motion

import edu.wpi.first.wpilibj.Notifier
import mu.KotlinLogging
import org.strykeforce.robotComponents
import org.strykeforce.thirdcoast.swerve.SwerveDrive

private const val DT_MS = 20
private const val T1_MS = 200
private const val T2_MS = 100
private const val V_PROG = (12000 * 10).toDouble() // ticks/sec

class MotionController(direction: Double, distance: Int, azimuth: Double = 0.0) {
    private val logger = KotlinLogging.logger {}

    private val motionProfile = MotionProfile(DT_MS, T1_MS, T2_MS, V_PROG, distance)
    private val drive = robotComponents.swerveDrive()
    private val notifier = Notifier(this::updateDrive)

    private val ticksPerSecMax = drive.wheels[0].driveSetpointMax * 10.0
    private val forwardComponent = Math.cos(Math.toRadians(direction)) / ticksPerSecMax
    private val strafeComponent = Math.sin(Math.toRadians(direction)) / ticksPerSecMax


    init {
        drive.setDriveMode(SwerveDrive.DriveMode.CLOSED_LOOP)
        logger.debug {
            "INIT motion, direction = $direction, distance = $distance,\n" +
                    "ticks/sec max = $ticksPerSecMax\n" +
                    "forward = $forwardComponent, strafe = $strafeComponent\n"
        }
    }

    val isFinished
        get() = motionProfile.isFinished

    fun start() {
        notifier.startPeriodic(DT_MS / 1000.0)
        logger.info("START motion, gyro angle = {}", drive.gyro.angle)
    }

    fun stop() {
        notifier.stop()
        drive.drive(0.0, 0.0, 0.0)
        logger.info("FINISH motion")
    }


    fun updateDrive() {
        motionProfile.calculate()
        val forward = forwardComponent * motionProfile.currVel
        val strafe = strafeComponent * motionProfile.currVel
        val azimuth = 0.0
        drive.drive(forward, strafe, azimuth)

    }

}