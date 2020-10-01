package playzia.com.playzia;



import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static playzia.com.playzia.Constants.USER_DATA;


public class PlayFragment extends Fragment {

    View view;
    PlayListener playListener;
    SharedPreferences sharedPreferences;
    String token;
    String username;
    String balance;
    RecyclerView recyclerView;
    CustomList customList;
    List<DataModel> events;
    LinearLayout noMatches;
    public PlayFragment() {

    }

    class PlayListener implements NetworkController.ResultListener, View.OnClickListener {

        @Override
        public void onResult(int method, boolean isSuccess, JSONObject jsonObject, VolleyError volleyError, ProgressDialog progressDialog) {
            if(isSuccess){
                putData(jsonObject,progressDialog);
            }else{
                if (progressDialog != null && progressDialog.isShowing())
                    progressDialog.dismiss();
            }
        }

        @Override
        public void onClick(View v) {

        }
    }


    private void putData(JSONObject object,ProgressDialog progressDialog) {
        try {
            balance = object.getString("balance");
            SharedPreferences.Editor edit = PlayFragment.this.sharedPreferences.edit();
            edit.putString("balance", balance);
            edit.apply();
            JSONArray array = new JSONArray(object.getString("data"));
            for(int i=0;i<array.length();i++){
                JSONObject data = array.getJSONObject(i);
                DataModel model = new DataModel();
                StringBuilder builder = new StringBuilder();
                builder.append("₹ ");
                builder.append(data.getString("entryFree"));
                model.setEntryFee(builder.toString());
                model.setAmountPay(data.getString("entryFree"));
                builder = new StringBuilder();
                builder.append("₹ ");
                builder.append(data.getString("winPrize"));
                model.setWinPrize(builder.toString());
                builder = new StringBuilder();
                builder.append("₹ ");
                builder.append(data.getString("perKill"));
                model.setPerKill(builder.toString());

                builder = new StringBuilder();
                builder.append("Time: ");
                String time = data.getString("timedata");
                time = time.replace("T"," ");
                time = time.replace("Z","");

                DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                DateFormat outputformat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss aa");
                Date date = null;
                String output = null;
                date= df.parse(time);
                output = outputformat.format(date);
                builder.append(output.substring(0,10)+" at "+ output.substring(10,22));
                model.setTimedate(builder.toString());
                model.setMatchMap(data.getString("matchMap"));
                model.setMatchID(data.getString("matchID"));
                model.setBanner(data.getString("img"));
                builder = new StringBuilder();
                builder.append(data.getString("title"));
                model.setTitle(builder.toString());
                model.setMatchVersion(data.getString("matchVersion"));
                model.setMatchType(data.getString("matchType"));
                int td = Integer.parseInt(data.getString("totalPeopleJoined")); // td = totalPeopleJoined
                model.setTotalPeopleJoined(td);
                builder = new StringBuilder();
                builder.append(td);
                builder.append("/100");
                model.setSize(builder.toString());
                builder = new StringBuilder();
                builder.append("Only ");
                builder.append(String.valueOf(100-td));
                builder.append(" spots left");
                model.setSpots(builder.toString());
                this.events.add(model);
            }
            if(this.events.size()==0){
                noMatches.setVisibility(View.VISIBLE);
            }else{
                this.customList = new CustomList(this.getContext(),this.events);
                this.recyclerView.setAdapter(this.customList);
                this.customList.notifyDataSetChanged();
            }

        } catch (JSONException | ParseException e) {
            e.printStackTrace();
        }
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view =  inflater.inflate(R.layout.fragment_play, container, false);
        playListener = new PlayListener();
        this.sharedPreferences = getActivity().getSharedPreferences(USER_DATA,0);
        this.token = this.sharedPreferences.getString("token",null);
        this.username = this.sharedPreferences.getString("username",null);
        noMatches = view.findViewById(R.id.noMatches);
        this.events = new ArrayList<>();
        this.recyclerView = view.findViewById(R.id.recyclerView);
        this.recyclerView.setHasFixedSize(true);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        this.events = new ArrayList<>();
        String URL = Constants.AWM;
        HashMap<String, String> data = new HashMap<>();
        data.put("username",this.username);
        data.put("token",this.token);
        HashMap<String, String> header = new HashMap<>();
        header.put("Authorization","token "+this.token);
        Log.e("token " ,this.token);
        NetworkController.getInstance().connect(PlayFragment.this.getContext(),URL,Request.Method.POST,data,header,playListener);
    }

    public class CustomList extends Adapter<CustomList.ListViewHolder> {
        Context context;
        List<DataModel> event;

        public class ListViewHolder extends ViewHolder {
            CardView cardView;
            ProgressBar progressBar;
            TextView title;
            TextView timedate;
            TextView winPrize;
            TextView perKill;
            TextView entryFee;
            TextView matchType;
            TextView matchVersion;
            TextView matchMap;
            TextView spots;
            TextView size;
            Button btnJoin;
            ImageView img;
            ImageView banner;

            public ListViewHolder(View view) {
                super(view);
                this.cardView = view.findViewById(R.id.mainCard);
                this.banner = view.findViewById(R.id.mainTopBanner);
                this.progressBar = view.findViewById(R.id.progressBar);
                this.title = view.findViewById(R.id.title);
                this.timedate = view.findViewById(R.id.timedate);
                this.winPrize = view.findViewById(R.id.winPrize);
                this.perKill = view.findViewById(R.id.perKill);
                this.entryFee = view.findViewById(R.id.entryFee);
                this.matchType = view.findViewById(R.id.matchType);
                this.matchVersion = view.findViewById(R.id.matchVersion);
                this.matchMap = view.findViewById(R.id.matchMap);
                this.spots = view.findViewById(R.id.spots);
                this.size = view.findViewById(R.id.size);
                this.progressBar = view.findViewById(R.id.progressBar);
                this.btnJoin = view.findViewById(R.id.btnJoin);
                this.btnJoin = view.findViewById(R.id.btnJoin);
                this.img = view.findViewById(R.id.img);
            }
        }


        public CustomList(Context context, List<DataModel> list) {
            this.context = context;
            this.event = list;
        }

        public ListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            return new ListViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_item, viewGroup, false));
        }

        public void onBindViewHolder(@NonNull ListViewHolder listViewHolder, int i) {

            final DataModel model = (DataModel) this.event.get(i);

            listViewHolder.winPrize.setText(model.getWinPrize());
            listViewHolder.entryFee.setText(model.getEntryFee());
            listViewHolder.perKill.setText(model.getPerKill());
            listViewHolder.matchMap.setText(model.getMatchMap());
            listViewHolder.matchVersion.setText(model.getMatchVersion());
            listViewHolder.matchType.setText(model.getMatchType());
            listViewHolder.title.setText(model.getTitle());
            listViewHolder.timedate.setText(model.getTimedate());
            listViewHolder.size.setText(model.getSize());
            listViewHolder.spots.setText(model.getSpots());
            listViewHolder.progressBar.setProgress(model.getTotalPeopleJoined());

            if(!model.getBanner().isEmpty()){
                Picasso.get().load(model.getBanner()).into(listViewHolder.banner);
                listViewHolder.banner.setVisibility(View.VISIBLE);
            }
            final int totalPeopleJoined = model.getTotalPeopleJoined();
            listViewHolder.progressBar.setProgress(model.getTotalPeopleJoined());
            if(totalPeopleJoined >= 100){
                listViewHolder.spots.setTextColor(Color.parseColor("#ff0000"));
                listViewHolder.btnJoin.setText("MATCH FULL");
                listViewHolder.spots.setText("No Spots Left! Match is Full.");
                listViewHolder.btnJoin.setTextColor(Color.parseColor("#ffffff"));
                listViewHolder.btnJoin.setBackgroundResource(R.drawable.buttonbackactive);
            }
            listViewHolder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(PlayFragment.this.getContext(),MatchDetails.class);
                    intent.putExtra("matchID", model.getMatchID());
                    PlayFragment.this.startActivity(intent);
                }
            });
            listViewHolder.btnJoin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(totalPeopleJoined >= 100){
                        Toast.makeText(PlayFragment.this.getActivity(),"No Spots Left! Match is Full",Toast.LENGTH_LONG).show();
                        return;
                    }
                    Intent intent = new Intent(PlayFragment.this.getContext(),JoinActivity.class);
                    intent.putExtra("matchID", model.getMatchID());
                    intent.putExtra("dataTime",model.getTimedate());
                    intent.putExtra("amountPay", model.getAmountPay());
                    intent.putExtra("matchType", model.getMatchType());
                    PlayFragment.this.startActivity(intent);
                }
            });

        }
        public int getItemCount() {
            Log.d("List Size", String.valueOf(this.event.size()));
            return this.event.size();
        }
    }


}
