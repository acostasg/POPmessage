package costas.albert.popmessage.api;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;

import java.net.URLEncoder;

import costas.albert.popmessage.entity.Token;

public final class RestClient {
    //TODO move this const to config application
    private static final String BASE_URL = "http://127.0.0.1:8080/";
    private static final String APP_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9";
    private static final String AUTHORIZATION = "Authorization";

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static RequestHandle get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.addHeader(AUTHORIZATION, APP_KEY);
        return client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static RequestHandle get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler, Token token) {
        client.addHeader(AUTHORIZATION, APP_KEY);
        params.add("token", token.hash());
        return client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static RequestHandle post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.addHeader(AUTHORIZATION, APP_KEY);
        return client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    public static RequestHandle post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler, Token token) {
        client.addHeader(AUTHORIZATION, APP_KEY);
        return client.post(getAbsoluteUrl(url, token), params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }

    private static String getAbsoluteUrl(String relativeUrl, Token token) {
        try {
            return BASE_URL + relativeUrl + "&" + URLEncoder.encode(token.hash(), "UTF-8");
        } catch (java.io.UnsupportedEncodingException exception) {
            return null;
        }
    }
}
