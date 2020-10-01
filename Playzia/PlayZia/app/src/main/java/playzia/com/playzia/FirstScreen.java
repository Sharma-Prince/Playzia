package playzia.com.playzia;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import am.appwise.components.ni.NoInternetDialog;

import static playzia.com.playzia.Constants.USER_DATA;

public class FirstScreen extends AppCompatActivity {

    NoInternetDialog noInternetDialog;
    SharedPreferences preferences;
    Boolean userData = Boolean.valueOf(false);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_screen);
        this.preferences = getSharedPreferences(USER_DATA, 0);
        this.noInternetDialog = new NoInternetDialog.Builder((Context) this).build();
        this.userData = Boolean.valueOf(this.preferences.getBoolean("isDataAvailable", false));
        if(this.userData.booleanValue()){
            startActivity(new Intent(this,MainActivity.class));
            finish();
            return;
        }
        Fragment login  = new LoginFragment(false);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frameContainer,login);
        transaction.commit();


        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationManager mNotificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(Constants.CHANNEL_ID, Constants.CHANNEL_NAME, importance);
            mChannel.setDescription(Constants.CHANNEL_DESCRIPTION);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            mNotificationManager.createNotificationChannel(mChannel);
        }

        /*
         * Displaying a notification locally
         */
        //playzia.com.playzia.NotificationManager.getInstance(this).displayNotification("Greetings", "Hello how are you?");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.noInternetDialog.onDestroy();
    }
}
