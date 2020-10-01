package playzia.com.playzia;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
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


/**
 * A simple {@link Fragment} subclass.
 */
public class TransactionTab extends Fragment {
    CustomList customList;
    RecyclerView recyclerView;
    LinearLayout noTxnsLayout;
    SharedPreferences sharedPreferences;
    List<TdModle> tdModles;
    String username;
    String token;
    public TransactionTab() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_transaction_tab, container, false);
        this.sharedPreferences = getActivity().getSharedPreferences(Constants.USER_DATA, 0);
        this.token = this.sharedPreferences.getString("token",null);
        this.username = this.sharedPreferences.getString("username",null);
        this.recyclerView = view.findViewById(R.id.txnListRecyclerView);
        this.tdModles = new ArrayList();
        this.noTxnsLayout = view.findViewById(R.id.noTxnLayout);
        this.recyclerView.setHasFixedSize(true);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        HashMap<String, String> data = new HashMap<>();
        data.put("username",this.username);
        data.put("token",this.token);
        HashMap<String, String> header = new HashMap<>();
        header.put(Constants.AUTH,"token " + this.token);
        NetworkController.getInstance().connect(getContext(),Constants.SCAR_L,Request.Method.POST,data,header,new Listener());
        return view;
    }

    public class Listener implements NetworkController.ResultListener{

        @Override
        public void onResult(int method, boolean isSuccess, JSONObject jsonObject, VolleyError volleyError, ProgressDialog progressDialog) {
            if(isSuccess){
                putData(jsonObject,progressDialog);
            }else{
                if (progressDialog != null && progressDialog.isShowing())
                    progressDialog.dismiss();
            }
        }
    }

    private void putData(JSONObject jsonObject, ProgressDialog progressDialog) {
        JSONArray array = null;
         {
             try {
                 array = jsonObject.getJSONArray("data");
                 for(int i=0;i<array.length();i++) {
                     JSONObject data = array.getJSONObject(i);
                     TdModle model = new TdModle();
                     model.setAmount(data.getString("amount"));
                     model.setRemark(data.getString("remark"));
                     model.setType(data.getString("type"));
                     model.setDate(data.getString("date"));
                     this.tdModles.add(model);
                 }
             } catch (JSONException e) {
                 e.printStackTrace();
             }
             if(this.tdModles.size()==0){
                 TransactionTab.this.noTxnsLayout.setVisibility(View.VISIBLE);
                 if (progressDialog != null && progressDialog.isShowing())
                     progressDialog.dismiss();
                 return;
             }
             TransactionTab.this.customList = new CustomList(TransactionTab.this.getActivity(),TransactionTab.this.tdModles);
             TransactionTab.this.recyclerView.setAdapter(TransactionTab.this.customList);
             TransactionTab.this.customList.notifyDataSetChanged();


        }
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
    }

    public class CustomList extends RecyclerView.Adapter<CustomList.ListViewHolder> {
        Context cnt;
        List<TdModle> tdModles;

        public class ListViewHolder extends RecyclerView.ViewHolder {
            TextView txnAmount;
            TextView txnDate;
            TextView txnRemark;
            TextView txnType;

            public ListViewHolder(View view) {
                super(view);
                setIsRecyclable(false);
                this.txnType =  view.findViewById(R.id.txnType);
                this.txnRemark =  view.findViewById(R.id.txnRemark);
                this.txnDate =  view.findViewById(R.id.txnDate);
                this.txnAmount =  view.findViewById(R.id.txnAmount);
            }
        }

        public CustomList(Context context, List<TdModle> list) {
            this.cnt = context;
            this.tdModles = list;
        }

        public ListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            return new ListViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.transactions_item, viewGroup, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ListViewHolder listViewHolder, int i) {
            TdModle tdModle = (TdModle) this.tdModles.get(i);
            listViewHolder.txnDate.setText(tdModle.getDate());
            listViewHolder.txnRemark.setText(tdModle.getRemark());
            String type = tdModle.getType();
            if(type.equals("credit")){
                listViewHolder.txnType.setText(type);
                TextView textView = listViewHolder.txnAmount;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("+ ₹");
                stringBuilder.append(tdModle.getAmount());
                textView.setText(stringBuilder.toString());
            }else if (type.equals("debit")) {
                listViewHolder.txnType.setText(type);
                listViewHolder.txnType.setTextColor(Color.parseColor("#ff0000"));
                TextView textView2 = listViewHolder.txnAmount;
                StringBuilder stringBuilder2 = new StringBuilder();
                stringBuilder2.append("- ₹");
                stringBuilder2.append(tdModle.getAmount());
                textView2.setText(stringBuilder2.toString());
                listViewHolder.txnAmount.setTextColor(Color.parseColor("#ff0000"));
            }
        }

        @Override
        public int getItemCount() {
            return this.tdModles.size();
        }
    }
}
