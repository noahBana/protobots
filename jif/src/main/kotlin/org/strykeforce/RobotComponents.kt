package org.strykeforce

import dagger.BindsInstance
import dagger.Component
import org.strykeforce.controls.Controls
import org.strykeforce.subsystem.DriveSubsystem
import org.strykeforce.thirdcoast.swerve.GyroModule
import org.strykeforce.thirdcoast.swerve.SwerveDrive
import org.strykeforce.thirdcoast.swerve.WheelModule
import org.strykeforce.thirdcoast.telemetry.NetworkModule
import org.strykeforce.thirdcoast.telemetry.TelemetryService
import java.io.File
import java.net.URL
import javax.inject.Singleton

const val CONFIG = "/home/lvuser/jif.toml"
const val DEFAULT_CONFIG = "/META-INF/settings.toml"

val robotComponents: RobotComponents by lazy {
    val f = File(CONFIG)
    val config = if (f.exists()) f.toURI().toURL() else loadResource(DEFAULT_CONFIG)
    DaggerRobotComponents.builder().config(config).build()
}

@Singleton
@Component(modules = [GyroModule::class, WheelModule::class, NetworkModule::class])
interface RobotComponents {
    fun controls(): Controls
    fun driveSubsystem(): DriveSubsystem
    fun swerveDrive(): SwerveDrive
    fun telemetryService(): TelemetryService

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun config(config: URL): Builder

        fun build(): RobotComponents
    }
}

private fun loadResource(resource: String): URL =
    try {
        object {}.javaClass.getResource(resource)
    } catch (all: Exception) {
        throw RuntimeException("Failed to load resource=$resource!", all)
    }
