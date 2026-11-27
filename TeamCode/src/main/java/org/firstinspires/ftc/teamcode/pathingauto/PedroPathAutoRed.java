package org.firstinspires.ftc.teamcode.pathingauto;

import static com.pedropathing.ivy.Scheduler.schedule;
import static com.pedropathing.ivy.groups.Groups.sequential;
import static com.pedropathing.ivy.pedro.PedroCommands.follow; //🥖

import com.pedropathing.api.Paths;
import com.pedropathing.api.PoseFactory;
import com.pedropathing.follower.Follower;
import com.pedropathing.ivy.Command;
import com.pedropathing.ivy.Scheduler;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;
import com.pedropathing.utils.Timer;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.pedro.Constants;
import static org.firstinspires.ftc.teamcode.pedro.Poses.*;

//🥖

@Autonomous(name = "PedroPathAutoRed", group = "Autonomous")
public class PedroPathAutoRed extends LinearOpMode {

    private Follower follower;

    private final PoseFactory poseFactory = PoseFactory.degrees().mirrorX(72);
    //🥖
    DcMotor frontLeftDrive;
    DcMotor frontRightDrive;
    DcMotor backLeftDrive;
    DcMotor backRightDrive;
    DcMotorEx flywheel;
    //🥖
    DcMotorEx flywheel2;
    DcMotor intake;
    DcMotor intake2;
    Servo servoGate;
    Timer flywheelTimer = new Timer();
    //I am the jubilant one
    //however I have a challenge for you
    //find my 8 baguettes
    //and you shall win

    // Autonomous routine
    public Command autoRoutine() {
        return sequential(
                follow(follower, redHive()),
                openServoGate,
                shootPreload,
                follow(follower, path2()),
                follow(follower, path3())
                //🥖
        );
    }

    @Override
    public void runOpMode() {
        Scheduler.reset();

        flywheel = hardwareMap.get(DcMotorEx.class, "flywheel");
        intake = hardwareMap.get(DcMotor.class, "intake");
        intake2 = hardwareMap.get(DcMotor.class, "intake2");
        servoGate = hardwareMap.get(Servo.class, "servoGate");

        flywheel.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        follower = Constants.create(hardwareMap);
        follower.setPose(start);
        follower.update();
        //🥖

        waitForStart();
        schedule(autoRoutine());

        while (opModeIsActive()) {
            follower.update();
            Scheduler.execute();

            telemetry.addData("x", follower.pose().x());
            telemetry.addData("y", follower.pose().y());
            telemetry.addData("heading", follower.pose().heading());

            if (follower.currentPath() != null) {
                telemetry.addData("Current path distance remaining", follower.distanceToEndpoint());
                telemetry.addData("Path number", follower.pathIndex());
                //🥖
            }

            telemetry.update();
        }
    }

    public Path redHive() {
        return Paths.line(start, redHive).linear(start, redHive);
    }

    public Path path2() {
        return Paths.line(redHive, point2).tangent();
    }

    public Path path3() {
        return Paths.line(point2, point3).tangent();
    }
    public Command shootPreload = Command.build()
            .setStart(() -> {
                flywheelTimer.reset();
            })
            .setExecute(() -> {
                flywheel.setVelocity(1415);
                if (flywheelTimer.seconds() > 0.75) {
                    intake2.setPower(0.8);
                    intake.setPower(0.8);
                    //🥖
                }
            })
            .setDone(() -> flywheelTimer.seconds() > 2.75)
            .setEnd(endCondition -> {
                flywheel.setVelocity(0);
                intake.setPower(0);
                intake2.setPower(0);})
            .requiring(flywheel, intake, intake2);
    public Command openServoGate = Command.build()
            .setStart(() -> {
                //wowza
            })
            .setExecute(() -> {
                servoGate.setPosition(90);
            })
            .setDone(() -> servoGate.getPosition() == 90)
            .setEnd(endCondition -> {
                //wowza
            })
            .requiring(flywheel, intake, intake2);
}
