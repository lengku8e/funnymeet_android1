package com.mtcent.funnymeet.config;

public final class Constants {

    /**
     * 查询到期望的结果
     */
    public static final int FIND_RESULT_EXIST = 1;
    /**
     * 没有查询到期望的结果
     */
    public static final int FIND_RESULT_NOTEXIST = -1;
    /**
     * 1 公众俱乐部
     */
    public static final int CLUB_TYPE_ID_PUBLIC = 1;
    /**
     * 2 私人俱乐部
     */
    public static final int CLUB_TYPE_ID_MEMBERSHIP = 2;

    /**
     * 趣聚APPID
     */
    public static final String APPID_QUJU = "funnymeet";

    /*
    开发环境连接配置
     */
    public static String SERVER_HOST_IP = "192.168.1.104:8080";
    public static String SERVER_HOST_OPEN = "192.168.1.104:8080";
    public static String SERVICE_HOST = "http://192.168.1.104:8080/api.htm";
/*    public static String SERVER_INVITE_LINK = "http://file.dev.mtcent.com/open/m/v.html?openHost=http://" + SERVER_HOST_OPEN + "&clubId=";
    public static String APP_DOWNLOADPATH = "http://file.dev.mtcent.com/app/android/";*/
    /**
     *
     * 自己改的邀请链接，需要后期再研究
     */

public static String SERVER_INVITE_LINK = "http://localhost:8080/open/m/v.html?openHost=http://" + SERVER_HOST_OPEN + "&clubId=";
    public static String APP_DOWNLOADPATH = "http://localhost:8080/app/android/";

    public static final String APP_NAME = "funnymeet.apk";

    //当前版本类型
    public static String CURRENT_VERSION_TYPE = "开发版";

    private Constants() {
        // dummy
    }
    //--------------------以上是212测试环境变量--------------------

   /* *//*
        切换连接参数到开发版
         *//*
    public static void switchVersionToDev() {
        SERVER_HOST_IP = "open.dev.mtcent.com";
        SERVER_HOST_OPEN = "open.dev.mtcent.com";
        SERVICE_HOST = "http://open.dev.mtcent.com/api.htm";
        SERVER_INVITE_LINK = "http://file.dev.mtcent.com/open/m/v.html?openHost=http://" + SERVER_HOST_OPEN + "&clubId=";
        APP_DOWNLOADPATH = "http://file.dev.mtcent.com/app/android/";
        CURRENT_VERSION_TYPE = "开发版";
    }

    *//*
        切换连接参数到内测版
    *//*
    public static void switchVersionToAlpha() {
        SERVER_HOST_IP = "open.mtcent.com";
        SERVER_HOST_OPEN = "open.mtcent.com";
        SERVICE_HOST = "http://open.mtcent.com/api.htm";
        SERVER_INVITE_LINK = "http://file.mtcent.com/open/m/v.html?openHost=http://" + SERVER_HOST_OPEN + "&clubId=";
        APP_DOWNLOADPATH = "http://file.mtcent.com/app/android/";
        CURRENT_VERSION_TYPE = "内测版";
    }*/

}
