package org.firstinspires.ftc.teamcode.pedro;

import com.pedropathing.api.PoseFactory;
import com.pedropathing.math.Pose;

public class RedPoses {
    public static final PoseFactory poseFactory = PoseFactory.degrees();

    public static final Pose byLoadingZonePose = poseFactory.of(8.2879, 64.0109, 0); // A bit to the right of the red loading zone.
    public static final Pose firstscorePose = poseFactory.of(55.9872, 8.1845, 90); // The straight scoring position straight ahead of the red hive.
    public static final Pose parkRightPose = poseFactory.of(11.7064, 91.7518, 0); // Parked in the red loading zone.
    public static final Pose parkLeftPose = poseFactory.of(11.7064, 120.873, 0); // Parked in the red loading zone.
}
