package org.strykeforce

import dagger.BindsInstance
import dagger.Component
import edu.wpi.first.wpilibj.DriverStation
import edu.wpi.first.wpilibj.TimedRobot
import mu.KotlinLogging
import org.strykeforce.controls.Controls
import org.strykeforce.thirdcoast.swerve.GyroModule
import org.strykeforce.thirdcoast.swerve.SwerveDrive
import org.strykeforce.thirdcoast.swerve.WheelModule
import java.io.File
import java.net.URL
import javax.inject.Singleton

const val CONFIG = "/home/lvuser/skippy.toml"
const val DEFAULT_CONFIG = "/META-INF/settings.toml"
const val SAVE_AZIMUTHS = "/home/lvuser/zero.me"

const val DEADBAND = 0.05

class Skippy : TimedRobot() {

    private val logger = KotlinLogging.logger {}

    private val components = robotComponents()
    private val controls = components.controls()
    private val swerveDrive = components.swerveDrive()
    private val gyroResetButton = controls.resetButton

    override fun robotInit() {
        // save azimuth zero positions by creating file named in SAVE_AZIMUTHS
        if (File(SAVE_AZIMUTHS).delete()) swerveDrive.saveAzimuthPositions()
        swerveDrive.zeroAzimuthEncoders()
    }

    override fun teleopInit() {
        swerveDrive.stop()
    }

    override fun teleopPeriodic() {
        val forward = controls.forward.applyDeadband(DEADBAND)
        val strafe = controls.strafe.applyDeadband(DEADBAND)
        val azimuth = controls.azimuth.applyDeadband(DEADBAND)

        swerveDrive.drive(forward, strafe, azimuth)

        if (gyroResetButton.isActivated) {
            swerveDrive.gyro.zeroYaw()
            "reset gyro zero".let {
                logger.warn(it)
                DriverStation.reportWarning(it, false)
            }
        }
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