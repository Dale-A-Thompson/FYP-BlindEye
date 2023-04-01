package utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

public class VersionUtil {
    //default ver name
    public static final String DEFAULT_VERSION = "unknown";

    //Get name of app
    public static String getNameOfVersion(Context context) {
        String vName;
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            vName = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            vName = DEFAULT_VERSION;
        }
        return vName;
    }

    //non-instantiable class
    private VersionUtil() {

    }
}
