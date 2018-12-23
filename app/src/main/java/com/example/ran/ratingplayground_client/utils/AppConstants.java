package com.example.ran.ratingplayground_client.utils;

public class AppConstants {

    public static final String PLAYER = "player";
    public static final String MANAGER = "manager";
    public static final String USER = "user";
    public static final String PLAYGROUND = "ratingplayground";

    //emulator
//    public static final String HOST = "http://10.0.2.2:8084";

    //real device
    public static final String HOST = "http://192.168.43.166:8084";


    public static final String HTTP_CREATE_USER = "/playground/users";
    public static final String HTTP_VERIFY_USER = "/playground/users/confirm/";
    public static final String HTTP_GET_USER = "/playground/users/login";
    public static final String HTTP_UPDATE_USER ="/playground/users/{playground}/{email}";

    public static final String EVENT_SIGN_IN ="sign_in_event";
    public static final String EVENT_VERIFY_USER = "verify_user_event";
    public static final String EVENT_REGISTER = "register_event";

}
