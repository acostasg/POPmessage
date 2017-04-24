package costas.albert.popmessage.session;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Session {

    private SharedPreferences prefs;

    public Session(Context context) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public String getToken() {
        String token;
        token = prefs.getString("token", "");
        return token;
    }

    public void setToken(String hash) {
        final boolean token = prefs.edit().putString("token", hash).commit();
    }

    public void resetToken() {
        final boolean token = prefs.edit().remove("token").commit();
    }
}