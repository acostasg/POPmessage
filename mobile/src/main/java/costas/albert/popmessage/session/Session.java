package costas.albert.popmessage.session;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import costas.albert.popmessage.entity.Token;

public class Session {

    private SharedPreferences prefs;

    public Session(Context context) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public Token getToken() {
        String tokenString = prefs.getString("token", null);
        return new Token(tokenString);
    }

    public void setToken(Token token) {
        boolean result = prefs.edit().putString("token", token.hash()).commit();
    }

    public void resetToken() {
        final boolean token = prefs.edit().remove("token").commit();
    }
}