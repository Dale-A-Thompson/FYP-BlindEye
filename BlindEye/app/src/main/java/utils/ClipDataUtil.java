package utils;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

//static method(s) for dealing with clipdata
public class ClipDataUtil {
    //setting text as current primary clip
    //this gets the clipboardmanager and sets the plain text value clipdata as the primary clip
    public static void clipText(Context context, String label, CharSequence charSequence) {
        final ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        final ClipData clipData = ClipData.newPlainText(label, charSequence);
        clipboardManager.setPrimaryClip(clipData);
    }

    private ClipDataUtil() {
        //noninstantiable
    }

}
