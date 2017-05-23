package costas.albert.popmessage.wrapper;

import android.text.Html;
import android.util.Base64;

public class EncodeMessageWrapper {
    private String text;

    public EncodeMessageWrapper(String text) {

        this.text = Html.fromHtml(text).toString();
    }

    public static String Decoder(String text) {
        return new EncodeMessageWrapper(text).decode();
    }

    public boolean isShort() {
        return (this.text.length() < 15);
    }

    public boolean isSmall() {
        return (this.text.length() > 160);
    }

    public String encode() {
        return new String(Base64.encode(this.text.getBytes(), 2));
    }

    public String clearCode() {
        return this.text;
    }

    private String decode() {
        return new String(Base64.decode(this.text.getBytes(), 2));
    }
}
