package com.sky.utils;

import java.util.UUID;

public class UUIDUtil {

    public static String getUUID(String fileName) {
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        return UUID.randomUUID().toString().replaceAll("-", "")+suffix;
    }
}
