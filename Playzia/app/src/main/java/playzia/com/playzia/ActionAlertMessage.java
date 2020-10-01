package playzia.com.playzia;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;


class ActionAlertMessage {
    private Context context;
    private String pubgUsername;
    private String match_id;
    private String token;
    private String matchType;
    String username;

    void showJoinMatchAlert(Context context, final String matchType, final String match_id, final String token, final String username){
        View inflate = LayoutInflater.from(context).inflate(R.layout.joinprompt, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(inflate);
        builder.setCancelable(false);
        final AlertDialog create = builder.create();
        Button next = inflate.findViewById(R.id.next);
        Button cancel = inflate.findViewById(R.id.cancel);
        this.context = context;
        this.match_id = match_id;
        this.token = token;
        this.username = username;
        this.matchType = matchType;
        final TextInputEditText gameID = inflate.findViewById(R.id.gameID);
        final TextView textView = inflate.findViewById(R.id.textError);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                create.cancel();
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String obj = gameID.getText().toString();
                if (obj.isEmpty() || obj.contains(" ")) {
                    textView.setVisibility(View.VISIBLE);
                    textView.setText("Invalid PUBG Username. Retry.");
                    return;
                }
                ActionAlertMessage.this.pubgUsername = gameID.getText().toString();
                JoinMatch();
            }
        });
        create.show();

    }
    private void JoinMatch(){
        HashMap<String, String> data = new HashMap<>();
        data.put("match_id",this.match_id);
        data.put("matchType",this.matchType);
        data.put("pubgUsername",this.pubgUsername);
        data.put("username",this.username);
        data.put("token",this.token);
        HashMap<String, String> header = new HashMap<>();
        header.put(Constants.AUTH,"token " + token);
        NetworkController.getInstance().connect(context, Constants.FLARES, Request.Method.POST, data, header, new NetworkController.ResultListener() {
            @Override
            public void onResult(int method, boolean isSuccess, JSONObject jsonObject, VolleyError volleyError, ProgressDialog progressDialog) {
                if(isSuccess){
                    try {
                        String status = jsonObject.getString("status");
                        String msg = jsonObject.getString("msg");
                        new GifDialog.Builder((Activity)ActionAlertMessage.this.context)
                                .setTitle("")
                                .setMessage(msg)
                                .setPositiveBtnBackground("#FF4081")
                                .setPositiveBtnText("Ok")
                                .OnPositiveClicked(new GifDialogListener() {
                                    @Override
                                    public void OnClick() {
                                        ((Activity)ActionAlertMessage.this.context).finish();
                                    }
                                })
                                .build();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if (progressDialog != null && progressDialog.isShowing())
                    progressDialog.dismiss();
            }
        });
    }
    public void CAlertDialog(Context context, String msg) {

    }
}
