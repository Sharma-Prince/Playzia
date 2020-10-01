package playzia.com.playzia;



import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.lang.annotation.Retention;
import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * Created by Prince on 29/12/18.
 */

public class NetworkController {

    private final String TAG = "NetworkController";

    boolean showDialog = true;
    private ProgressDialog progressDialog;

    private static NetworkController controller = new NetworkController();

    public static NetworkController getInstance()
    {
        return controller;
    }


    /**
     * Call this method if you want to show/hide loader
     *
     * @param showDialog default value = true
     *
     * */
    public void showDialog(boolean showDialog) {
        this.showDialog = showDialog;
    }


    /**
     * This method is responsible to communicate with server
     *
     * @param context current class context
     * @param method GET or POST
     * @param requestCode to identify request
     * @param resultListener to get callback for response
     * @param stringParams can be null if method is GET
     *
     * */
    public void connect(Context context, final String url, final int method, HashMap<String, String> stringParams, HashMap<String, String> header, final ResultListener resultListener)
    {
        try
        {
            if (CheckNetworkState.isOnline(context)) {
                if (showDialog)
                    showDialog(context);


                NetworkRequest networkRequest = new NetworkRequest(context, url, method, stringParams, header, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        Log.e(TAG, "onResponse() called");
                        resultListener.onResult(method, true, jsonObject, null, progressDialog);
                    }
                },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e(TAG, "onErrorResponse() called");
                                resultListener.onResult(method, false, null, error, progressDialog);
                                error.printStackTrace();
                            }
                        });

                networkRequest.setRetryPolicy(new DefaultRetryPolicy(50000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                Volley.newRequestQueue(context).add(networkRequest);
            }
            else
            {
                Toast.makeText(context, "Network not available!", Toast.LENGTH_SHORT).show();
            }


        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }



    private void showDialog(Context context)
    {
        try
        {
            if (progressDialog != null && progressDialog.isShowing())
            {
                progressDialog.dismiss();
            }

            progressDialog = new ProgressDialog(context);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Please wait...");
            progressDialog.show();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public interface ResultListener {

        void onResult(int method, boolean isSuccess, JSONObject jsonObject, VolleyError volleyError, ProgressDialog progressDialog);

    }

}