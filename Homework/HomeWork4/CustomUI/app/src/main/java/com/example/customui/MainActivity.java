package com.example.customui;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends ActionBarActivity {

    private static final int    TYPE_CLOCK = 42;

    private GridView            mGrid;

    private int[] icons = {R.drawable.store, R.drawable.chrome, TYPE_CLOCK, R.drawable.gmail, R.drawable.camera, R.drawable.flappyman};
    private String[] titles = new String[] {"Store", "Chrome", "Clock", "Gmail", "Camera", "Flappy Man"};

    private String              mDate = null;
    private String              mHour = null;
    private String              mSeconds = null;
    private Clock          mClockHolder = null;

    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (mClockHolder != null) {
                if (mClockHolder.date != null && mDate != null) {
                    mClockHolder.date.setText(mDate);
                }
                if (mClockHolder.hour != null && mHour != null) {
                    mClockHolder.hour.setText(mHour);
                }
                if (mClockHolder.seconds != null && mSeconds != null) {
                    mClockHolder.seconds.setText(mSeconds);
                }
            }
        };
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mGrid = (GridView) findViewById(R.id.grid);
        mGrid.setAdapter(new GridAdapter());
        mGrid.setOnItemClickListener(new GridItemClickListener());

        Timer timer = new Timer();
        TimerTask task = new TimerTask() {

            @Override
            public void run() {
                long time = System.currentTimeMillis();
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                SimpleDateFormat hourFormat = new SimpleDateFormat("hh:mm");
                SimpleDateFormat secondsFormat = new SimpleDateFormat("ss");

                mDate = dateFormat.format(time);
                mHour = hourFormat.format(time);
                mSeconds = secondsFormat.format(time);
                handler.sendEmptyMessage(0);
            }
        };
        timer.schedule(task, 0, 1000);
    }

    private class GridItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Toast.makeText(MainActivity.this, titles[position], Toast.LENGTH_SHORT).show();
        }

    }

    private class GridAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return icons.length;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;

            if (icons[position] != TYPE_CLOCK) {
                view = View.inflate(MainActivity.this, R.layout.grid_item, null);
                ImageView icon = (ImageView) view.findViewById(R.id.icon);
                TextView title = (TextView) view.findViewById(R.id.title);

                icon.setImageResource(icons[position]);
                title.setText(titles[position]);
            } else {
                view = View.inflate(MainActivity.this, R.layout.grid_item_clock, null);
                mClockHolder = new Clock();

                mClockHolder.date = (TextView) view.findViewById(R.id.date);
                mClockHolder.hour = (TextView) view.findViewById(R.id.hour);
                mClockHolder.seconds = (TextView) view.findViewById(R.id.seconds);
            }

            return view;
        }

        @Override
        public Object getItem(int position) {
            return titles[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

    }

    class Clock {
        TextView    date;
        TextView    hour;
        TextView    seconds;
    }
}
