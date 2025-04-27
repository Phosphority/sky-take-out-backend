package com.sky.context;

import java.util.Map;

public class BaseContext {

    public static ThreadLocal<Map<String, Long>> threadLocal = new ThreadLocal<>();

    public static void setCurrentId(Map<String,Long> idClaims) {
        threadLocal.set(idClaims);
    }

    public static Map<String, Long> getCurrentId() {
        return threadLocal.get();
    }

    public static void removeCurrentId() {
        threadLocal.remove();
    }

}
