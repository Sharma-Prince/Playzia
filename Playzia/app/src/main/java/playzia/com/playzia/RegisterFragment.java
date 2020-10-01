package playzia.com.playzia;


import android.app.ProgressDialog;
import android.os.Bundle;

import android.util.Log;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterFragment extends Fragment {

    Button btnHaveAccount;
    Button btnRegister;
    TextView signError;
    String firstName;
    TextInputEditText rFirstName;
    String lastName;
    TextInputEditText rLastName;
    String username;
    TextInputEditText rUserName;
    String email;
    TextInputEditText rEmail;
    String phone;
    TextInputEditText rPhone;
    String password;
    TextInputEditText rPassword;
    String result;


    public RegisterFragment() {
        // Required empty public constructor
    }

    class Click implements View.OnClickListener{

        @Override
        public void onClick(View v) {

            switch (v.getId()){
                case R.id.btnHaveAccount:
                    FragmentTransaction beginTransaction = RegisterFragment.this.getFragmentManager().beginTransaction();
                    beginTransaction.setCustomAnimations(R.anim.anim_slide_in_right, R.anim.anim_slide_out_right, R.anim.anim_slide_in_left, R.anim.anim_slide_out_left);
                    beginTransaction.replace(R.id.frameContainer, new LoginFragment(false));
                    beginTransaction.commit();
                    break;
                case R.id.btnRegister:
                    submitData();
                    break;
            }
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        btnHaveAccount = view.findViewById(R.id.btnHaveAccount);
        btnHaveAccount.setOnClickListener(new Click());
        btnRegister = view.findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(new Click());
        this.rUserName = view.findViewById(R.id.rUserName);
        this.rEmail = view.findViewById(R.id.rEmail);
        this.rFirstName = view.findViewById(R.id.rFirstName);
        this.rLastName = view.findViewById(R.id.rLastName);
        this.rPhone = view.findViewById(R.id.rPhone);
        this.rPassword = view.findViewById(R.id.rPassword);
        this.signError = view.findViewById(R.id.signError);
        return view;
    }
    public void submitData(){

        this.username = this.rUserName.getText().toString().trim();
        this.email = this.rEmail.getText().toString().trim();
        this.firstName = this.rFirstName.getText().toString().trim();
        this.lastName = this.rLastName.getText().toString().trim();
        this.phone = this.rPhone.getText().toString().trim();
        this.password = this.rPassword.getText().toString().trim();
        if (validatefirstname() && validatelastname() && validateemail() && validateusername() && validatepassword() && validatemobilenumber()) {


            String url = Constants.FLARE_GUN;

            HashMap userdata = new HashMap();
            userdata.put("firstName",this.firstName);
            userdata.put("lastName",this.lastName);
            userdata.put("phone",this.phone);
            userdata.put("email",this.email);
            userdata.put("password",this.password);
            userdata.put("username",this.username);
            NetworkController.getInstance().connect(RegisterFragment.this.getContext(), url, Request.Method.POST, userdata,null, new NetworkController.ResultListener() {
                @Override
                public void onResult(int method, boolean isSuccess, JSONObject jsonObject, VolleyError volleyError, ProgressDialog progressDialog) {

                    if(isSuccess){
                        Log.e("error",jsonObject.toString());
                        try {
                            String status = jsonObject.getString("status");
                            String msg = jsonObject.getString("msg");
                            if(status.equals("1")){
                                Toast.makeText(RegisterFragment.this.getContext(),msg, Toast.LENGTH_LONG).show();
                                FragmentTransaction beginTransaction = RegisterFragment.this.getFragmentManager().beginTransaction();
                                beginTransaction.setCustomAnimations(R.anim.anim_slide_in_right, R.anim.anim_slide_out_right, R.anim.anim_slide_in_left, R.anim.anim_slide_out_left);
                                beginTransaction.replace(R.id.frameContainer, new LoginFragment(true));
                                beginTransaction.commit();
                            }else if(status.equals("0")){
                                showError(msg);
                                Toast.makeText(RegisterFragment.this.getContext(),"Please try again...", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else{
                        Toast.makeText(RegisterFragment.this.getContext(),"Something went wrong", Toast.LENGTH_LONG).show();
                    }

                    if (progressDialog != null && progressDialog.isShowing())
                        progressDialog.dismiss();
                }
            });

        }

    }
    public void showError(String msg) {
        JSONObject error = null;
        try {
            error = new JSONObject(msg);
            if (error.has("username")){
                RegisterFragment.this.rUserName.setError(error.get("username").toString());
                RegisterFragment.this.rUserName.requestFocus();
            }if(error.has("email")){
                RegisterFragment.this.rEmail.setError(error.get("email").toString());
                RegisterFragment.this.rEmail.requestFocus();
            }if(error.has("password")){
                RegisterFragment.this.rPassword.setError(error.get("password").toString());
                RegisterFragment.this.rPassword.requestFocus();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public boolean validatefirstname() {
        if (!this.firstName.isEmpty()) {
            return true;
        }
        this.rFirstName.setError("Enter First Name");
        this.rFirstName.requestFocus();
        return false;
    }

    public boolean validatelastname() {
        if (!this.lastName.isEmpty()) {
            return true;
        }
        this.rLastName.setError("Enter Last Name");
        this.rLastName.requestFocus();
        return false;
    }

    public boolean isvalideemail(String str) {
        return Patterns.EMAIL_ADDRESS.matcher(str).matches();
    }

    public boolean validateemail() {
        this.email = this.rEmail.getText().toString();
        if (!this.email.isEmpty() && isvalideemail(this.email)) {
            return true;
        }
        this.rEmail.setError("Enter Valid Email Address");
        this.rEmail.requestFocus();
        return false;
    }

    public boolean validateusername() {
        this.username = this.rUserName.getText().toString();
        if (this.username.matches("[a-zA-Z0-9]*")) {
            return true;
        }
        this.rUserName.setError("No special characters allowed");
        this.rUserName.requestFocus();
        return false;
    }
    public boolean validatepassword() {
        if (!this.password.isEmpty()) {
            return true;
        }
        this.rPassword.setError("Enter Password");
        this.rPassword.requestFocus();
        return false;
    }

    public boolean validatemobilenumber() {
        if (this.rPhone.length() <= 10 && this.rPhone.length() >= 10) {
            return true;
        }
        this.rPhone.setError("Mobile Number should be of 10 Digits");
        this.rPhone.requestFocus();
        return false;
    }
}
