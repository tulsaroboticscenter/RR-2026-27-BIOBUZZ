package org.firstinspires.ftc.teamcode.pathingauto;

import org.firstinspires.ftc.teamcode.pedro.Constants;

// ftc robotcore
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

/*

    Welcome! This is a template Pedro auto designed as a
    foundation for real Pedro Pathing autos. It should not
    be run as a real OpMode. Please refer to the Pedro Pathing
    docs for more assistance.

 */

@Autonomous
@Disabled
public class PedroTemplate extends OpMode {
    private Follower follower;
    private final PoseFactory poseFactory = PoseFactory.degrees();

    // Pose declarations belong here.
    // For example (replacing null value):
    private final Pose examplePose = null; // = p.of(x, y, heading);
    private final Pose examplePose2 = null; // = p.of(x, y, heading);
    private final Pose exampleControlPose = null; // = p.of(x, y, heading);

    private Path examplePath() {
        // This creates a line between two poses on the field.
        return line(examplePose, examplePose2).linear(examplePose, examplePose2);
    }

    private Path exampleCurvePath() {
        // This creates a curve using three poses on the field.
        return curve(examplePose, examplePose2, exampleControlPose).linear(examplePose, exampleControlPose);
    }

    private Command autoRoutine() {
        return sequential(
                // Ideally, mechanism commands are also added here.
                follow(follower, examplePath())
        );
    }

    @Override
    public void init() {
        Scheduler.reset();

        follower = Constants.create(hardwareMap);
        follower.setPose(examplePose); // This should be replaced with the actual starting pose.
        follower.update();
    }

    @Override
    public void start() {
        schedule(autoRoutine());
    }

    @Override
    public void loop() {
        follower.update();
        Scheduler.execute();
        // Ideally, other loop methods are added here.

        telemetry.addData("X", follower.pose().x());
        telemetry.addData("Y", follower.pose().y());
        telemetry.addData("Heading", Math.toDegrees(follower.pose().heading()));
        telemetry.addData("Follower Mode", follower.mode());
        telemetry.update();
    }
}
