package com.chatgpt.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

public class OrderUtils {
    private static final AtomicInteger counter = new AtomicInteger(0);
    
    public static String generateOrderId() {
        // 获取当前时间戳
        long currentTime = System.currentTimeMillis();
        // 格式化时间戳，以当前时间为基准保留17位数字
        String dateTime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date(currentTime));
        // 获取自增数字，并保留3位数字，不足的前面补充0
        String sequence = String.format("%03d", counter.getAndIncrement() % 1000);
        // 组合成订单号
        return dateTime + sequence;
    }
}
