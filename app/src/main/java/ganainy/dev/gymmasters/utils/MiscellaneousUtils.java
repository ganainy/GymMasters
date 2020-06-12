package ganainy.dev.gymmasters.utils;

import android.content.Context;

public class MiscellaneousUtils {
    /**converts string to image res with same name*/
    public static int getImageId(Context context, String imageName) {
        return context.getResources().getIdentifier("drawable/" + imageName, null, context.getPackageName());
    }
}
