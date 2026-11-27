package org.firstinspires.ftc.teamcode.pathingauto;

import static com.pedropathing.api.Paths.line;
import static com.pedropathing.ivy.Scheduler.schedule;
import static com.pedropathing.ivy.groups.Groups.sequential;
import static com.pedropathing.ivy.pedro.PedroCommands.follow;
import static org.firstinspires.ftc.teamcode.pedro.Poses.blueHive;
import static org.firstinspires.ftc.teamcode.pedro.Poses.blueLoadingZoneCorner;
import static org.firstinspires.ftc.teamcode.pedro.Poses.blueStart;
import static org.firstinspires.ftc.teamcode.pedro.Poses.byLoadingZonePose;
import static org.firstinspires.ftc.teamcode.pedro.Poses.redHive;
import static org.firstinspires.ftc.teamcode.pedro.Poses.redLoadingZoneCorner;
import static org.firstinspires.ftc.teamcode.pedro.Poses.start;

import com.pedropathing.follower.Follower;
import com.pedropathing.ivy.Command;
import com.pedropathing.ivy.Scheduler;
import com.pedropathing.paths.Path;
import com.pedropathing.utils.Timer;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.teamcode.pedro.Constants;

import java.sql.Time;

@Autonomous(name = "Default Blue Pathing Auto", group = "Pedro Auto")
public class DefaultBluePathingAuto extends OpMode {
    // Declare timers
    public Timer runtime = new Timer();
    public Timer flywheelTimer = new Timer();
    private Follower follower;

    // Declare OpMode members
    DcMotorEx flywheel;
    DcMotorEx flywheel2;
    DcMotor intake;
    DcMotor intake2;

    public Path startToScore() {
        return line(blueStart, blueHive).linear(blueStart, blueHive);
    }

    public Path scoreToPark() {
        return line(blueHive, blueLoadingZoneCorner).linear(blueHive, blueLoadingZoneCorner);
    }

    private Command autoRoutine() {
        return sequential(
                follow(follower, startToScore()),
                shootPollen,
                follow(follower, scoreToPark())
        );
    }

    public Command shootPollen = Command.build()
            .setStart(() -> {
                flywheelTimer.reset();
            })
            .setExecute(() -> {
                flywheel.setVelocity(2500);
                flywheel2.setVelocity(2500);
                if (flywheelTimer.seconds() > 1.0) {
                    intake.setPower(1);
                    intake2.setPower(1);
                }
            })
            .setDone(() -> flywheelTimer.seconds() > 2.75)
            .setEnd(endCondition -> {
                flywheel.setVelocity(0);
                flywheel2.setVelocity(0);
                intake.setPower(0);
                intake2.setPower(0);
            })
            .requiring(flywheel, intake, intake2);

    public Command activateIntake;

    @Override
    public void init() {
        // Link members to configuration name
        flywheel = hardwareMap.get(DcMotorEx.class, "flywheel");
        flywheel2 = hardwareMap.get(DcMotorEx.class, "flywheel2");
        intake = hardwareMap.get(DcMotorEx.class, "intake");
        intake2 = hardwareMap.get(DcMotor.class, "intake2");

        Scheduler.reset();

        follower = Constants.create(hardwareMap);
        follower.setPose(byLoadingZonePose);
        follower.update();
    }

    @Override
    public void start() {
        runtime.reset();
        schedule(autoRoutine());
        flywheel.setVelocity(2000);
    }

    @Override
    public void loop() {
        follower.update();
        Scheduler.execute();

        telemetry.addData("Runtime", runtime.seconds());
        telemetry.addData("X", follower.pose().x());
        telemetry.addData("Y", follower.pose().y());
        telemetry.addData("Heading", Math.toDegrees(follower.pose().heading()));
        telemetry.addData("Follower Mode", follower.mode());
        telemetry.update();
    }
}
