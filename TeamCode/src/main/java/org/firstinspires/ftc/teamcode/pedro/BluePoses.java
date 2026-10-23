package org.firstinspires.ftc.teamcode.pedro;

import com.pedropathing.api.PoseFactory;
import com.pedropathing.math.Pose;

public class BluePoses {
    public static final PoseFactory poseFactory = PoseFactory.degrees();

    public static final Pose byLoadingZonePose = poseFactory.of(133.656, 64.0109, 180); // A bit to the right of the blue loading zone.
    public static final Pose firstscorePose = poseFactory.of(84.576, 130.993, 270); // The straight scoring position straight ahead of the blue hive.
    public static final Pose parkRightPose = poseFactory.of(129.071, 45.753, 180); // Parked in the blue loading zone.
    public static final Pose parkLeftPose = poseFactory.of(129.071, 23.160, 180); // Parked in the blue loading zone.
}
