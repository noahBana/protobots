package org.strykeforce.motion

import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.failure
import com.github.kittinunf.result.success
import com.squareup.moshi.Moshi
import edu.wpi.first.wpilibj.Notifier
import mu.KotlinLogging
import org.strykeforce.robotComponents
import org.strykeforce.thirdcoast.swerve.SwerveDrive

const val POST = "http://192.168.3.208:5000/load"
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

    private val jsonAdapter = ActivityJsonAdapter(Moshi.Builder().build())
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
        activity.profileTicks = distance
        activity.meta["tags"] = listOf("jif", "magic")
    }

    val isFinished
        get() = motionProfile.isFinished && Math.abs(positionError()) < GOOD_ENOUGH

    val distance: Double
        get() {
            var distance = 0.0
            for (i in 0..3) distance += Math.abs(drive.wheels[i].driveTalon.getSelectedSensorPosition(0) - start[i])
            return distance / 4.0
        }

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
        activity.actualTicks = distance.toInt()

        val (_, _, result) = POST.httpPost()
            .header("content-type" to "application/json")
            .body(jsonAdapter.toJson(activity))
            .responseString()

        result.success { logger.info(result.get()) }

        result.failure { logger.error("error sending data to database", result.component2()) }

    }

    fun updateDrive() {
        motionProfile.calculate()
        val velocity = motionProfile.currVel + K_P * positionError()
        val forward = forwardComponent * velocity
        val strafe = strafeComponent * velocity
        val azimuth = 0.0
        drive.drive(forward, strafe, azimuth)
        // milliseconds, profile_acceleration, profile_velocity, profile_ticks, actual_ticks, forward, strafe, azimuth
        activity.data.add(
            listOf(
                motionProfile.iteration * DT_MS.toDouble(),
                motionProfile.currAcc,
                motionProfile.currVel,
                motionProfile.currPos,
                velocity,
                distance,
                forward,
                strafe,
                azimuth
            )
        )
    }

    fun positionError() = distance - motionProfile.currPos
}
