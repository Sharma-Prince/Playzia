package playzia.com.playzia;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;

import com.android.volley.Request;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CheckUpdater implements NetworkController.ResultListener{

    private String currentVersion;
    private String latestVersion;
    private String updateInfo;
    private String updateURL;
    private String updatedOn;
    Context context;

    CheckUpdater(){

    }
    public void checkAppUpdate(Context context) {
        this.context = context;
        currentVersion = BuildConfig.VERSION_NAME;
        NetworkController.getInstance().connect(this.context,Constants.FRAG_GRENADA, Request.Method.GET,null,null,this);
    }

    @Override
    public void onResult(int method, boolean isSuccess, JSONObject jsonObject, VolleyError volleyError, ProgressDialog progressDialog) {
        if(isSuccess){
            try {
                JSONArray array = new JSONArray(jsonObject.getString("data"));
                JSONObject object = array.getJSONObject(0);
                this.latestVersion = object.getString("version");
                this.updatedOn = object.getString("date");
                this.updateInfo = object.getString("updateinfo");
                this.updateURL = object.getString("updateurl");
                if(!latestVersion.equals(currentVersion)){
                    showUpdate();
                }else {
                    return;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
    }
    private void showUpdate(){
        Intent intent = new Intent(this.context,UpdateApp.class);
        intent.putExtra("version",this.latestVersion);
        intent.putExtra("date",this.updatedOn);
        intent.putExtra("updateinfo",this.updateInfo);
        intent.putExtra("updateurl",this.updateURL);
        this.context.startActivity(intent);
    }
}
