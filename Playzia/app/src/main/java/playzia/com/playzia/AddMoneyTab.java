package playzia.com.playzia;


import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddMoneyTab extends Fragment {


    Button addButton;
    EditText amountEditText;
    TextView errorMessage;


    public AddMoneyTab() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_money_tab, container, false);
        this.addButton = view.findViewById(R.id.addButton);
        this.amountEditText = view.findViewById(R.id.amountEditText);
        this.errorMessage = view.findViewById(R.id.errorMessage);


        this.addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String obj = AddMoneyTab.this.amountEditText.getText().toString().trim();
                if(!obj.isEmpty()){
                    int value = Integer.parseInt(obj);
                    if(value<20){
                        AddMoneyTab.this.errorMessage.setVisibility(View.VISIBLE);
                    }else if(value>=20){
                        AddMoneyTab.this.errorMessage.setVisibility(View.GONE);
                        ((MyWallet) AddMoneyTab.this.getActivity()).PaytmAddMoney(obj);
                    }
                }else{
                    AddMoneyTab.this.errorMessage.setVisibility(0);
                    AddMoneyTab.this.errorMessage.setText("Enter minimum Rs 20");
                    AddMoneyTab.this.errorMessage.setTextColor(Color.parseColor("#ff0000"));
                }
            }
        });
        return view;
    }

}
