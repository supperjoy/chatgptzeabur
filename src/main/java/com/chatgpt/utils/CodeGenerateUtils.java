package com.chatgpt.utils;

import java.util.UUID;

public class CodeGenerateUtils {
    public static String getCode(){
        // 通过UUID生成随机UUID
        UUID uuid = UUID.randomUUID();
        // 将UUID转换为16进制字符串，并取前16位
        return Long.toHexString(uuid.getMostSignificantBits());
    }
}
