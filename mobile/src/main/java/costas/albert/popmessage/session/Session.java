package costas.albert.popmessage.session;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import costas.albert.popmessage.entity.Token;

public class Session {

    private static final String TOKEN = "token";
    private SharedPreferences prefs;

    public Session(Context context) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public Token getToken() {
        String tokenString = prefs.getString(TOKEN, null);
        return new Token(tokenString);
    }

    public boolean setToken(Token token) {
        final boolean result;
        if (prefs.edit().putString(TOKEN, token.hash()).commit()) result = true;
        else result = false;
        return result;
    }

    public boolean resetToken() {
        final boolean result;
        if (prefs.edit().remove(TOKEN).commit()) result = true;
        else result = false;
        return result;
    }
}