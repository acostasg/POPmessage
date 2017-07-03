package costas.albert.popmessage.wrapper;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;

import costas.albert.popmessage.R;

public class ProgressViewWrapper {
    private final AppCompatActivity appCompatActivity;
    private ProgressBar mProgressView;

    public ProgressViewWrapper(AppCompatActivity messagesActivity) {
        this.appCompatActivity = messagesActivity;
        this.mProgressView = (ProgressBar) this.appCompatActivity.findViewById(R.id.load_progress);
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = appCompatActivity.getResources().getInteger(android.R.integer.config_shortAnimTime);

            this.mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            this.mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            this.mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }
}