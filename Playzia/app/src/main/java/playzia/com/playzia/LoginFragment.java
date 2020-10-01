package playzia.com.playzia;



import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import android.content.SharedPreferences.Editor;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;

import static playzia.com.playzia.Constants.USER_DATA;


/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {

    Boolean loginPost = Boolean.valueOf(false);
    Button btnLogin;
    TextView reset;
    TextInputEditText lUsername;
    TextInputEditText lPassword;
    TextView lLoginError;
    Button btnNoAccount;
    String balance;
    String firstName;
    String lastName;
    String phone;
    String status;
    String username;
    String password;
    String token;
    String email;
    SharedPreferences sp;


    class Click implements View.OnClickListener{

        @Override
        public void onClick(View v) {

            switch (v.getId()){
                case R.id.btnNoAccount:
                    FragmentTransaction beginTransaction = LoginFragment.this.getFragmentManager().beginTransaction();
                    beginTransaction.setCustomAnimations(R.anim.anim_slide_in_left, R.anim.anim_slide_out_left, R.anim.anim_slide_in_right, R.anim.anim_slide_out_right);
                    beginTransaction.addToBackStack(null);
                    beginTransaction.replace(R.id.frameContainer, new RegisterFragment());
                    beginTransaction.commit();
                    break;
                case R.id.btnLogin:
                    sendData();
                    break;
            }
        }
    }


    public LoginFragment() {
        // Required empty public constructor
    }

    public LoginFragment(Boolean bool) {
        this.loginPost = bool;
    }


    private void sendData() {
        lLoginError.setVisibility(View.INVISIBLE);
        this.username = this.lUsername.getText().toString();
        this.password = this.lPassword.getText().toString();
        String url = Constants.M762;
        if (validateusername() && validatepassword()) {
            HashMap<String, String> userdata = new HashMap<>();
            userdata.put("username",this.username);
            userdata.put("password",this.password);
            NetworkController.getInstance().connect(LoginFragment.this.getContext(), url, Request.Method.POST, userdata, null, new NetworkController.ResultListener() {
                @Override
                public void onResult(int method, boolean isSuccess, JSONObject jsonObject, VolleyError volleyError, ProgressDialog progressDialog) {
                    if(isSuccess){
                        try {
                            String status = jsonObject.getString("status");
                            if(status.equals("1")){
                                loginData(jsonObject);
                            }else{
                                String msg = "Unable to login with given credentials.";
                                lLoginError.setText(msg);
                                lLoginError.setVisibility(View.VISIBLE);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else{
                        String msg = "Unable to  with given credentials.";
                        lLoginError.setText(msg);
                        lLoginError.setVisibility(View.VISIBLE);
                    }
                    if (progressDialog != null && progressDialog.isShowing())
                        progressDialog.dismiss();
                }
            });
        }
    }

    private void loginData(JSONObject jsonObject) {
        try {
            this.phone = jsonObject.getString("phone");
            this.firstName = jsonObject.getString("firstName");
            this.lastName = jsonObject.getString("lastName");
            this.token = jsonObject.getString("token");
            this.balance = jsonObject.getString("balance");
            LoginFragment.this.sp = LoginFragment.this.getActivity().getSharedPreferences(USER_DATA,0);
            Editor edit = LoginFragment.this.sp.edit();
            edit.putString("username",this.username);
            edit.putString("token",this.token);
            edit.putString("lastName",this.lastName);
            edit.putString("email", this.email);
            edit.putString("phone", this.phone);
            edit.putString("balance",this.balance);
            edit.putBoolean("isDataAvailable",true);
            edit.apply();
            String msg = "Login Successful.";
            Toast.makeText(LoginFragment.this.getActivity(),msg,Toast.LENGTH_SHORT).show();
            LoginFragment.this.startActivity(new Intent(LoginFragment.this.getActivity(),MainActivity.class));
            LoginFragment.this.getActivity().finish();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public boolean validateusername() {
        if (this.username.isEmpty()) {
            this.lUsername.setError("Enter Username");
            return false;
        } else if (this.username.matches("[a-zA-Z0-9]*")) {
            return true;
        } else {
            this.lUsername.setError("No special characters allowed");
            return false;
        }
    }

    public boolean validatepassword() {
        if (!this.password.isEmpty()) {
            return true;
        }
        this.lPassword.setError("Enter Password");
        return false;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_login, container, false);
        btnNoAccount =  view.findViewById(R.id.btnNoAccount);
        btnNoAccount.setOnClickListener(new Click());
        btnLogin = view.findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new Click());
        lUsername = view.findViewById(R.id.lUsername);
        lPassword = view.findViewById(R.id.lPassword);
        lLoginError = view.findViewById(R.id.lLoginError);
        reset = view.findViewById(R.id.txtReset);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showForgotPassword(LoginFragment.this.getActivity());
            }
        });
        return view;
    }
    public void showForgotPassword(Context context){
        View view = LayoutInflater.from(context).inflate(R.layout.forgotpassword,null);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);
        builder.setCancelable(false);
        final TextInputEditText textInputEditText = (TextInputEditText) view.findViewById(R.id.emailAddress);
        final TextView textView = (TextView) view.findViewById(R.id.passwordResetResponse);
        Button reset = (Button) view.findViewById(R.id.reset);
        Button cancel = (Button) view.findViewById(R.id.cancel);
        final AlertDialog create = builder.create();
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                create.dismiss();
            }
        });
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = textInputEditText.getText().toString();
                if(email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches());
                    textView.setText("Enter Valid Email Address");
                    textView.setTextColor(Color.parseColor("#ff0000"));
                    textView.setVisibility(0);
                    return;
            }
        });
        create.show();
    }

}
