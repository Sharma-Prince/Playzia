package playzia.com.playzia;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.support.v7.app.AlertDialog.Builder;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static playzia.com.playzia.Constants.USER_DATA;

public class MatchDetails extends AppCompatActivity {

    NestedScrollView nestedScrollView;
    Button joinButton;
    ArrayList<String> pnames;
    TextView refreshLV;
    String token;
    String username;
    String match_id;
    SharedPreferences sharedPreferences;
    ListView lvParticipants;
    ListView listRulesDetails;
    Button loadParticipants;
    TextView version;
    TextView winPrize;
    TextView type;
    TextView startTime;
    TextView title;
    TextView map;
    TextView fee;
    TextView perKillPrize;
    ImageView matchImage;
    String matchID;
    String matchentryFree;
    String matchwinPrize;
    String matchperKill;
    String matchtotalPeopleJoined;
    String matchMap;
    String matchtitle;
    String matchType;
    String matchVersion;
    String matchtimedata;
    String roomID;
    String passID;
    String imgUrl;
    ArrayList<String> rule;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_details);
        this.nestedScrollView = findViewById(R.id.nestedScroll);
        this.sharedPreferences = getSharedPreferences(USER_DATA,0);
        this.token = this.sharedPreferences.getString("token",null);
        this.username = this.sharedPreferences.getString("username",null);
        this.lvParticipants = findViewById(R.id.listParticipants);
        this.listRulesDetails = (ListView) findViewById(R.id.listRules);
        this.pnames = new ArrayList<>();
        this.rule = new ArrayList<>();
        this.lvParticipants.setDivider(null);
        this.listRulesDetails.setDivider(null);
        this.loadParticipants = findViewById(R.id.loadBtn);
        this.refreshLV = findViewById(R.id.refreshLVBtn);
        this.match_id = getIntent().getStringExtra("matchID");
        this.matchImage = findViewById(R.id.matchImage);
        this.startTime = findViewById(R.id.startdate);
        this.title =  findViewById(R.id.title);
        this.type =  findViewById(R.id.type);
        this.version =  findViewById(R.id.version);
        this.map =  findViewById(R.id.map);
        this.fee =  findViewById(R.id.fee);
        this.winPrize =  findViewById(R.id.winnerPrize);
        this.perKillPrize =  findViewById(R.id.perKillPrize);
        this.joinButton = findViewById(R.id.JoinButton);
        this.rule.add("•  Please note that the listed entry fee is per individual.");
        this.rule.add("•  Room ID and Password will be shared in the app, 15 minutes before the match start time.");
        this.rule.add("•  If you joined the custom room do not keep changing your position. If you do so, you may get kicked from the room.");
        this.rule.add("•  Do not share the Room ID & Password with any other person who has not joined the match. Otherwise,your account may get terminated and all of the winnings will be lost.");
        this.rule.add("•  If in any case you failed to join the room by the match start time then we are not responsible for it. Refund in such cases will not be proceeded. So make sure to join on time.");
        this.rule.add("•  Use only Mobile Device to join match. Use of any hacks or emulators are not allowed.");
        listRulesDetails.setAdapter(new ArrayAdapter<>(MatchDetails.this,R.layout.participantslistview,MatchDetails.this.rule));
        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MatchDetails.this,JoinActivity.class);
                intent.putExtra("matchID", MatchDetails.this.match_id);
                intent.putExtra("amountPay", MatchDetails.this.matchentryFree);
                intent.putExtra("matchType", MatchDetails.this.matchType);
                MatchDetails.this.startActivity(intent);
            }
        });
        loadParticipants.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MatchDetails.this.refreshLV.setVisibility(View.VISIBLE);
                MatchDetails.this.lvParticipants.setVisibility(View.VISIBLE);
                MatchDetails.this.loadParticipants.setVisibility(View.GONE);
            }
        });
        refreshLV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MatchDetails.this.pnames.clear();
                ParticipantsList();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        HashMap<String, String> data = new HashMap<>();
        data.put("username",this.username);
        data.put("token",this.token);
        data.put("match_id",this.match_id);
        HashMap<String, String> header = new HashMap<>();
        header.put("Authorization","token "+this.token);
        NetworkController.getInstance().connect(this,Constants.COMPENSATOR,Request.Method.POST,data,header,new MatchListener());
    }

    public void RoomDetailsDialog(final Context context){
        View view = LayoutInflater.from(context).inflate(R.layout.room_details, null);
        Builder builder = new Builder(context);
        builder.setView(view);
        this.pnames = new ArrayList<>();
        TextView roomIDtxt = view.findViewById(R.id.roomIDValue);
        TextView roomPasstxt = view.findViewById(R.id.roomPassValue);
        roomIDtxt.setText(this.roomID);
        roomPasstxt.setText(this.passID);
        Button play =  view.findViewById(R.id.play);
        Button cancel =  view.findViewById(R.id.cancel);
        builder.setCancelable(false);
        final AlertDialog create = builder.create();



        create.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                return false;
            }
        });
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MatchDetails.this.launchPUBGMobile(MatchDetails.this);
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                create.cancel();
            }
        });
        create.show();
    }
    public void launchPUBGMobile(Context context) {
        Intent launchIntentForPackage = context.getPackageManager().getLaunchIntentForPackage("com.tencent.ig");
        if (launchIntentForPackage != null) {
            context.startActivity(launchIntentForPackage);
        } else {
            Toast.makeText(context, "PUBGMobile Not Installed", 0).show();
        }
    }
    public void ParticipantsList(){
        HashMap<String , String> data = new HashMap<>();
        data.put("username",this.username);
        data.put("token",this.token);
        data.put("match_id",this.match_id);
        HashMap<String , String> header = new HashMap<>();
        header.put("Authorization","token "+this.token);
        NetworkController.getInstance().connect(this, Constants.MACHINE_GUN, Request.Method.POST, data, header, new NetworkController.ResultListener() {
            @Override
            public void onResult(int method, boolean isSuccess, JSONObject jsonObject, VolleyError volleyError, ProgressDialog progressDialog) {
                if(isSuccess){
                    JSONArray array = null;
                    try {
                        array = new JSONArray(jsonObject.getString("data"));
                        for(int i=0;i<array.length();i++){
                            String pubgusername = array.getJSONObject(i).getString("pubgusername");
                            MatchDetails.this.pnames.add(pubgusername);
                        }
                        lvParticipants.setAdapter(new ArrayAdapter<>(MatchDetails.this,R.layout.participantslistview,MatchDetails.this.pnames));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if (progressDialog != null && progressDialog.isShowing())
                    progressDialog.dismiss();
            }
        });

    }
    class MatchListener implements NetworkController.ResultListener{

        @Override
        public void onResult(int method, boolean isSuccess, JSONObject jsonObject, VolleyError volleyError, ProgressDialog progressDialog) {
            if(isSuccess){
                try {
                    JSONObject data = jsonObject.getJSONObject("data");
                    matchID = data.getString("matchID");
                    matchentryFree = data.getString("entryFree");
                    matchwinPrize = data.getString("winPrize");
                    matchperKill = data.getString("perKill");
                    matchtotalPeopleJoined = data.getString("totalPeopleJoined");
                    matchMap = data.getString("matchMap");
                    matchtitle = data.getString("title");
                    matchType = data.getString("matchType");
                    imgUrl = data.getString("img");
                    matchVersion = data.getString("matchVersion");
                    matchtimedata = data.getString("timedata");
                    matchtimedata = matchtimedata.replace("T"," at ");
                    matchtimedata = matchtimedata.replace("Z","");
                    roomID = jsonObject.getString("roomID");
                    passID = jsonObject.getString("passID");


                    startTime.setText(matchtimedata);
                    title.setText(matchtitle);
                    type.setText(matchType);
                    version.setText(matchVersion);
                    map.setText(matchMap);

                    StringBuilder sd = new StringBuilder();
                    sd.append("₹ ");
                    sd.append(matchwinPrize);
                    winPrize.setText(sd.toString());
                    sd = new StringBuilder();
                    sd.append("₹ ");
                    sd.append(matchperKill);
                    perKillPrize.setText(sd.toString());
                    sd = new StringBuilder();
                    sd.append("₹ ");
                    sd.append(matchentryFree);
                    fee.setText(sd.toString());
                    if(!imgUrl.isEmpty()){
                        Picasso.get().load(imgUrl).into(matchImage);
                    }
                    if(!roomID.isEmpty()){
                        MatchDetails.this.RoomDetailsDialog(MatchDetails.this);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    if (progressDialog != null && progressDialog.isShowing())
                        progressDialog.dismiss();
                }

            }
            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();
        }
    }
}
