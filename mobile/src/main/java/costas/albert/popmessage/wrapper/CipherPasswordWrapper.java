package costas.albert.popmessage.wrapper;

import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class CipherPasswordWrapper {

    private static int MINIM = 16;
    private String password;

    private CipherPasswordWrapper(String password) {

        this.password = password;
    }

    public static String Encoder(String text) {
        return new CipherPasswordWrapper(text).encode();
    }

    private SecretKey generateKey()
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        return new SecretKeySpec(password.getBytes(), "AES");
    }

    private byte[] encryptMsg(String message, SecretKey secret)
            throws NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidKeyException, InvalidParameterSpecException,
            IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
        Cipher cipher;
        cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
        cipher.init(Cipher.ENCRYPT_MODE, secret);
        return cipher.doFinal(message.getBytes("UTF-8"));
    }

    private String encode() {
        try {
            return this.packageToSend(
                    this.encryptMsg(
                            this.getPassword(),
                            this.generateKey()
                    )
            );
        } catch (Exception exception) {
            return null;
        }
    }

    private String packageToSend(byte[] encrypt) {
        return new String(Base64.encode(encrypt, 2));
    }


    private String getPassword() {
        if (password.length() < MINIM) {
            char add = '-';
            int number = MINIM - password.length();

            char[] repeat = new char[number];
            Arrays.fill(repeat, add);
            password += new String(repeat);
        }

        return this.password;
    }
}
