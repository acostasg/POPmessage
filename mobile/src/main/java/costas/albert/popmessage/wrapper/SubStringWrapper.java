package costas.albert.popmessage.wrapper;

public class SubStringWrapper {

    private static final int MAX_CHAR = 40;
    private String text;

    private SubStringWrapper(String text) {
        this.text = text;
    }

    public static String subString(String text) {
        return new SubStringWrapper(text).subString();
    }

    private String subString() {
        int maxLength = (this.text.length() < MAX_CHAR) ? this.text.length() : MAX_CHAR;
        return this.text.substring(0, maxLength);
    }
}
