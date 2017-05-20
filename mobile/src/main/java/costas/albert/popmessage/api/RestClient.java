package costas.albert.popmessage.api;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.net.URLEncoder;

import costas.albert.popmessage.entity.Token;

public final class RestClient {

    private static final char SEPARATOR_PARAMS = '?';
    private static final char EQUAL = '=';

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.addHeader(ApiValues.AUTHORIZATION, ApiValues.APP_KEY);
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler, Token token) {
        client.addHeader(ApiValues.AUTHORIZATION, ApiValues.APP_KEY);
        params.add(ApiValues.TOKEN, token.hash());
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.addHeader(ApiValues.AUTHORIZATION, ApiValues.APP_KEY);
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler, Token token) {
        client.addHeader(ApiValues.AUTHORIZATION, ApiValues.APP_KEY);
        client.post(getAbsoluteUrl(url, token), params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return ApiValues.BASE_URL + relativeUrl;
    }

    private static String getAbsoluteUrl(String relativeUrl, Token token) {
        try {
            return ApiValues.BASE_URL + relativeUrl + SEPARATOR_PARAMS +
                    ApiValues.TOKEN + EQUAL + URLEncoder.encode(token.hash(), "UTF-8");
        } catch (java.io.UnsupportedEncodingException exception) {
            return null;
        }
    }
}
