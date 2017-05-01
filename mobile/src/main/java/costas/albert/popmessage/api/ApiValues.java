package costas.albert.popmessage.api;

/**
 * https://developer.android.com/studio/run/emulator-commandline.html#networkaddresses
 */
public class ApiValues {

    //only scope api package
    protected static final String BASE_URL = "http://10.0.2.2:8080/"; //android simulator to pc
    protected static final String APP_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9";
    protected static final String AUTHORIZATION = "Authorization";

    //public scope
    public static final String TOKEN_VALIDATION_END_POINT = "session/token";
    public static final String LOGIN_END_POINT = "user/login";


    //fields
    public static final String USERNAME = "userName";
    public static final String PASSWORD = "password";

}
