package com.chatgpt.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtils {
    public static String timeStamp2Date(long timeStamp, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date(timeStamp));
    }
}
