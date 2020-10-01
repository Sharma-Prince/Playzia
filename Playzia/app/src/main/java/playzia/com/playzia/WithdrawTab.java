package playzia.com.playzia;


import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 */
public class WithdrawTab extends Fragment {

    TextInputEditText paytmNumber;
    TextInputEditText amountWithdraw;
    Button withdrawButton;
    String balance;
    float withdrawalAmount;
    SharedPreferences sharedPreferences;
    String paytmNo;
    String AmountToWithdraw;
    String token;
    String username;
    TextView errorMessage;

    public WithdrawTab() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_withdraw_tab, container, false);
        paytmNumber = view.findViewById(R.id.paytmNumber);
        amountWithdraw = view.findViewById(R.id.amountWithdraw);
        withdrawButton = view.findViewById(R.id.withdrawButton);
        errorMessage = view.findViewById(R.id.errorMsg);



        this.sharedPreferences = getActivity().getSharedPreferences(Constants.USER_DATA, 0);
        this.username = this.sharedPreferences.getString("username", null);
        this.token = this.sharedPreferences.getString("token", null);
        this.paytmNo = this.sharedPreferences.getString("paytmnumber", null);
        this.balance = this.sharedPreferences.getString("balance", null);

        withdrawButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WithdrawTab.this.paytmNo = WithdrawTab.this.paytmNumber.getText().toString();
                WithdrawTab.this.AmountToWithdraw = WithdrawTab.this.amountWithdraw.getText().toString();
                if (WithdrawTab.this.AmountToWithdraw.isEmpty()) {
                    WithdrawTab.this.errorMessage.setVisibility(View.VISIBLE);
                    WithdrawTab.this.errorMessage.setText("Enter Withdrawal Amount");
                    WithdrawTab.this.errorMessage.setTextColor(Color.parseColor("#ff0000"));
                    return;
                }
                WithdrawTab.this.withdrawalAmount = Float.valueOf(WithdrawTab.this.AmountToWithdraw);
                WithdrawTab.this.submitWithdrawData();
            }
        });
        return view;
    }
    private void submitWithdrawData() {
        this.errorMessage.setVisibility(View.INVISIBLE);
        if (validatePaytmNumber() && validateWithdrawalAmount() && validateBalance()) {
            if (CheckNetworkState.isOnline(getContext())) {
                HashMap<String, String> data = new HashMap<>();
                data.put("username",this.username);
                data.put("token",this.token);
                data.put("paytm_no",this.paytmNo);
                data.put("amount", this.AmountToWithdraw);
                HashMap<String, String> header = new HashMap<>();
                header.put(Constants.AUTH,"token " + this.token);
                NetworkController.getInstance().connect(WithdrawTab.this.getActivity(),Constants.CROSSBOW,Request.Method.POST,data,header,new Listener());
            } else {
                Toast.makeText(getActivity(), "No internet connection", Toast.LENGTH_SHORT).show();
            }
        }
    }

    class Listener implements NetworkController.ResultListener{

        @Override
        public void onResult(int method, boolean isSuccess, JSONObject jsonObject, VolleyError volleyError, ProgressDialog progressDialog) {
            if(isSuccess){
                try {
                    String status = jsonObject.getString("status");
                    String txn = jsonObject.getString("txn");
                    if(status.equals("1")&&txn.equals("success")){
                        String balance = jsonObject.getString("balance");
                        new GifDialog.Builder(WithdrawTab.this.getActivity())
                                .setTitle("Payment message")
                                .setMessage("Money will we transferred within 24 hour.")
                                .setPositiveBtnBackground("#FF4081")
                                .setPositiveBtnText("Ok")
                                .OnPositiveClicked(new GifDialogListener() {
                                    @Override
                                    public void OnClick() {
                                        WithdrawTab.this.getActivity().finish();
                                    }
                                })
                                .build();

                    }else{
                        new GifDialog.Builder(WithdrawTab.this.getActivity())
                                .setTitle("Payment message")
                                .setMessage("Not have enough balance in wallet. ")
                                .setPositiveBtnBackground("#FF4081")
                                .setPositiveBtnText("Ok")
                                .OnPositiveClicked(new GifDialogListener() {
                                    @Override
                                    public void OnClick() {
                                        WithdrawTab.this.getActivity().finish();
                                    }
                                })
                                .build();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();
        }
    }

    public boolean validateWithdrawalAmount() {
        if (this.withdrawalAmount >= 50) {
            return true;
        }
        this.errorMessage.setText("Minimum withdrawal amount is ₹ 50.");
        this.errorMessage.setTextColor(Color.parseColor("#ff0000"));
        this.errorMessage.setVisibility(View.VISIBLE);
        return false;
    }
    public boolean validateBalance(){
        if(Float.valueOf(this.balance)>=50){
            return true;
        }
        this.errorMessage.setText("You don't have sufficient balance.");
        this.errorMessage.setTextColor(Color.parseColor("#ff0000"));
        this.errorMessage.setVisibility(View.VISIBLE);
        return false;
    }
    public boolean validatePaytmNumber() {
        if (this.paytmNo.length() <= 10 && this.paytmNo.length() >= 10) {
            return true;
        }
        this.paytmNumber.setError("Paytm Number should be of 10 Digits");
        return false;
    }


}
