package playzia.com.playzia;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.android.volley.Request;
import com.android.volley.VolleyError;
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


public class ResultFragment extends Fragment {
    View view;
    List<DataModel> events;
    RecyclerView recyclerView;
    CustomList customList;
    String token;
    String username;
    AlertDialog.Builder builder;
    ResultListener resultListener;
    AlertDialog alertDialog;
    SharedPreferences sharedPreferences;
    RecyclerView resultRecyclerView;
    List<ModelPlayers> resultModelPlayersList;
    List<ModelPlayers> resutl1;
    List<ModelPlayers> resutl2;
    LinearLayout noMatches;
    public ResultFragment() {

    }

    @Override
    public void onResume() {
        super.onResume();
        super.onResume();
        this.events = new ArrayList<>();
        String URL = Constants.M249;
        HashMap<String, String> data = new HashMap<>();
        data.put("username",this.username);
        data.put("token",this.token);
        HashMap<String, String> header = new HashMap<>();
        header.put("Authorization","token "+this.token);
        Log.e("token " ,this.token);
        NetworkController.getInstance().connect(ResultFragment.this.getContext(),URL, Request.Method.POST,data,header,resultListener);
    }

    private void putData(JSONObject object,ProgressDialog progressDialog) {
        try {
            JSONArray array = new JSONArray(object.getString("data"));
            JSONArray p1 = new JSONArray(object.getString("result2"));
            for(int i=0;i<p1.length();i++){
                JSONObject data = p1.getJSONObject(i);
                ModelPlayers modelPlayers = new ModelPlayers();
                modelPlayers.setPlayerName(data.getString("matchKill"));
                modelPlayers.setPlayerWinning(data.getString("moneyEarn"));
                modelPlayers.setPosition(data.getString("pubgusername"));
                resutl1.add(modelPlayers);
            }
            p1 = new JSONArray(object.getString("result1"));
            for(int i=0;i<p1.length();i++){
                JSONObject data = p1.getJSONObject(i);
                ModelPlayers modelPlayers = new ModelPlayers();
                modelPlayers.setPlayerName(data.getString("matchKill"));
                modelPlayers.setPlayerWinning(data.getString("moneyEarn"));
                modelPlayers.setPosition(data.getString("pubgusername"));
                resutl2.add(modelPlayers);
            }
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    class ResultListener implements NetworkController.ResultListener, View.OnClickListener {

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_result, container, false);
        this.events = new ArrayList<>();
        this.recyclerView = view.findViewById(R.id.recyclerViewResult);
        this.recyclerView.setHasFixedSize(true);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        noMatches = view.findViewById(R.id.noMatches);
        this.sharedPreferences = getActivity().getSharedPreferences(USER_DATA,0);
        this.token = this.sharedPreferences.getString("token",null);
        this.username = this.sharedPreferences.getString("username",null);
        resultListener  = new ResultListener();
        Rect displayRectangle = new Rect();
        Window window = this.getActivity().getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
        this.builder = new AlertDialog.Builder(ResultFragment.this.getContext(),R.style.CustomAlertDialog);
        ViewGroup viewGroup = view.findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(view.getContext()).inflate(R.layout.result_player, viewGroup, false);
        builder.setView(dialogView);
        this.alertDialog = builder.create();
        resutl1  = new ArrayList<>();
        resutl2  = new ArrayList<>();
        resultRecyclerView = (RecyclerView)dialogView.findViewById(R.id.resultPlayersListRecyclerView);
        resultRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return view;
    }
    public class CustomList extends RecyclerView.Adapter<CustomList.ListViewHolder> {
        Context context;
        List<DataModel> result;

        public class ListViewHolder extends RecyclerView.ViewHolder{
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
            Button btnResult;
            ImageView img;
            ImageView banner;

            public ListViewHolder(View view) {
                super(view);
                this.cardView = view.findViewById(R.id.mainCard);
                this.banner = view.findViewById(R.id.mainTopBanner);
                this.title = view.findViewById(R.id.title);
                this.progressBar = view.findViewById(R.id.progressBar);
                this.timedate = view.findViewById(R.id.timedate);
                this.winPrize = view.findViewById(R.id.winPrize);
                this.perKill = view.findViewById(R.id.perKill);
                this.entryFee = view.findViewById(R.id.entryFee);
                this.matchType = view.findViewById(R.id.matchType);
                this.matchVersion = view.findViewById(R.id.matchVersion);
                this.matchMap = view.findViewById(R.id.matchMap);
                this.spots = view.findViewById(R.id.spots);
                this.size = view.findViewById(R.id.size);
                this.btnResult = view.findViewById(R.id.btnJoin);
                this.img = view.findViewById(R.id.img);
            }
        }
        public CustomList(Context context, List<DataModel> list) {
            this.context = context;
            this.result = list;
        }
        public ListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            return new ListViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_item, viewGroup, false));
        }
        public void onBindViewHolder(@NonNull ListViewHolder listViewHolder, int i) {

            final DataModel model = (DataModel) this.result.get(i);

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
            listViewHolder.btnResult.setText("Result");
            final int i_no=i;
            listViewHolder.btnResult.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showPopup(i_no);

                }
            });


        }
        public int getItemCount() {
            Log.d("List Size", String.valueOf(this.result.size()));
            return this.result.size();
        }
    }

    private void showPopup(int i) {
        //alertDialog.show();
        if(i==0)
            resultModelPlayersList=resutl1;
        else
            resultModelPlayersList=resutl2;
        ResultCustomList resultCustomList = new ResultCustomList(getContext(),resultModelPlayersList);
        resultRecyclerView.setAdapter(resultCustomList);
        resultCustomList.notifyDataSetChanged();
        alertDialog.show();
    }

    public class ResultCustomList extends RecyclerView.Adapter<ResultCustomList.ListViewHolder> {
        Context context;
        List<ModelPlayers> topPlayers;

        public class ListViewHolder extends RecyclerView.ViewHolder {
            TextView name;
            TextView position;
            TextView winning;

            public ListViewHolder(View view) {
                super(view);
                this.position =  view.findViewById(R.id.rName);
                this.name =  view.findViewById(R.id.rKill);
                this.winning =  view.findViewById(R.id.rWinning);
            }
        }

        public ResultCustomList(Context context, List<ModelPlayers> list) {
            this.context = context;
            this.topPlayers = list;
        }

        public ListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            return new ListViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.result_player_item, viewGroup, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ListViewHolder listViewHolder, int i) {
            ModelPlayers players = (ModelPlayers) this.topPlayers.get(i);
            listViewHolder.name.setText(players.getPlayerName());
            listViewHolder.position.setText(players.getPosition());
            listViewHolder.winning.setText(players.getPlayerWinning());
        }
        public int getItemCount() {
            Log.d("List Size", String.valueOf(this.topPlayers.size()));
            return this.topPlayers.size();
        }
    }

}
