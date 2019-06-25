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

public class RoomListView extends ArrayAdapter<String> {

    private String rooms[];
    private Activity context;

    public RoomListView(Activity context, String rooms[]) {
        super(context, R.layout.layout_upcoming_schedule, rooms);
        this.context = context;
        this.rooms = rooms;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View r = convertView;
        RoomListView.ViewHolder viewHolder = null;

        if(r == null){
            LayoutInflater layoutInflater = context.getLayoutInflater();
            r = layoutInflater.inflate(R.layout.layout_listview_rooms, null, true);
            viewHolder = new RoomListView.ViewHolder(r);
            r.setTag(viewHolder);
        }
        else{
            viewHolder = (RoomListView.ViewHolder) r.getTag();
        }

        viewHolder.rooms.setText(rooms[position]);

        return r;
    }

    class ViewHolder{
        TextView rooms;

        ViewHolder(View v){
            rooms = (TextView) v.findViewById(R.id.listitem_roomname);
        }
    }

}
