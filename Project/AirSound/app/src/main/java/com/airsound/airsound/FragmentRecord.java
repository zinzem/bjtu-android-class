package com.airsound.airsound;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;

import com.airsound.airsound.database.entities.Settings;
import com.airsound.airsound.helper.DriveContentsHelper;
import com.google.android.gms.drive.DriveId;

import java.io.File;
import java.io.IOException;
import java.util.Date;

public class FragmentRecord extends Fragment implements IDriveFragment {

    private static final String     TAG = "FragmentRecord";

    private Activity                mActivity;

    private static File             mFile;

    private RelativeLayout          mRecorderLayout;
    private LinearLayout            mSaverLayout;
    private LinearLayout            mInsertPoint;
    private Circle                  mCircle;
    private MediaRecorder           mRecorder = null;
    private boolean                 mRecording = false;

    private Button                  mAction;
    private Switch                  mLocalStorage;
    private LinearLayout            mCloudStorageLayout;
    private Switch                  mCloudStorage;

    private ImageView               mStatus;

    private int                     mAmplitude;
    private static final int        DEFAULT_SIZE = 200;

    public static FragmentRecord newInstance() {
        FragmentRecord fragment = new FragmentRecord();

        return fragment;
    }

    public FragmentRecord() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAmplitude = DEFAULT_SIZE;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mRecording) {
            stopRecording();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflateView = inflater.inflate(R.layout.fragment_record, container, false);

        mStatus = (ImageView) inflateView.findViewById(R.id.status);
        mInsertPoint = (LinearLayout) inflateView.findViewById(R.id.insertPoint);
        mCircle = new Circle(mActivity);

        mRecorderLayout = (RelativeLayout) inflateView.findViewById(R.id.recorder);
        mSaverLayout = (LinearLayout) inflateView.findViewById(R.id.saver);
        mLocalStorage = (Switch) inflateView.findViewById(R.id.localStorage);
        mCloudStorageLayout = (LinearLayout) inflateView.findViewById(R.id.cloudStorageLayout);
        mCloudStorage = (Switch) inflateView.findViewById(R.id.cloudStorage);
        mAction = (Button) inflateView.findViewById(R.id.action);

        mInsertPoint.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSaverLayout.getVisibility() == View.GONE) {
                    if (mRecording) {
                        stopRecording();
                    } else {
                        startRecording();
                    }
                }
            }
        });
        mInsertPoint.addView(mCircle, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        mLocalStorage.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked && !mCloudStorage.isChecked()) {
                    mAction.setText("Cancel");
                    mAction.setBackgroundResource(R.color.airSoundRed);
                } else {
                    mAction.setText("Save");
                    mAction.setBackgroundResource(R.color.airSoundGreen);
                }
            }
        });
        mCloudStorage.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked && !mLocalStorage.isChecked()) {
                    mAction.setText("Cancel");
                    mAction.setBackgroundResource(R.color.airSoundRed);
                } else {
                    mAction.setText("Save");
                    mAction.setBackgroundResource(R.color.airSoundGreen);
                }
            }
        });
        mAction.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation disappear = AnimationUtils.loadAnimation(mActivity, R.anim.layout_disappear);

                if (mCloudStorage.isChecked()) {
                    Log.e(TAG, "Gonna create");
                    new DriveContentsHelper((MainActivity) mActivity).createFile(DriveId.decodeFromString(Settings.getCloudRootFolder()), mFile);
                }
                if (!mLocalStorage.isChecked()) {
                    mFile.delete();
                }


                mSaverLayout.setAnimation(disappear);
                mSaverLayout.getAnimation().setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        mLocalStorage.setChecked(true);
                        mCloudStorage.setChecked(false);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });
                mSaverLayout.setVisibility(View.GONE);
            }
        });

        return inflateView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
        mActivity.setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mActivity.setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        mActivity = null;
    }

    private void startRecording() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        if (Settings.getEncoding() == Settings.EEncoding.MP4) {
            mFile = new File(Settings.getLocalRootFolder() + "/" + new Date().getTime() + ".mp4");
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        } else {
            mFile = new File(Settings.getLocalRootFolder() + "/" + new Date().getTime() + ".3gp");
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        }
        mRecorder.setOutputFile(mFile.getAbsolutePath());
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(TAG, "prepare() failed");
        }

        mRecorder.start();
        new Thread(new AmplitudeMeasurer()).start();
        mRecording = true;
    }

    private void stopRecording() {
        Animation appear = AnimationUtils.loadAnimation(mActivity, R.anim.layout_appear);

        mRecording = false;
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;

        mSaverLayout.setVisibility(View.VISIBLE);
        mSaverLayout.setAnimation(appear);

    }

    @Override
    public void onConnected() {
        if (mStatus != null) {
            mStatus.setVisibility(View.VISIBLE);
        }
        if (mCloudStorageLayout != null) {
            mCloudStorageLayout.setVisibility(View.VISIBLE);
            if (mCloudStorage != null) {
                mCloudStorage.setChecked(false);
            }
        }
    }

    @Override
    public void onDisconnected() {
        if (mStatus != null) {
            mStatus.setVisibility(View.INVISIBLE);
        }
        if (mCloudStorageLayout != null) {
            mCloudStorageLayout.setVisibility(View.GONE);
            if (mCloudStorage != null) {
                mCloudStorage.setChecked(false);
            }
        }
    }

    public class Circle extends View {

        public Circle(Context context) {
            super(context);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            int x = getWidth();
            int y = getHeight();
            double radius = 3 * Math.sqrt(mAmplitude);
            Paint paint = new Paint();

            radius = radius < DEFAULT_SIZE ? DEFAULT_SIZE : radius;
            if (mRecording) {
                paint.setColor(getResources().getColor(R.color.red));
            } else {
                paint.setColor(getResources().getColor(R.color.airSoundYellow));
            }
            canvas.drawCircle(x / 2, y / 2, (int )radius, paint);
        }
    }

    public class AmplitudeMeasurer implements Runnable {

        @Override
        public void run() {

            while(mRecording) {
                try {
                    Thread.sleep(30);
                } catch (InterruptedException e) {
                   Log.e(TAG, e.toString());
                }
                if (mRecorder != null && mRecording) {
                    mAmplitude = mRecorder.getMaxAmplitude();
                    Log.i("AMPLITUDE", new Integer(mAmplitude).toString());
                }
                mCircle.post(new Runnable() {
                    @Override
                    public void run() {
                        mCircle.invalidate();
                    }
                });
            }
            mAmplitude = DEFAULT_SIZE;
        }
    }
}
