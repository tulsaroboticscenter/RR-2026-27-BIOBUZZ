/* Copyright (c) 2025 FIRST. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp(name = "Bot Centric Teleop", group = "Drive")
public class StableBotCentricTeleop extends OpMode {
    private final ElapsedTime runtime = new ElapsedTime();

    // Declare members
    DcMotorEx frontLeftDrive;
    DcMotorEx frontRightDrive;
    DcMotorEx backLeftDrive;
    DcMotorEx backRightDrive;
    DcMotorEx flywheel;
    DcMotorEx flywheel2;
    DcMotorEx intake;
    DcMotor intake2;
    Servo servo;
    Servo servoGate;

    // Gate servo positions (0.0 - 1.0 = 0 - 180 degrees). Tune these on the robot.
    static final double GATE_DOWN = 0.1; // blocks balls from entering the shooter
    static final double GATE_UP = 0.6;   // lets balls into the shooter

    double flywheel1Vel = 0;
    double flywheel2Vel = 0;
    double averageVel = 0;
    boolean isShooting = false;

    // ---- Shooter settings (tuned with PIDFTestingBotCentricTeleop) ----
    // Target flywheel speed in ticks/second. Both flywheels always spin at this speed.
    static final double SHOOT_VELOCITY = 1415;
    // How close (ticks/second) the average flywheel speed must be before we feed a ball.
    static final double VELOCITY_TOLERANCE = 50;
    // true = the transfer only feeds when the flywheels are up to speed
    static final boolean FEED_ONLY_WHEN_READY = false;
    // Fill in P and F from the PIDF tuning OpMode, then set USE_CUSTOM_PIDF = true.
    // Leave false until then: P = 0 and F = 0 would give the flywheels no power at all.
    static final boolean USE_CUSTOM_PIDF = false;
    static final double FLYWHEEL_P = 0;
    static final double FLYWHEEL_F = 0;

    @Override
    public void init() {
        // Map motors to assigned driver hub names
        frontLeftDrive = hardwareMap.get(DcMotorEx.class, "front_left_drive");
        frontRightDrive = hardwareMap.get(DcMotorEx.class, "front_right_drive");
        backLeftDrive = hardwareMap.get(DcMotorEx.class, "back_left_drive");
        backRightDrive = hardwareMap.get(DcMotorEx.class, "back_right_drive");
        flywheel = hardwareMap.get(DcMotorEx.class, "flywheel");
        flywheel2 = hardwareMap.get(DcMotorEx.class, "flywheel2");
        intake = hardwareMap.get(DcMotorEx.class, "intake");
        intake2 = hardwareMap.get(DcMotor.class, "intake2");
        servo = hardwareMap.get(Servo.class, "servo");
        servoGate = hardwareMap.get(Servo.class, "servoGate");

        // Gate starts down (blocking)
        servoGate.setPosition(GATE_DOWN);

        // Reverse the motor directions on the left side
        backLeftDrive.setDirection(DcMotor.Direction.REVERSE);
        frontLeftDrive.setDirection(DcMotor.Direction.REVERSE);
        flywheel.setDirection(DcMotor.Direction.REVERSE);
        intake.setDirection(DcMotorSimple.Direction.FORWARD);
        intake2.setDirection(DcMotor.Direction.REVERSE);

        // These lines should be removed if we aren't using the encoder
        frontLeftDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        frontRightDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backLeftDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backRightDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        flywheel.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        flywheel2.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // Apply tuned PIDF to both flywheels (same coefficients the tuning OpMode found)
        if (USE_CUSTOM_PIDF) {
            PIDFCoefficients pidf = new PIDFCoefficients(FLYWHEEL_P, 0, 0, FLYWHEEL_F);
            flywheel.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, pidf);
            flywheel2.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, pidf);
        }

        // Brake all drive motors when stopped to reduce drift
        frontLeftDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRightDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeftDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRightDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    @Override
    public void start() {
        // Turn the shooter on as soon as START is pressed; it stays on until STOP
        flywheel.setVelocity(SHOOT_VELOCITY);
        flywheel2.setVelocity(SHOOT_VELOCITY);
        runtime.reset();
    }

    @Override
    public void stop() {
        // Shut everything off when STOP is pressed
        flywheel.setVelocity(0);
        flywheel2.setVelocity(0);
        intake.setPower(0);
        intake2.setPower(0);
    }

    @Override
    public void loop() {
        // Adds telemetry lines at the top, indicating instructions
        telemetry.addLine("You are in ROBOT CENTRIC");
        telemetry.addLine("The left joystick sets the robot direction");
        telemetry.addLine("Moving the right joystick left and right turns the robot");
        telemetry.addLine("Hold left trigger to activate intake inwards, press the bumper in front of it to reverse");
        telemetry.addLine("Flywheels spin up automatically. Hold A to raise the gate and feed balls into the shooter.");
        telemetry.addLine(" ");

        // Get flywheel data
        flywheel1Vel = flywheel.getVelocity();
        flywheel2Vel = flywheel2.getVelocity();
        averageVel = (flywheel1Vel + flywheel2Vel) / 2;

        // Intake / shooter code. Shooting (A) takes priority over the other intake buttons.
        if (gamepad1.a) {
            // Open the gate and run all intakes toward the shooter while A is held
            isShooting = true;
            servoGate.setPosition(GATE_UP);
            if (!FEED_ONLY_WHEN_READY || flywheelsReady()) {
                intake.setPower(1);
                intake2.setPower(1);
            } else {
                intake.setPower(0);
                intake2.setPower(0);
            }
        } else if (gamepad1.left_trigger_pressed || (gamepad1.left_bumper && gamepad1.left_trigger_pressed)) {
            // Move elements inside intake but outside the transfer
            isShooting = false;
            servoGate.setPosition(GATE_DOWN);
            intake.setPower(1);
            intake2.setPower(-1);
        } else if (gamepad1.left_bumper) {
            // Eject elements in intake
            isShooting = false;
            servoGate.setPosition(GATE_DOWN);
            intake.setPower(-1);
            intake2.setPower(-1);
        } else {
            // Gate returns to the blocking position; turn all motors associated off
            servoGate.setPosition(GATE_DOWN);
            isShooting = false;
            intake.setPower(0);
            intake2.setPower(0);
        }

        // Keep both flywheels at shooting speed at all times
        flywheel.setVelocity(SHOOT_VELOCITY);
        flywheel2.setVelocity(SHOOT_VELOCITY);

        drive(-gamepad1.left_stick_y, gamepad1.left_stick_x * 1.1, gamepad1.right_stick_x);
        updateTelemetry();
    }

    private boolean flywheelsReady() {
        return Math.abs(SHOOT_VELOCITY - flywheel1Vel) <= VELOCITY_TOLERANCE
                && Math.abs(SHOOT_VELOCITY - flywheel2Vel) <= VELOCITY_TOLERANCE;
    }

    public void drive(double forward, double right, double rotate) {
        double frontLeftPower = forward + right + rotate;
        double frontRightPower = forward - right - rotate;
        double backRightPower = forward + right - rotate;
        double backLeftPower = forward - right + rotate;

        double maxPower = 1.0;
        double maxSpeed = 1.0;

        // This is needed to make sure >1.0 is not passed to any wheel
        maxPower = Math.max(maxPower, Math.abs(frontLeftPower));
        maxPower = Math.max(maxPower, Math.abs(frontRightPower));
        maxPower = Math.max(maxPower, Math.abs(backRightPower));
        maxPower = Math.max(maxPower, Math.abs(backLeftPower));

        frontLeftDrive.setPower(maxSpeed * (frontLeftPower / maxPower));
        frontRightDrive.setPower(maxSpeed * (frontRightPower / maxPower));
        backLeftDrive.setPower(maxSpeed * (backLeftPower / maxPower));
        backRightDrive.setPower(maxSpeed * (backRightPower / maxPower));
    }

    public void updateTelemetry() {
        telemetry.addData("Status", "Run Time: " + runtime);
        telemetry.addData("Flywheel 1 Velocity", flywheel1Vel);
        telemetry.addData("Flywheel 2 Velocity", flywheel2Vel);
        telemetry.addData("Flywheel 1 Power", flywheel.getPower());
        telemetry.addData("Flywheel 2 Power", flywheel2.getPower());
        telemetry.addData("Average Flywheel Velocity", averageVel);
        telemetry.addData("Target Velocity", SHOOT_VELOCITY);
        telemetry.addData("Flywheels Ready?", flywheelsReady());
        telemetry.addData("Gate Position", servoGate.getPosition());
        telemetry.addData("isShooting?", isShooting);
        telemetry.update();
    }
}
