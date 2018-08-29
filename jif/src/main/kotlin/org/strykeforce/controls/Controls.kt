package org.strykeforce.controls

import edu.wpi.first.wpilibj.Joystick
import edu.wpi.first.wpilibj.buttons.JoystickButton
import org.strykeforce.command.ZeroGyroCommand
import javax.inject.Inject
import javax.inject.Singleton

/** Accesses driver config input.  */
@Singleton
class Controls @Inject constructor() {

    private val driverController = Joystick(1)

    init {
        JoystickButton(driverController, Switch.RESET.index).whenPressed(ZeroGyroCommand())
    }

    val forward: Double
        get() = -driverController.getRawAxis(Axis.LEFT_Y.index)

    val strafe: Double
        get() = driverController.getRawAxis(Axis.LEFT_X.index)

    val azimuth: Double
        get() = driverController.getRawAxis(Axis.RIGHT_X.index)
}

enum class Shoulder constructor(val index: Int) {
    RIGHT(2),
    LEFT_DOWN(4),
    LEFT_UP(5)
}

enum class Switch constructor(val index: Int) {
    LEFT(1),
    RESET(3)
}

enum class Trim constructor(val index: Int) {
    LEFT_X_POS(7),
    LEFT_X_NEG(6),
    LEFT_Y_POS(8),
    LEFT_Y_NEG(9),
    RIGHT_Y_POS(10),
    RIGHT_Y_NEG(11),
    RIGHT_X_POS(12),
    RIGHT_X_NEG(13)
}

enum class Axis constructor(val index: Int) {
    RIGHT_X(0),
    RIGHT_Y(1),
    LEFT_X(5),
    LEFT_Y(2),
    TUNER(6)
}
