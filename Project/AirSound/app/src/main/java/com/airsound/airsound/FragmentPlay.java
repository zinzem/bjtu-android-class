package com.airsound.airsound;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

public class FragmentPlay extends Fragment {

    private static final String     LOG_TAG = "FragmentPlay";

    private Activity                mActivity;

    private static File             mFile = null;

    private LinearLayout            mInsertPoint;
    private Arc                     mArc;

    private TextView                mTitle;
    private MediaPlayer             mPlayer = null;
    private boolean                 mPlaying = false;

    public static FragmentPlay newInstance(File file) {
        FragmentPlay fragment = new FragmentPlay();
        Bundle args = new Bundle();

        args.putString("filePath", file.getPath());
        fragment.setArguments(args);
        return fragment;
    }

    public FragmentPlay() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mFile = new File(getArguments().getString("filePath"));
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mPlaying) {
            stopPlaying();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflateView = inflater.inflate(R.layout.fragment_play, container, false);

        mTitle = (TextView) inflateView.findViewById(R.id.title);
        mInsertPoint = (LinearLayout) inflateView.findViewById(R.id.insertPoint);
        mArc = new Arc(mActivity);

        if (mFile != null) {
            mTitle.setText(mFile.getName());
        }
        mInsertPoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPlaying) {
                    stopPlaying();
                } else {
                    startPlaying();
                }
            }
        });
        mInsertPoint.addView(mArc, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        return inflateView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mActivity = null;
    }

    private void startPlaying() {
        if (mFile != null) {
            mPlayer = new MediaPlayer();
            try {
                mPlayer.setDataSource(mFile.getPath());
                mPlayer.prepare();
                mPlayer.start();
                new Thread(new Seeker()).start();
                mPlaying = true;
            } catch (IOException e) {
                Log.e(LOG_TAG, "prepare() failed");
            }
        } else {
            Toast.makeText(mActivity, "Error Playing", Toast.LENGTH_SHORT).show();
        }
    }

    private void stopPlaying() {
        mPlaying = false;
        if (mPlayer != null) {
            mPlayer.release();
        }
        mPlayer = null;
    }

    public class Arc extends View {

        private final RectF mOval = new RectF();
        private final Paint mPaint = new Paint();

        public Arc(Context context) {
            super(context);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeWidth(30);
            int x = getWidth();
            int y = getHeight();
            float coef = 2.3f;
            float offsetX = x / coef;
            float offsetY = y / coef;
            float offset = offsetX < offsetY ? offsetX : offsetY;
            double curentPos = mPlayer != null ? mPlayer.getCurrentPosition() : 0;
            double length = mPlayer != null ? mPlayer.getDuration() : 1;
            double angle = 5d + ((270d / length) * curentPos);

            mOval.set((x / 2) - offset, (y / 2) - offset, (x / 2) + offset, (y / 2) + offset);
            mPaint.setColor(getResources().getColor(R.color.gray));
            canvas.drawArc(mOval, -45, 270, false, mPaint);
            mPaint.setColor(getResources().getColor(R.color.airSoundYellow));
            canvas.drawArc(mOval, -45, (int) angle, false, mPaint);
        }
    }

    public class Seeker implements Runnable {

        int curentPos = 0;

        @Override
        public void run() {

            while(mPlaying) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    Log.e(LOG_TAG, e.toString());
                }

                if (!mPlaying || curentPos == mPlayer.getCurrentPosition()) {
                    stopPlaying();
                } else {
                    curentPos = mPlayer.getCurrentPosition();
                }
                mArc.post(new Runnable() {
                        @Override
                        public void run() {
                            mArc.invalidate();
                        }
                    });
            }
        }
    }
}
