package org.strykeforce.motion

import edu.wpi.first.wpilibj.Notifier
import mu.KotlinLogging
import org.strykeforce.robotComponents
import org.strykeforce.thirdcoast.swerve.SwerveDrive

const val K_P = -0.64
const val GOOD_ENOUGH = 2500

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

    private val start = IntArray(4)

    private var activity = Activity("Magic on Jif")


    init {
        drive.setDriveMode(SwerveDrive.DriveMode.CLOSED_LOOP)
        logger.debug {
            "INIT motion, direction = $direction, distance = $distance,\n" +
                    "ticks/sec max = $ticksPerSecMax\n" +
                    "forward = $forwardComponent, strafe = $strafeComponent\n"
        }

        activity.meta["dt"] = DT_MS.toString()
        activity.meta["t1"] = T1_MS.toString()
        activity.meta["t2"] = T2_MS.toString()
        activity.meta["vProg"] = V_PROG.toInt().toString()
        activity.meta["direction"] = direction.toString()
        activity.meta["azimuth"] = azimuth.toString()
        activity.meta["tags"] = listOf("jif", "magic")

        activity.activityData.add(distance.toDouble())
    }

    val isFinished
        get() = motionProfile.isFinished && Math.abs(positionError()) < GOOD_ENOUGH

    val actualDistance: Double
        get() {
            var distance = 0.0
            for (i in 0..3) distance += Math.abs(drive.wheels[i].driveTalon.getSelectedSensorPosition(0) - start[i])
            return distance / 4.0
        }

    val actualVelocity: Int
        get() = drive.wheels[0].driveTalon.getSelectedSensorVelocity(0)

    fun start() {
        notifier.startPeriodic(DT_MS / 1000.0)
        logger.info("START motion, gyro angle = {}", drive.gyro.angle)
        activity.meta["gyroStart"] = drive.gyro.angle.toString()

        for (i in 0..3) start[i] = drive.wheels[i].driveTalon.getSelectedSensorPosition(0)
    }

    fun stop() {
        notifier.stop()
        drive.drive(0.0, 0.0, 0.0)
        logger.info("FINISH motion position = {}", motionProfile.currPos)
        activity.meta["gyroEnd"] = drive.gyro.angle.toString()
        activity.activityData.add(actualDistance)
        activity.activityData.add(0.0) // actual_distance measured physically

        activity.upload()
    }

    fun updateDrive() {
        motionProfile.calculate()
        val velocity = motionProfile.currVel + K_P * positionError()
        val forward = forwardComponent * velocity
        val strafe = strafeComponent * velocity
        val azimuth = 0.0
        drive.drive(forward, strafe, azimuth)
        activity.traceData.add(
            listOf(
                motionProfile.iteration * DT_MS.toDouble(), // millis
                motionProfile.currAcc,     // profile_acc
                motionProfile.currVel,     // profile_vel
                velocity,                  // setpoint_vel
                actualVelocity.toDouble(), // actual_vel
                motionProfile.currPos,     // profile_ticks
                actualDistance,            // actual_ticks
                forward,  // forward
                strafe,   // strafe
                azimuth   // azimuth
            )
        )
    }

    fun positionError() = actualDistance - motionProfile.currPos
}
