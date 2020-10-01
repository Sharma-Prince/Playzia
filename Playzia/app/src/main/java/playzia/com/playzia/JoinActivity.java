package playzia.com.playzia;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;

import com.allyants.notifyme.NotifyMe;

import java.util.Calendar;

import static playzia.com.playzia.Constants.USER_DATA;

public class JoinActivity extends AppCompatActivity {

    String matchID;
    String matchType;
    String username;
    String entryFee;
    String dateTime;
    Button btnCancel;
    Button btnAddMoney;
    Button btnNext;
    int fee;
    float myBalance;
    String accountBalance;
    TextView txtEntryFee;
    TextView balanceStatus;
    TextView txtBalance;
    String token;
    LinearLayout nextButtonLL;
    LinearLayout addMoneyLL;

    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);
        this.nextButtonLL = findViewById(R.id.nextButtonLL);
        this.addMoneyLL =  findViewById(R.id.addMoneyLL);
        this.btnAddMoney = findViewById(R.id.btnAddMoney);
        this.btnCancel = findViewById(R.id.btnCancel);
        this.btnNext = findViewById(R.id.btnNext);
        this.txtEntryFee = findViewById(R.id.txtEntryFee);
        this.txtBalance = findViewById(R.id.txtBalance);
        this.balanceStatus = findViewById(R.id.balanceStatus);
        this.matchID = getIntent().getStringExtra("matchID");
        this.matchType = getIntent().getStringExtra("matchType");
        this.entryFee = getIntent().getStringExtra("amountPay");
        this.dateTime = getIntent().getStringExtra("dataTime");
        sharedPreferences = getSharedPreferences(USER_DATA,0);
        this.token = sharedPreferences.getString("token",null);
        this.username = sharedPreferences.getString("username",null);
        this.accountBalance = sharedPreferences.getString("balance","₹ 0");
        this.fee = Integer.parseInt(entryFee);
        this.myBalance = Float.valueOf(sharedPreferences.getString("balance","0"));
        this.txtBalance.setText("₹ " + accountBalance);
        this.txtEntryFee.setText("₹ " + entryFee);

        if(this.myBalance >= this.fee){
            this.balanceStatus.setText(R.string.sufficientBalanceText);
            this.btnNext.setText("NEXT");
        }else{
            this.balanceStatus.setTextColor(Color.parseColor("#ff0000"));
            this.balanceStatus.setText(R.string.insufficientBalanceText);
            this.addMoneyLL.setVisibility(View.VISIBLE);
            this.btnNext.setVisibility(View.GONE);
        }
        this.btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNotification();
                new ActionAlertMessage().showJoinMatchAlert(JoinActivity.this, JoinActivity.this.matchType, JoinActivity.this.matchID, JoinActivity.this.token, JoinActivity.this.username);

            }
        });
        this.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JoinActivity.this.onBackPressed();
            }
        });
        this.btnAddMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JoinActivity.this.startActivity(new Intent(JoinActivity.this,MyWallet.class));
                JoinActivity.this.finish();
            }
        });
    }

    private void addNotification() {
        try {
            int year,month,day,hr,min;
            //18-05-2020 at  09:00:00 PM
            Calendar now = Calendar.getInstance();
            dateTime = dateTime.substring(6,dateTime.length());
            day = Integer.parseInt(dateTime.substring(0,2));
            month = Integer.parseInt(dateTime.substring(3,5));
            year = Integer.parseInt(dateTime.substring(6,10));
            hr = Integer.parseInt(dateTime.substring(15,17));
            min = Integer.parseInt(dateTime.substring(18,20));


            now.set(Calendar.YEAR,year);
            now.set(Calendar.MONTH,month);
            now.set(Calendar.DAY_OF_MONTH,day);
            if(min-15<0){
                hr=hr-1;
                min = min+60-15;
                now.set(Calendar.HOUR_OF_DAY,hr);
                now.set(Calendar.MINUTE,min);
            }else{
                now.set(Calendar.HOUR_OF_DAY,hr);
                now.set(Calendar.MINUTE,min-15);
            }
            if(dateTime.substring(dateTime.length() - 2, dateTime.length()).equals("PM"))
                now.set(Calendar.PM,Calendar.PM);
            else
                now.set(Calendar.AM,Calendar.AM);
            Log.e("time",now.getTime().toString());
            NotifyMe notifyMe = new NotifyMe.Builder(getApplicationContext())
                    .title("Room ID and Password is updated")
                    .content("Match is going to start in 15 Minute")
                    .color(41, 98, 255, 1)
                    .led_color(41, 98, 255, 1)
                    .time(now)
                    .key(String.valueOf(matchID))
                    .addAction(new Intent(),"Cancel",true,false)
                    .addAction(new Intent(),"Play",true,false)
                    .large_icon(R.drawable.ic_notifications_black_24dp)
                    .build();
        }catch (Exception e){
            Log.e("Error","");
        }

    }
}
