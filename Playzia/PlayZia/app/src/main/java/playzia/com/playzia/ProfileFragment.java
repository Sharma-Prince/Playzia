package playzia.com.playzia;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;


public class ProfileFragment extends Fragment {

    CardView logoutCard;
    CardView walletCard;
    CardView topPlayersCard;
    CardView shareCard;
    View view;
    TextView myemail;
    TextView name;
    TextView myBalance;
    TextView matchesPlayed;
    TextView myKills;
    TextView amountWon;
    SharedPreferences sharedPreferences;
    ProgressDialog progressDialog;
    String balance;
    String email;
    String token;
    String total_amount_paid;
    String total_amount_won;
    String total_matches_playerd;
    String total_kills;
    String username;
    String versionName;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile, container, false);

        myemail = view.findViewById(R.id.myemail);
        name = view.findViewById(R.id.name);
        myBalance = view.findViewById(R.id.myBalance);
        matchesPlayed = view.findViewById(R.id.matchesPlayed);
        amountWon = view.findViewById(R.id.amountWon);
        myKills = view.findViewById(R.id.myKills);
        logoutCard = view.findViewById(R.id.logoutCard);
        walletCard = view.findViewById(R.id.walletCard);
        shareCard = view.findViewById(R.id.shareCard);
        topPlayersCard = view.findViewById(R.id.topPlayersCard);
        sharedPreferences = getActivity().getSharedPreferences(Constants.USER_DATA,0);
        this.sharedPreferences = getActivity().getSharedPreferences(Constants.USER_DATA, 0);

        this.username = this.sharedPreferences.getString("username", null);
        this.token = this.sharedPreferences.getString("token", null);
        this.email = this.sharedPreferences.getString("email", null);
        this.balance = this.sharedPreferences.getString("balance", "0");
        this.total_matches_playerd = this.sharedPreferences.getString("matchesPlayed", "0");
        this.total_kills = this.sharedPreferences.getString("myKills", "0");
        this.total_amount_won = this.sharedPreferences.getString("amountWon", "0");



        this.myemail.setText(this.email);
        this.name.setText(this.username);
        this.myBalance.setText("₹" + this.balance);
        this.matchesPlayed.setText(this.total_matches_playerd);
        this.myKills.setText(this.total_kills);
        this.amountWon.setText("₹ " + this.total_amount_won);
        this.logoutCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor edit = ProfileFragment.this.sharedPreferences.edit();
                edit.clear();
                edit.apply();
                ProfileFragment.this.getActivity().finish();
                Toast.makeText(ProfileFragment.this.getActivity(), "Logged Out Successfully!", Toast.LENGTH_SHORT).show();
                ProfileFragment.this.startActivity(new Intent(ProfileFragment.this.getActivity(),FirstScreen.class));
            }
        });

        walletCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProfileFragment.this.startActivity(new Intent(ProfileFragment.this.getActivity(),MyWallet.class));
            }
        });
        topPlayersCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProfileFragment.this.startActivity(new Intent(ProfileFragment.this.getActivity(), TopPlayers.class));
            }
        });
        shareCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("android.intent.action.SEND");
                intent.setType("text/plain");
                String string = ProfileFragment.this.getString(R.string.shareContent);
                intent.putExtra("android.intent.extra.SUBJECT", ProfileFragment.this.getString(R.string.shareSub));
                intent.putExtra("android.intent.extra.TEXT", string);
                ProfileFragment.this.startActivity(Intent.createChooser(intent, "Share using"));
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        HashMap data = new HashMap();
        data.put("username",this.username);
        data.put("token",this.token);
        HashMap header = new HashMap();
        header.put("Authorization","token " + this.token);
        NetworkController.getInstance().connect(ProfileFragment.this.getContext(),Constants.KAR98K, Request.Method.POST,data,header,new Listener());
    }
    class Listener implements NetworkController.ResultListener{

        @Override
        public void onResult(int method, boolean isSuccess, JSONObject jsonObject, VolleyError volleyError, ProgressDialog progressDialog) {
            if(isSuccess){
                setData(jsonObject);
            }else{
                Log.e("error",volleyError.toString());

            }
            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();
        }
    }

    private void setData(JSONObject jsonObject) {
        try {
            this.balance = jsonObject.getString("balance");
            this.total_kills = jsonObject.getString("myKills");
            this.total_amount_won = jsonObject.getString("amountWon");
            this.total_matches_playerd = jsonObject.getString("matchesPlayed");
            this.email = jsonObject.getString("email");
            SharedPreferences.Editor edit = ProfileFragment.this.sharedPreferences.edit();
            edit.putString("balance",this.balance);
            edit.putString("myKills",this.total_kills);
            edit.putString("amountWon",this.total_amount_won);
            edit.putString("matchesPlayed",this.total_matches_playerd);
            edit.putString("email",this.email);
            edit.apply();
            myBalance.setText("₹" + this.balance);
            myKills.setText(this.total_kills);
            matchesPlayed.setText(this.total_matches_playerd);
            amountWon.setText("₹ " + this.total_amount_won);
            this.myemail.setText(this.email);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
