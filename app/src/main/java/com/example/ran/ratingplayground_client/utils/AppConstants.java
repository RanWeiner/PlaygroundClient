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


    public static final String BOOK_IMAGE_URL = "https://cdn-images-1.medium.com/max/1500/1*OQXqTrTpXynrt85LeuaCnA.jpeg";
    public static final String MOVIE_IMAGE_URL = "https://media.wired.com/photos/5b7350e75fc74d47846ce4e4/master/w_825,c_limit/Popcorn-869302844.jpg";
    public static final String BILLBOARD_IMAGE_URL = "https://upload.wikimedia.org/wikipedia/commons/thumb/8/8d/Paper-notes.svg/768px-Paper-notes.svg.png";

    //emulator
//    public static final String HOST = "http://10.0.2.2:8084";

    //real device
    public static final String HOST = "http://10.100.102.18:8084";


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
