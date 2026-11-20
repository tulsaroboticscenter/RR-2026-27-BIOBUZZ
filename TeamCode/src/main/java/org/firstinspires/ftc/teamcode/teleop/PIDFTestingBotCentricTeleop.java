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

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;

@TeleOp(name = "PIDF Testing - Not a Competition OpMode", group = "Testing")
public class PIDFTestingBotCentricTeleop extends OpMode {
    // Declare members
    // DcMotorEx flywheel;
    DcMotorEx flywheel2;
    DcMotorEx intake;
    DcMotorEx intake2;

    double highFlywheelVelocity = 1500;
    double lowFlywheelVelocity = 900;
    double curTargetVelocity = highFlywheelVelocity;

    double F = 0;
    double P = 0;

    double[] stepSizes = {
            10.0,
            1.0,
            0.1,
            0.001,
            0.0001
    };

    int stepIndex = 1;

    @Override
    public void init() {
        // Map motors to assigned driver hub names
        // flywheel = hardwareMap.get(DcMotorEx.class, "flywheel");
        flywheel2 = hardwareMap.get(DcMotorEx.class, "flywheel2");
        intake = hardwareMap.get(DcMotorEx.class, "intake");
        intake2 = hardwareMap.get(DcMotorEx.class, "intake2");

        // Reverse the motor directions
        // flywheel.setDirection(DcMotorEx.Direction.REVERSE);
        flywheel2.setDirection(DcMotorEx.Direction.FORWARD);
        intake.setDirection(DcMotorSimple.Direction.FORWARD);
        intake2.setDirection(DcMotor.Direction.REVERSE);

        // Run the flywheel motors using the encoder
        // flywheel.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        flywheel2.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        intake.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // PIDF Tuning
        PIDFCoefficients pidfCoefficients = new PIDFCoefficients(P, 0, 0, F);
        flywheel2.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, pidfCoefficients);
    }

    @Override
    public void loop() {
        // Adds telemetry lines at the top, indicating instructions
        telemetry.addLine("You are tuning PIDF.");
        telemetry.addLine("The robot will not move.");
        telemetry.addLine(" ");

        // PIDF FLYWHEEL CODE
        // get all our gamepad commands
        // set target velocity
        // update telemetry

        if (gamepad1.xWasPressed()) {
            if (curTargetVelocity == highFlywheelVelocity) {
                curTargetVelocity = lowFlywheelVelocity;
            } else {
                curTargetVelocity = highFlywheelVelocity;
            }
        }

        if (gamepad1.bWasPressed()) {
            stepIndex = (stepIndex + 1) % stepSizes.length;
        }

        if (gamepad1.dpadLeftWasPressed()) {
            F -= stepSizes[stepIndex];
        }

        if (gamepad1.dpadRightWasPressed()) {
            F += stepSizes[stepIndex];
        }

        if (gamepad1.dpadDownWasPressed()) {
            P += stepSizes[stepIndex];
        }

        if (gamepad1.dpadUpWasPressed()) {
            P -= stepSizes[stepIndex];
        }

        // Intake
        if (gamepad1.a) {
            // intake.setVelocity(250);
            intake2.setVelocity(400);
        } else {
            // intake.setPower(0);
            // intake.setPower(0);
            intake2.setVelocity(0);
        }

        // Set new PIDF coefficients
        PIDFCoefficients pidfCoefficients = new PIDFCoefficients(P, 0, 0, F);
        flywheel2.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, pidfCoefficients);

        // Set velocity
        // flywheel.setVelocity(curTargetVelocity);
        flywheel2.setVelocity(curTargetVelocity);

        // double curVelocity = flywheel.getVelocity();
        double curVelocity = flywheel2.getVelocity();
        double error = curTargetVelocity - curVelocity;

        telemetry.addData("Target Velocity", curTargetVelocity);
        telemetry.addData("Current Velocity 1", "%.2f", curVelocity);
        // telemetry.addData("Current Velocity 1", "%.2f", curVelocity2);
        telemetry.addData("Error", "%.2f", error);
        telemetry.addLine("--------");
        telemetry.addData("Tuning P", "%.4f (D-Pad U/D)", P);
        telemetry.addData("Tuning F", "%.4f (D-Pad L/R)", F);
        telemetry.addData("Step Size", "%.4f (B Button)", stepSizes[stepIndex]);
        telemetry.addLine("--------");
        telemetry.addData("Intake Vel 1", intake.getVelocity());
        telemetry.addData("Intake Vel 2", intake2.getVelocity());
    }
}
