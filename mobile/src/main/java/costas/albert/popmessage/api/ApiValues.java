package costas.albert.popmessage.api;

/**
 * https://developer.android.com/studio/run/emulator-commandline.html#networkaddresses
 */
public class ApiValues {

    //only scope api package
    protected static final String BASE_URL = "http://10.0.2.2:8080/"; //android simulator to pc
    protected static final String APP_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9";
    protected static final String AUTHORIZATION = "Authorization";
    protected static final String TOKEN = "Token";

    //public scope
    public static final String TOKEN_VALIDATION_END_POINT = "session/token";
    public static final String LOGIN_END_POINT = "user/login";
    public static final String LOGOUT_END_POINT = "user/logout";
    public static final String REGISTER_END_POINT = "user/create";

    /**
     * fields
     **/
    //login
    public static final String USERNAME = "userName";
    public static final String PASSWORD = "password";

    //extra for register
    public static final String NAME = "name";
    public static final String DATE_OF_BIRTH = "dateOfBirth";
    public static final String PRIVACY_POLICY = "privacyPolicy";

}
