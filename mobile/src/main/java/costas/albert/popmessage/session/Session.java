package costas.albert.popmessage.session;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.fasterxml.jackson.databind.ObjectMapper;

import costas.albert.popmessage.entity.Token;
import costas.albert.popmessage.entity.User;

public class Session {

    private static final String TOKEN = "token";
    private static final String USER = "user";
    private SharedPreferences prefs;
    private ObjectMapper mapper = new ObjectMapper();

    public Session(Context context) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public Token getToken() {
        String tokenString = prefs.getString(TOKEN, null);
        return new Token(tokenString);
    }

    public boolean setToken(Token token) {
        final boolean result;
        result = prefs.edit().putString(TOKEN, token.hash()).commit();
        return result;
    }

    public User getUser() {
        String user = prefs.getString(USER, null);
        try {
            return mapper.readValue(user, User.class);
        } catch (Exception exception) {
            return new User();
        }

    }

    public boolean setUser(User user) {
        try {
            String jsonInString = mapper.writeValueAsString(user);
            final boolean result;
            result = prefs.edit().putString(USER, jsonInString).commit();
            return result;
        } catch (Exception exception) {
            return false;
        }
    }

    public boolean hasUser() {
        return prefs.contains(USER);
    }

    public void resetSession() {
        prefs.edit().clear().apply();
    }
}