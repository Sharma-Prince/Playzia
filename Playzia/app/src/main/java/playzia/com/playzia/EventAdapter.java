package playzia.com.playzia;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;



public class EventAdapter extends ArrayAdapter<DataModel>{

    ArrayList<DataModel> dataSet;
    Context mContext;


    private static class ViewHolder {
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
    }

    public EventAdapter(ArrayList<DataModel> data, Context context) {
        super(context, R.layout.row_item, data);
        this.dataSet = data;
        this.mContext=context;

    }


    public void onClick(View v) {
        int position=(Integer) v.getTag();
        Object object= getItem(position);
        DataModel dataModel=(DataModel)object;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        DataModel dataModel = getItem(position);
        ViewHolder viewHolder;
        final View result;
        if(convertView == null){
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.row_item, parent, false);

            viewHolder.title = (TextView) convertView.findViewById(R.id.title);
            viewHolder.timedate = (TextView)convertView.findViewById(R.id.timedate);
            viewHolder.winPrize = (TextView)convertView.findViewById(R.id.winPrize);
            viewHolder.perKill = (TextView)convertView.findViewById(R.id.perKill);
            viewHolder.entryFee = (TextView)convertView.findViewById(R.id.entryFee);
            viewHolder.matchType = (TextView)convertView.findViewById(R.id.matchType);
            viewHolder.matchVersion = (TextView)convertView.findViewById(R.id.matchVersion);
            viewHolder.matchMap = (TextView)convertView.findViewById(R.id.matchMap);
            viewHolder.spots = (TextView)convertView.findViewById(R.id.spots);
            viewHolder.size =(TextView)convertView.findViewById(R.id.size);
            result = convertView;
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }
        viewHolder.title.setText(dataModel.getTitle());
        viewHolder.timedate.setText(dataModel.getTimedate());
        viewHolder.winPrize.setText(dataModel.getWinPrize());
        viewHolder.perKill .setText(dataModel.getPerKill());
        viewHolder.entryFee.setText(dataModel.getEntryFee());
        viewHolder.matchType.setText(dataModel.getMatchType());
        viewHolder.matchVersion.setText(dataModel.getMatchVersion());
        viewHolder.matchMap.setText(dataModel.getMatchMap());
        viewHolder.size.setText(dataModel.getSize());
        viewHolder.spots.setText(dataModel.getSpots());
        return convertView;
    }
}
