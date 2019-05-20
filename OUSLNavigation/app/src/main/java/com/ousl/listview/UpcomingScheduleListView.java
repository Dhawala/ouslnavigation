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

public class UpcomingScheduleListView extends ArrayAdapter<String> {

    private String course_code[], ac_name[], medium[], group[], date[], start_time[], end_time[], centre[], location[], room[];
    private Activity context;

    public UpcomingScheduleListView(Activity context, String course_code[], String ac_name[], String medium[], String group[], String date[], String start_time[], String end_time[], String centre[], String location[], String room[]) {
        super(context, R.layout.layout_upcoming_schedule, course_code);
        this.context = context;
        this.course_code = course_code;
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
            r = layoutInflater.inflate(R.layout.layout_upcoming_schedule, null, true);
            viewHolder = new ViewHolder(r);
            r.setTag(viewHolder);
        }
        else{
            viewHolder = (ViewHolder) r.getTag();
        }

        viewHolder.course_code.setText(course_code[position]);
        viewHolder.ac_name.setText(ac_name[position]);

        if(medium[position].equals("E")){
            viewHolder.medium.setText("Medium: English");
        }
        else if(medium[position].equals("S")){
            viewHolder.medium.setText("Medium: Sinhala");
        }
        else if(medium[position].equals("T")){
            viewHolder.medium.setText("Medium: Tamil");
        }
        else{
            viewHolder.medium.setText("Medium: Not mentioned.");
        }

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
        TextView course_code;
        TextView ac_name;
        TextView medium;
        TextView group;
        TextView date;
        TextView time;
        TextView centre;
        TextView location;

        ViewHolder(View v){
            course_code = (TextView) v.findViewById(R.id.upcoming_course_code);
            ac_name = (TextView) v.findViewById(R.id.upcoming_ac_name);
            medium = (TextView) v.findViewById(R.id.upcoming_medium);
            group = (TextView) v.findViewById(R.id.upcoming_group);
            date = (TextView) v.findViewById(R.id.upcoming_date);
            time = (TextView) v.findViewById(R.id.upcoming_time);
            centre = (TextView) v.findViewById(R.id.upcoming_centre);
            location = (TextView) v.findViewById(R.id.upcoming_location);
        }
    }
}
