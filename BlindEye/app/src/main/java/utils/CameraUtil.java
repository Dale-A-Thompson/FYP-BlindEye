package utils;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.view.Surface;
import android.view.WindowManager;

import java.util.List;

//Static utils methods for Camera
public class CameraUtil {
    private static final double ASPECT_TOLERANCE = 0.15;

    //Calculating optimal camera preview size according to layout that is being used to display the preview
    //available camera sizes, layout width to display preview, layout height to display preview
    //orientation to check if it is portrait because camera sizes are in landscape
    public static Camera.Size bestPreviewSize(List<Camera.Size> sizeList, int lWidth, int lHeight, boolean isPortrait) {
        if (isPortrait) {
            //inversing SurfaceWidth and SurfaceHeight as all sizes are in landscape mode
            lHeight = lHeight + lWidth;
            lWidth = lHeight - lWidth;
            lHeight = lHeight - lWidth;
        }
        double ratio = (double) lWidth / lHeight;
        Camera.Size idealSize = null;
        double idealArea = 0;

        //Trying to find the matching size for the ratio and maximum area
        for (Camera.Size cameraSize : sizeList) {
            double cameraRatio = (double) cameraSize.width / cameraSize.height;
            double cameraArea = cameraSize.width * cameraSize.height;
            double ratioDiff = Math.abs(cameraRatio - ratio);

            if (ratioDiff < ASPECT_TOLERANCE && cameraArea > idealArea) {
                idealSize = cameraSize;
                idealArea = cameraArea;
            }
        }

        //Unable to find size to match ratio, find size that matches the maximum area
        if (idealSize == null) {
            idealArea = 0;
            for (Camera.Size cameraSize : sizeList) {
                double cameraArea = cameraSize.width * cameraSize.height;
                if (cameraArea > idealArea) {
                    idealSize = cameraSize;
                    idealArea = cameraArea;
                }
            }
        }
        return idealSize;
    }

    //calculating proportional layout dimens for displaying camera review according to given preview size
    public static int[] proportionalDimen(Camera.Size size, int tW, int tH, boolean isPortrait) {
        int[] fitDimen = new int[2];
        double prevRatio;

        if (isPortrait) {
            prevRatio = (double) size.height / size.width;
        } else {
            prevRatio = (double) size.height / size.height;
        }

        if (((double) tW / tH) > prevRatio) {
            fitDimen[0] = tW;
            fitDimen[1] = (int) (fitDimen[0] / prevRatio);
        } else {
            fitDimen[1] = tH;
            fitDimen[0] = (int) (fitDimen[1] * prevRatio);
        }
        return fitDimen;
    }

    //Adapting the Camera's display orientation to the current device
    public static void displayOrien(Context context, Camera camera) {
        final Camera.CameraInfo cameraInfo = new Camera.CameraInfo();

        Camera.getCameraInfo(Camera.CameraInfo.CAMERA_FACING_BACK, cameraInfo);
        int d = 0;
        final int currRotation = ((WindowManager) context.getSystemService(Activity.WINDOW_SERVICE)).getDefaultDisplay().getRotation();
        switch (currRotation) {
            case Surface.ROTATION_0:
                d = 0;
                break;
            case Surface.ROTATION_90:
                d = 90;
                break;
            case Surface.ROTATION_180:
                d = 180;
                break;
            case Surface.ROTATION_270:
                d = 270;
                break;
        }

        int dOrientation = 0;
        if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            dOrientation = (cameraInfo.orientation + d) % 360;
            dOrientation = (360 - dOrientation) % 360; //done to compensate for mirroring
        } else {
            //back facing camera
            dOrientation = (cameraInfo.orientation - d + 360) % 360;
        }

        camera.setDisplayOrientation(dOrientation);
    }

    //non-instantiable class
    private CameraUtil() {

    }
}
