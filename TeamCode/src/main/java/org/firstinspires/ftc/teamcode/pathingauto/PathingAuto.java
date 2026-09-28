package org.firstinspires.ftc.teamcode.pathingauto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

// pedro pathing
import com.pedropathing.follower.Follower;
import static com.pedropathing.api.Paths.*;
import com.pedropathing.api.PoseFactory;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;

// ivy
import com.pedropathing.ivy.Command;
import com.pedropathing.ivy.Scheduler;
import static com.pedropathing.ivy.Scheduler.schedule;
import static com.pedropathing.ivy.groups.Groups.sequential;
import static com.pedropathing.ivy.pedro.PedroCommands.follow;

import org.firstinspires.ftc.teamcode.pedro.Constants;

@Autonomous(name = "SamplePedroAuto", group = "null")
@Disabled
public class PathingAuto extends OpMode {
    private Follower follower;
    private final PoseFactory p = PoseFactory.degrees();

    private final Pose startPose = p.of(24, 24, 0);
    private final Pose park = p.of(48, 48, 90);
    private final Pose controlPose = p.of(36, 60, 45);

    // OUR PATHS ARE UNDER OUR POSES - max, 2026

    private Path move() {
        return line(startPose, controlPose).linear(startPose, controlPose);
    }

    private Path straightPark() {
        return line(startPose, park).linear(startPose, park);
    }

    private Path park() {
        return curve(startPose, controlPose, park).linear(startPose, park);
    }

    private Command autoRoutine() {
        return sequential(
                follow(follower, move()),
                follow(follower, park())
        );
    }

    @Override
    public void init() {
        Scheduler.reset();

        follower = Constants.create(hardwareMap);
        follower.setPose(startPose);
    }

    @Override
    public void start() {
        schedule(autoRoutine());
    }

    @Override
    public void loop() {
        follower.update();
        Scheduler.execute();

        telemetry.addData("X", follower.pose().x());
        telemetry.addData("Y", follower.pose().y());
        telemetry.addData("Heading", Math.toDegrees(follower.pose().heading()));
        telemetry.addData("Follower Mode", follower.mode());
        telemetry.update();
    }
}
