package com.example.ran.ratingplayground_client.utils;

public class AppConstants {

    public static final String PLAYER = "player";
    public static final String MANAGER = "manager";
    public static final String USER = "user";
    public static final String ELEMENT = "element";
    public static final String PLAYGROUND = "ratingplayground";
    public static final String DELIMITER = "@@";
    public static final String BOOK_TYPE = "book";
    public static final String MOVIE_TYPE = "movie";
    public static final String BILLBOARD_TYPE = "billboard";
    public static final String MESSAGE = "message";
    public static final String MESSAGES = "messages";
    public static final String ATTRIBUTES = "attributes";
    public static final String PAGE = "page";
    public static final String SIZE = "size";
    public static final String RATING = "rating";


    public static final String BOOK_IMAGE_URL = "https://i.ibb.co/Wsg4dF8/f8863405-b08d-4742-9588-d027366bccd5.png";
    public static final String MOVIE_IMAGE_URL = "https://i.ibb.co/BPqXyK7/22e2300c-22cc-43a4-bdd5-188b6a117386.png";
    public static final String BILLBOARD_IMAGE_URL = "https://i.ibb.co/Kxsk9JQ/b71d4375-46ed-4e62-bf14-282438e68dd3.png";

    //emulator
//    public static final String HOST = "http://10.0.2.2:8084";

    //real device
    public static final String HOST = "http://10.100.102.18:8084"; //ran's place

    public static final String HTTP_USER ="/playground/users/";
    public static final String HTTP_VERIFY_USER = "/playground/users/confirm/";
    public static final String HTTP_GET_USER = "/playground/users/login";
    public static final String HTTP_ELEMENT = "/playground/elements/";
    public static final String HTTP_ACTIVITIES = "/playground/activities/";



    public static final String EVENT_SIGN_IN ="sign_in_event";
    public static final String EVENT_VERIFY_USER = "verify_user_event";
    public static final String EVENT_REGISTER = "register_event";
    public static final String EVENT_UPDATE_USER ="sign_in_event";
    public static final String EVENT_GET_ELEMENTS = "get_elements";
    public static final String EVENT_POST_ACTIVITY = "post_activity";
    public static final String EVENT_READ_ACTIVITY = "read_activity";

    public static final String TYPE_POST_MESSAGE = "PostMessage";
    public static final String TYPE_READ_MESSAGES = "ReadMessages";
    public static final String TYPE_RATING = "Rating";

}
