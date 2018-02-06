package app.ys.quakeview;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;


/**
 * Created by YS on 12-12-2017.
 */

public class Splash extends AppCompatActivity {


        private static final int SPLASH_DISPLAY_TIME = 4000; // splash screen delay time

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.splash);
            ActionBar myActionBar = getSupportActionBar();
            myActionBar.hide();

            new Handler().postDelayed(new Runnable() {
                public void run() {

                    Intent intent = new Intent();
                    intent.setClass(Splash.this, MainActivity.class);

                    Splash.this.startActivity(intent);
                    Splash.this.finish();

                    // transition from splash to main menu
                    overridePendingTransition(R.anim.activityfadein,
                            R.anim.splashfadeout);

                }
            }, SPLASH_DISPLAY_TIME);
        }
}
