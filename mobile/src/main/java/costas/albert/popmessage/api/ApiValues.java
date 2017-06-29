package costas.albert.popmessage.api;

/**
 * https://developer.android.com/studio/run/emulator-commandline.html#networkaddresses
 */
public class ApiValues {

    //public scope
    public static final String TOKEN_VALIDATION_END_POINT = "session/token";
    public static final String LOGIN_END_POINT = "user/login";
    public static final String GET_USER = "user/get";
    public static final String LOGOUT_END_POINT = "user/logout";
    public static final String REGISTER_END_POINT = "user/create";
    public static final String GET_MESSAGE_TO_LOCATION = "message/get";
    public static final String POST_CREATE_MESSAGE = "message/create";
    public static final String POST_UPDATE_MESSAGE = "message/update";
    public static final String POST_REMOVE_MESSAGE = "message/delete";
    public static final String POST_MESSAGE_LIKE = "message/vote/like/create";
    public static final String POST_MESSAGE_DISLIKE = "message/vote/dislike/create";
    public static final String GET_MESSAGE_TO_USER = "/user/message/get";
    //login
    public static final String USERNAME = "userName";
    public static final String PASSWORD = "password";
    //extra for register
    public static final String NAME = "name";
    public static final String DATE_OF_BIRTH = "dateOfBirth";
    public static final String PRIVACY_POLICY = "privacyPolicy";
    //location
    public static final String LAT = "lat";
    public static final String LON = "lon";
    public static final String LAST = "last";
    //publication
    public static final String TEXT = "text";
    //vote message
    public static final String MESSAGE = "message";
    public static final int LIMIT = 10;

    //only scope api package
    static final String BASE_URL = "http://10.0.2.2:8080/"; //android emulator simulator to pc
    //static final String BASE_URL = "http://zonamessage.com:8080/"; //VPS
    static final String APP_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9";
    static final String AUTHORIZATION = "Authorization";
    static final String TOKEN = "Token";

}
