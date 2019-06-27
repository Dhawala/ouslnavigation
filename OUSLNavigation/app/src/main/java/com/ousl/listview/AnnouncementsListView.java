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

public class AnnouncementsListView extends ArrayAdapter<String> {

    private String course_code[], ac_name[], message[];
    private Activity context;

    public AnnouncementsListView(Activity context, String course_code[], String ac_name[], String message[]) {
        super(context, R.layout.layout_upcoming_schedule, course_code);
        this.context = context;
        this.course_code = course_code;
        this.ac_name = ac_name;
        this.message = message;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View r = convertView;
        AnnouncementsListView.ViewHolder viewHolder = null;

        if(r == null){
            LayoutInflater layoutInflater = context.getLayoutInflater();
            r = layoutInflater.inflate(R.layout.layout_listview_announcements, null, true);
            viewHolder = new AnnouncementsListView.ViewHolder(r);
            r.setTag(viewHolder);
        }
        else{
            viewHolder = (AnnouncementsListView.ViewHolder) r.getTag();
        }

        viewHolder.title.setText(course_code[position]+" "+ac_name[position]+" is cancelled");
        viewHolder.message.setText(message[position]);

        return r;
    }

    class ViewHolder{
        TextView title;
        TextView message;

        ViewHolder(View v){
            title = (TextView) v.findViewById(R.id.notice_txt_title);
            message = (TextView) v.findViewById(R.id.notice_txt_message);
        }
    }

}
