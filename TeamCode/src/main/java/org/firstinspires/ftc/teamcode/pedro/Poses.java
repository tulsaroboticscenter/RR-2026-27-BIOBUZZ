package org.firstinspires.ftc.teamcode.pedro;

import com.pedropathing.api.PoseFactory;
import com.pedropathing.math.Pose;

public class Poses {
    public static final PoseFactory poseFactory = PoseFactory.degrees();

    public static final Pose byLoadingZonePose = poseFactory.of(133.656, 64.0109, 180); // A bit to the right of the blue loading zone.
    public static final Pose firstscorePose = poseFactory.of(84.576, 130.993, 270); // The straight scoring position straight ahead of the blue hive.
    public static final Pose parkRightPose = poseFactory.of(129.071, 45.753, 180); // Parked in the blue loading zone.
    public static final Pose parkLeftPose = poseFactory.of(129.071, 23.160, 180); // Parked in the blue loading zone.

    public static final Pose start = poseFactory.of(56, 8, 270);
    public static final Pose redHive = poseFactory.of(56, 27, 270);
    public static final Pose blueStart = poseFactory.of(82, 135, 90);
    public static final Pose blueHive = poseFactory.of(82, 109, 90);
    public static final Pose point2 = poseFactory.of(22.7501, 35.5613, -179.2889);
    public static final Pose point3 = poseFactory.of(13, 92.445, 100.7807);
    public static final Pose blueLoadingZoneCorner = poseFactory.of(130, 24, 0);
    public static final Pose blueLoadingZoneField = poseFactory.of(130, 46, 0);
    public static final Pose redLoadingZoneCorner = poseFactory.of(10, 118, 0);
    public static final Pose redLoadingZoneField = poseFactory.of(10, 95, 0);
    public static final Pose redGardenCorner = poseFactory.of(23, 22, 0);
    public static final Pose redOtherCorner = poseFactory.of(23, 118, 0);
    public static final Pose blueGardenCorner = poseFactory.of(118, 119, 0);
    public static final Pose blueOtherCorner = poseFactory.of(118, 22, 0);
    public static final Pose center = poseFactory.of(70.5, 70.5, 0);
    public static final Pose middleRedLoadingZoneSide = poseFactory.of(70.5, 119, 0);
    public static final Pose middleBlueLoadingZoneSide = poseFactory.of(70.5, 22, 0);
    public static final Pose redSideCenter = poseFactory.of(22, 70.5, 0);
    public static final Pose blueSideCenter = poseFactory.of(118, 70.5, 0);
    public static final Pose timeOutCorner = poseFactory.of(0, 0, 0);
}
