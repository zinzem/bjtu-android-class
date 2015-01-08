package com.airsound.airsound;

import android.content.Context;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.airsound.airsound.entities.Record;

import java.io.IOException;
import java.util.List;

public class LocalListAdapter extends ArrayAdapter<Record>{

    public LocalListAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public LocalListAdapter(Context context, int resource, List<Record> items) {
        super(context, resource, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;

        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.local_list_item, null);
        }

        final Record rec = getItem(position);
        MediaPlayer player = new MediaPlayer();

        if (rec != null) {
            TextView title = (TextView) view.findViewById(R.id.title);
            TextView duration = (TextView) view.findViewById(R.id.duration);
            String durationString = "";

            try {
                player.setDataSource(rec.mLocation.getPath());
                player.prepare();
            } catch (IOException e) {

            }
            if (player != null) {
                int sec = player.getDuration() / 1000;
                int min = sec / 60;

                durationString = (min < 9 ? "0" + min : min) + ":" + (sec < 9 ? "0" + sec : sec);
            }

            title.setText(rec.mTitle);
            duration.setText(durationString);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((MainActivity) getContext()).getFragmentManager().beginTransaction().replace(R.id.content, new FragmentPlay().newInstance(rec.mLocation)).commit();
                }
            });
        }

        return view;

    }
}
