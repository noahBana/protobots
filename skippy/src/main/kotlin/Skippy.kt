package org.strykeforce

import dagger.BindsInstance
import dagger.Component
import edu.wpi.first.wpilibj.DriverStation
import edu.wpi.first.wpilibj.TimedRobot
import mu.KotlinLogging
import org.example.swerve.controls.Controls
import org.example.swerve.controls.Trigger
import org.strykeforce.thirdcoast.swerve.GyroModule
import org.strykeforce.thirdcoast.swerve.SwerveDrive
import org.strykeforce.thirdcoast.swerve.WheelModule
import java.io.File
import java.net.URL
import javax.inject.Singleton

const val CONFIG = "/home/lvuser/swerve.toml"
const val DEFAULT_CONFIG = "/META-INF/settings.toml"
const val DEADBAND = 0.05

class Skippy : TimedRobot() {

    private val logger = KotlinLogging.logger {}

    private val components = robotComponents()
    private lateinit var controls: Controls
    private lateinit var gyroResetButton: Trigger
    private lateinit var swerveDrive: SwerveDrive

    override fun robotInit() {
        controls = components.controls()
        gyroResetButton = controls.resetButton
        swerveDrive = components.swerveDrive()
        swerveDrive.zeroAzimuthEncoders()
    }

    override fun teleopInit() {
        swerveDrive.stop()
    }

    override fun teleopPeriodic() {
        if (gyroResetButton.hasActivated()) {
            val msg = "Resetting gyro yaw zero"
            logger.warn(msg)
            DriverStation.reportWarning(msg, false)
            swerveDrive.getGyro().zeroYaw()
        }
        val forward = controls.forward.applyDeadband(DEADBAND)
        val strafe = controls.strafe.applyDeadband(DEADBAND)
        val azimuth = controls.azimuth.applyDeadband(DEADBAND)

        swerveDrive.drive(forward, strafe, azimuth)
    }

    private fun robotComponents(): RobotComponents {
        var config: URL = this.javaClass.getResource(DEFAULT_CONFIG)

        val f = File(CONFIG)
        if (f.exists() && !f.isDirectory) config = f.toURI().toURL()

        logger.info("reading settings from '{}'", config)
        return DaggerRobotComponents.builder().config(config).build()
    }
}

private fun Double.applyDeadband(amount: Double) = if (Math.abs(this) < amount) 0.0 else this


@Singleton
@Component(modules = [GyroModule::class, WheelModule::class])
interface RobotComponents {
    fun controls(): Controls
    fun swerveDrive(): SwerveDrive

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun config(config: URL): Builder

        fun build(): RobotComponents
    }
}