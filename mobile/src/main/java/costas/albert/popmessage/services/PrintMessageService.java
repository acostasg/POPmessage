package costas.albert.popmessage.services;

import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

public class PrintMessageService {
    public PrintMessageService() {
    }

    public void printMessage(String text, AppCompatActivity mContext, int duration) {
        Toast.makeText(
                mContext.getBaseContext(),
                text,
                duration
        ).show();
    }

    public void printMessage(String text, AppCompatActivity mContext) {
        this.printMessage(
                text,
                mContext,
                Toast.LENGTH_LONG
        );
    }

    public void printBarMessage(String text, AppCompatActivity mContext) {
        this.printBarMessage(
                text,
                mContext,
                Snackbar.LENGTH_LONG
        );
    }

    public void printBarMessage(String text, AppCompatActivity mContext, int duration) {
        Snackbar.make(
                mContext.findViewById(android.R.id.content).getRootView(),
                text,
                duration
        ).show();
    }
}