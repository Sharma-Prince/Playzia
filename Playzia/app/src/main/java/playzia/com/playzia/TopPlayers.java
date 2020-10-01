package playzia.com.playzia;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static playzia.com.playzia.Constants.USER_DATA;

public class TopPlayers extends AppCompatActivity {

    RecyclerView recyclerView;
    SharedPreferences sharedPreferences;
    String token;
    CustomList customList;
    List<ModelPlayers> topPlayers;
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_players);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle((CharSequence)"Top Players");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        this.sharedPreferences = getSharedPreferences(USER_DATA,0);
        this.token = this.sharedPreferences.getString("token",null);
        this.recyclerView =  findViewById(R.id.topPlayersListRecyclerView);
        this.topPlayers = new ArrayList();
        this.recyclerView.setHasFixedSize(true);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(this));

        HashMap header = new HashMap();
        header.put("Authorization","token " + this.token);
        NetworkController.getInstance().connect(this, Constants.TOMMY_GUN, Request.Method.GET, null, header, new NetworkController.ResultListener() {
            @Override
            public void onResult(int method, boolean isSuccess, JSONObject jsonObject, VolleyError volleyError, ProgressDialog progressDialog) {
                if(isSuccess){
                    putData(jsonObject,progressDialog);
                }else{
                    if (progressDialog != null && progressDialog.isShowing())
                        progressDialog.dismiss();
                }
            }
        });
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void putData(JSONObject jsonObject, ProgressDialog progressDialog) {
        try{
            JSONArray array = new JSONArray(jsonObject.getString("data"));
            for(int i=0;i<array.length();i++){
                JSONObject data = array.getJSONObject(i);
                ModelPlayers modelPlayers = new ModelPlayers();
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(data.getString("firstName"));
                stringBuilder.append(" ");
                stringBuilder.append(data.getString("lastName"));
                modelPlayers.setPlayerName(stringBuilder.toString());
                modelPlayers.setPlayerWinning(data.getString("amountWon"));
                modelPlayers.setPosition(String.valueOf(i+1));
                this.topPlayers.add(modelPlayers);
            }
            this.customList = new CustomList(this,this.topPlayers);
            this.customList.notifyDataSetChanged();
            this.recyclerView.setAdapter(this.customList);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
    }


    public class CustomList extends RecyclerView.Adapter<CustomList.ListViewHolder> {
        Context context;
        List<ModelPlayers> topPlayers;

        public class ListViewHolder extends RecyclerView.ViewHolder {
            TextView name;
            TextView position;
            TextView winning;

            public ListViewHolder(View view) {
                super(view);
                this.position =  view.findViewById(R.id.playerPosition);
                this.name =  view.findViewById(R.id.playerName);
                this.winning =  view.findViewById(R.id.playerWinning);
            }
        }

        public CustomList(Context context, List<ModelPlayers> list) {
            this.context = context;
            this.topPlayers = list;
        }

        public ListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            return new ListViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.topplayers_item, viewGroup, false));
        }

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
