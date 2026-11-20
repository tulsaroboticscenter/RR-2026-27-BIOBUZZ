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
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;

@TeleOp(name = "Bot Centric Teleop", group = "Drive")
public class BotCentricTeleop extends OpMode {
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

    double flywheel1Vel = 0;
    double flywheel2Vel = 0;
    double averageVel = 0;
    boolean isShooting = false;

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

        // Brake all drive motors when stopped to reduce drift
        frontLeftDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRightDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeftDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRightDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    @Override
    public void loop() {
        // Adds telemetry lines at the top, indicating instructions
        telemetry.addLine("You are in ROBOT CENTRIC");
        telemetry.addLine("The left joystick sets the robot direction");
        telemetry.addLine("Moving the right joystick left and right turns the robot");
        telemetry.addLine("Hold left trigger to activate intake inwards, press the bumper in front of it to reverse");
        telemetry.addLine("Hold right trigger to activate flywheel for shooting");
        telemetry.addLine("Press B to toggle the gate servo position.");
        telemetry.addLine(" ");

        // Get flywheel data
        flywheel1Vel = flywheel.getVelocity();
        flywheel2Vel = flywheel2.getVelocity();
        averageVel = (flywheel1Vel + flywheel2Vel) / 2;

        // Intake code
        if (gamepad1.left_trigger_pressed || (gamepad1.left_bumper && gamepad1.left_trigger_pressed)) {
            // Move elements inside intake but outside the transfer
            intake.setPower(1);
            intake2.setPower(-1);
        } else if (gamepad1.left_bumper) {
            // Eject elements in intake
            intake.setPower(-1);
            intake2.setPower(-1);
        } else if (gamepad1.a) {
            // Move elements from transfer to flywheel
            isShooting = true;
            flywheel.setVelocity(1800);
            flywheel2.setVelocity(1800);
            intake.setPower(1);
            intake2.setPower(1);
        } else {
            // Turn all motors associated off
            isShooting = false;
            intake.setPower(0);
            intake2.setPower(0);
        }

        if (!isShooting) {
            flywheel.setVelocity(1550);
            flywheel2.setVelocity(1550);
        }

        drive(-gamepad1.left_stick_y, gamepad1.left_stick_x * 1.1, gamepad1.right_stick_x);
        updateTelemetry();
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
        telemetry.addData("Front left/Right", "%4.2f, %4.2f", frontLeftDrive.getPower(), frontRightDrive.getPower());
        telemetry.addData("Back  left/Right", "%4.2f, %4.2f", backLeftDrive.getPower(), backRightDrive.getPower());
        telemetry.addData("Current - front left/Right", "%4.2f, %4.2f", frontLeftDrive.getCurrent(CurrentUnit.MILLIAMPS), frontRightDrive.getCurrent(CurrentUnit.MILLIAMPS));
        telemetry.addData("Current - back left/Right", "%4.2f, %4.2f", backLeftDrive.getCurrent(CurrentUnit.MILLIAMPS), backRightDrive.getCurrent(CurrentUnit.MILLIAMPS));
        telemetry.addData("Current - intake 1", "%4.2f, %4.2f", intake.getCurrent(CurrentUnit.MILLIAMPS));
        // telemetry.addData("Pollen Blocked", servoSwitch);
        // telemetry.addData("Servo Position", servo.getPosition());
        telemetry.addData("Flywheel 1 Velocity", flywheel1Vel);
        telemetry.addData("Flywheel 2 Velocity", flywheel2Vel);
        telemetry.addData("Average Flywheel Velocity", flywheel2Vel);
        telemetry.addData("isShooting?", isShooting);
        telemetry.update();
    }
}
