package playzia.com.playzia;

import android.app.ProgressDialog;
import android.content.SharedPreferences;

import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.android.material.tabs.TabLayout;
import com.paytm.pgsdk.PaytmConstants;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import static playzia.com.playzia.Constants.MK47_MUTANT;
import static playzia.com.playzia.Constants.WINCHESTER;

public class MyWallet extends AppCompatActivity {

    private TabAdapter adapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private TextView walletBalance;
    Toolbar toolbar;
    SharedPreferences sharedPreferences;
    String balance;
    String number;
    String username;
    String token;
    String email;
    String orderID;
    String callbackURL;
    String channelID;
    String checkSum;
    String industryType;
    String mID;
    String phone;
    String txnAmount;
    String website;
    String status;
    String msg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_wallet);


        toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        this.viewPager = (ViewPager) findViewById(R.id.viewPager);
        this.tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        this.walletBalance = findViewById(R.id.walletBalance);
        this.adapter = new TabAdapter(getSupportFragmentManager());
        this.sharedPreferences = getSharedPreferences(Constants.USER_DATA, 0);
        this.balance = this.sharedPreferences.getString("balance", null);
        this.username = this.sharedPreferences.getString("username",null);
        this.token = this.sharedPreferences.getString("token",null);
        this.email = this.sharedPreferences.getString("email",null);
        this.adapter.addFragment(new AddMoneyTab(), "Add Money ");
        this.adapter.addFragment(new WithdrawTab(), "Withdraw");
        this.adapter.addFragment(new TransactionTab(), "Transactions");
        this.viewPager.setAdapter(this.adapter);
        this.tabLayout.setupWithViewPager(this.viewPager);
        this.walletBalance.setText("₹ " + balance);
    }

    public void PaytmAddMoney(String str) {
        this.txnAmount = str;
        HashMap<String, String> data = new HashMap<>();
        data.put("username",this.username);
        data.put("token",token);
        data.put("amount",str);
        HashMap<String, String> header = new HashMap<>();
        header.put(Constants.AUTH,"token " + this.token);
        NetworkController.getInstance().connect(this, MK47_MUTANT,Request.Method.POST,data,header,new PaytmGateway());
    }
    class PaytmGateway implements NetworkController.ResultListener, PaytmPaymentTransactionCallback {

        @Override
        public void onResult(int method, boolean isSuccess, JSONObject jsonObject, VolleyError volleyError, ProgressDialog progressDialog) {
            if(isSuccess) {
                try {
                    MyWallet.this.orderID = jsonObject.getString("ORDER_ID");
                    HashMap<String, String> ha = new HashMap<>();
                    ha.put("MID",jsonObject.getString("MID"));
                    ha.put("ORDER_ID",MyWallet.this.orderID);
                    ha.put("CUST_ID",MyWallet.this.email);
                    ha.put("INDUSTRY_TYPE_ID",jsonObject.getString("INDUSTRY_TYPE_ID"));
                    ha.put("CHANNEL_ID",jsonObject.getString("CHANNEL_ID"));
                    ha.put("TXN_AMOUNT",MyWallet.this.txnAmount);
                    ha.put("WEBSITE",jsonObject.getString("WEBSITE"));
                    ha.put("CALLBACK_URL",jsonObject.getString("CALLBACK_URL"));
                    ha.put("CHECKSUMHASH",jsonObject.getString("checksum"));

                    PaytmOrder order = new PaytmOrder(ha);

                    PaytmPGService pgService = PaytmPGService.getProductionService(); //.getProductionService();
                    pgService.initialize(order,null);
                    pgService.startPaymentTransaction(MyWallet.this, true, true, this);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else{

            }
            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();
        }

        @Override
        public void onTransactionResponse(Bundle inResponse) {
            Log.d("PaytmResponse", inResponse.toString());
            String bundle2 = inResponse.toString();
            bundle2 = bundle2.substring(bundle2.indexOf(PaytmConstants.STATUS), bundle2.indexOf("}]")).split(",")[0].split("=")[1];
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(bundle2);
            stringBuilder.append(" ");
            stringBuilder.append(MyWallet.this.orderID);
            Log.d("ResponseData", stringBuilder.toString());
            if (bundle2.equals("TXN_SUCCESS")) {
                SendPaymentRequest(MyWallet.this.orderID);
            }
        }

        @Override
        public void networkNotAvailable() {
            Toast.makeText(getApplicationContext(), "Network connection error: Check your internet connectivity", Toast.LENGTH_LONG).show();

        }

        @Override
        public void clientAuthenticationFailed(String inErrorMessage) {
            Toast.makeText(getApplicationContext(), "Authentication failed: Server error" , Toast.LENGTH_LONG).show();
        }

        @Override
        public void someUIErrorOccurred(String inErrorMessage) {
            Toast.makeText(getApplicationContext(), "UI Error " , Toast.LENGTH_LONG).show();
        }

        @Override
        public void onErrorLoadingWebPage(int iniErrorCode, String inErrorMessage, String inFailingUrl) {
            Toast.makeText(getApplicationContext(), "Unable to load webpage " , Toast.LENGTH_LONG).show();
        }

        @Override
        public void onBackPressedCancelTransaction() {
            Toast.makeText(getApplicationContext(), "Transaction cancelled" , Toast.LENGTH_LONG).show();
        }

        @Override
        public void onTransactionCancel(String inErrorMessage, Bundle inResponse) {
            Toast.makeText(getApplicationContext(), "Transaction Cancelled" , Toast.LENGTH_LONG).show();
        }
    }

    private void SendPaymentRequest(String str) {
        HashMap<String, String> data = new HashMap<>();
        data.put("username",this.username);
        data.put("token",token);
        data.put("orderid",str);
        HashMap<String, String> header = new HashMap<>();
        header.put(Constants.AUTH,"token " + this.token);
        NetworkController.getInstance().connect(this, WINCHESTER, Request.Method.POST, data, header, new NetworkController.ResultListener() {
            @Override
            public void onResult(int method, boolean isSuccess, JSONObject jsonObject, VolleyError volleyError, ProgressDialog progressDialog) {
                if(isSuccess){
                    try{
                        MyWallet.this.status = jsonObject.getString("status");
                        if(jsonObject.has("balance")){
                            MyWallet.this.balance = jsonObject.getString("balance");
                            SharedPreferences.Editor edit = MyWallet.this.sharedPreferences.edit();
                            edit.putString("balance", MyWallet.this.balance);
                            edit.apply();
                            MyWallet.this.walletBalance.setText("₹ " + balance);
                            MyWallet.this.msg = "Money added successfully";
                        }
                    }catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else{
                    MyWallet.this.msg = "Something went wrong";
                }
                if (progressDialog != null && progressDialog.isShowing())
                    progressDialog.dismiss();
                showDialog();
            }
        });
    }
    void showDialog(){
        new GifDialog.Builder(this)
                .setTitle("Payment message")
                .setMessage("Money added successfully")
                .setPositiveBtnBackground("#FF4081")
                .setPositiveBtnText("Ok")
                .OnPositiveClicked(new GifDialogListener() {
                    @Override
                    public void OnClick() {
                        onBackPressed();
                    }
                })
                .build();
    }
}
