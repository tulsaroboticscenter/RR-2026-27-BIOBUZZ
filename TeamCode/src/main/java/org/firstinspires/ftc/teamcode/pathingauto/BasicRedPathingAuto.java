package org.firstinspires.ftc.teamcode.pathingauto;

import static com.pedropathing.api.Paths.line;
import static com.pedropathing.ivy.Scheduler.schedule;
import static com.pedropathing.ivy.groups.Groups.sequential;
import static com.pedropathing.ivy.pedro.PedroCommands.follow;

import com.pedropathing.utils.Timer;
import com.pedropathing.follower.Follower;
import com.pedropathing.ivy.Command;
import com.pedropathing.ivy.Scheduler;
import com.pedropathing.paths.Path;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.pedro.Constants;
import static org.firstinspires.ftc.teamcode.pedro.RedPoses.*;

@Autonomous(name = "Basic Red Pathing Auto", group = "Pedro Auto")
@Disabled
public class BasicRedPathingAuto extends OpMode {
    public Timer runtime;
    private Follower follower;

    public Path startToScore() {
        return line(byLoadingZonePose, firstscorePose).linear(byLoadingZonePose, firstscorePose);
    }

    public Path scoreToPark() {
        return line(firstscorePose, parkRightPose).linear(firstscorePose, parkRightPose);
    }

    private Command autoRoutine() {
        return sequential(
                follow(follower, startToScore()),
                follow(follower, scoreToPark())
        );
    }

    @Override
    public void init() {
        Scheduler.reset();

        follower = Constants.create(hardwareMap);
        follower.setPose(byLoadingZonePose);
        follower.update();
    }

    @Override
    public void start() {
        runtime.reset();
        schedule(autoRoutine());
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
