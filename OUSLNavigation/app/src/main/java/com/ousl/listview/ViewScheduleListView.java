package com.ousl.listview;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ousl.ouslnavigation.R;

public class ViewScheduleListView extends ArrayAdapter<String> {

    private String ac_name[], medium[], group[], date[], start_time[], end_time[], centre[], location[], room[];
    private Activity context;

    public ViewScheduleListView(Activity context, String ac_name[], String medium[], String group[], String date[], String start_time[], String end_time[], String centre[], String location[], String room[]) {
        super(context, R.layout.layout_view_schedule, ac_name);
        this.context = context;
        this.ac_name = ac_name;
        this.medium = medium;
        this.group = group;
        this.date = date;
        this.start_time = start_time;
        this.end_time = end_time;
        this.centre = centre;
        this.location = location;
        this.room = room;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View r = convertView;
        ViewHolder viewHolder = null;

        if(r == null){
            LayoutInflater layoutInflater = context.getLayoutInflater();
            r = layoutInflater.inflate(R.layout.layout_view_schedule, null, true);
            viewHolder = new ViewHolder(r);
            r.setTag(viewHolder);
        }
        else{
            viewHolder = (ViewHolder) r.getTag();
        }

        viewHolder.ac_name.setText(ac_name[position]);
        viewHolder.medium.setText("Medium: "+medium[position]);
        viewHolder.group.setText("Group: "+group[position]);
        viewHolder.date.setText("Date: "+date[position]);
        viewHolder.time.setText("Time: "+start_time[position]+" - "+end_time[position]);
        viewHolder.centre.setText("Centre: "+centre[position]);

        if(!location[position].equals("null")){
            viewHolder.location.setText("Location: "+location[position]+" - "+room[position]);
        }
        else{
            viewHolder.location.setText("Location: Not yet decided.");
        }

        return r;
    }

    class ViewHolder{
        TextView ac_name;
        TextView medium;
        TextView group;
        TextView date;
        TextView time;
        TextView centre;
        TextView location;

        ViewHolder(View v){
            ac_name = (TextView) v.findViewById(R.id.view_ac_name);
            medium = (TextView) v.findViewById(R.id.view_medium);
            group = (TextView) v.findViewById(R.id.view_group);
            date = (TextView) v.findViewById(R.id.view_date);
            time = (TextView) v.findViewById(R.id.view_time);
            centre = (TextView) v.findViewById(R.id.view_centre);
            location = (TextView) v.findViewById(R.id.view_location);
        }
    }
}
