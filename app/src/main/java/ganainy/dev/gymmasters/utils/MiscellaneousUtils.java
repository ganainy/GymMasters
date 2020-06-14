package ganainy.dev.gymmasters.utils;

import android.content.Context;
import android.net.Uri;

public class MiscellaneousUtils {
    /**converts string to image res with same name*/
    public static int getImageId(Context context, String imageName) {
        return context.getResources().getIdentifier("drawable/" + imageName, null, context.getPackageName());
    }

    /**convert uri to a string with timestamp concatenated to it*/
    public static String formatUriAsTimeStampedString(Uri uriWithoutTimeStamp){
        String lastPathSegment = uriWithoutTimeStamp.getLastPathSegment();
        String timeStampedString = lastPathSegment + System.currentTimeMillis();
        return timeStampedString;
    }
}
