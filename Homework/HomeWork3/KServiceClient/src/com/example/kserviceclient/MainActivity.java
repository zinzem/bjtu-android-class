package com.example.kserviceclient;

import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.example.kservicelib.IKService;
import com.example.kservicelib.Request;

public class MainActivity extends Activity implements ServiceConnection {

	private static final String 	TAG = "MainActivity";
	
	private IKService 				mIKService;
	
	private EditText				mEntry;
	private RadioGroup				mType;
	private TextView				mResult;
	private Button					mSend;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mEntry = (EditText) findViewById(R.id.entry);
		mType = (RadioGroup) findViewById(R.id.type);
		mResult = (TextView) findViewById(R.id.result);
		mSend = (Button) findViewById(R.id.send);
		
		mSend.setEnabled(false);
		
		mEntry.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				if (mEntry.getText().toString().length() > 0 && mIKService != null) {
					if (mType.getCheckedRadioButtonId() != -1) {
						mSend.setEnabled(true);
					}
				} else {
					mSend.setEnabled(false);
				}
			}
		});
		mType.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (mEntry.getText().toString().length() > 0 && mIKService != null) {
					mSend.setEnabled(true);
				}
			}
		});
		mSend.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (mSend.isEnabled()) {
					new Calculate().execute(Long.getLong(mEntry.getText().toString()));
				}
			}
		});
	}
	
	@Override
    protected void onResume() {
        super.onResume();
        
        Intent i = new Intent(IKService.class.getName());
        Intent i2 = explicitFromImplicitIntent(i);
        
        if (!super.bindService(i2, this, BIND_AUTO_CREATE)) {
            Log.e(TAG, "Failed to bind to service");
        }
    }

	@Override
    protected void onPause() {
        super.onPause();
        super.unbindService(this);
    }
	
	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		Log.e(TAG, "Connected !");
		mIKService = IKService.Stub.asInterface(service);
	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
		Log.e(TAG, "Bye Bye !");
		mIKService = null;
		mSend.setEnabled(false);
	}
	
	public Intent explicitFromImplicitIntent(Intent implicitIntent) {
		PackageManager pm = getPackageManager();
        List<ResolveInfo> resolveInfos = pm.queryIntentServices(implicitIntent, 0);

        if (resolveInfos == null || resolveInfos.size() != 1) {
        	Log.e(TAG, "size = " + resolveInfos.size());
            return null;
        }

        ResolveInfo serviceInfo = resolveInfos.get(0);
        String packageName = serviceInfo.serviceInfo.packageName;
        String className = serviceInfo.serviceInfo.name;
        ComponentName component = new ComponentName(packageName, className);
        Intent explicitIntent = new Intent();
        explicitIntent.setComponent(component);
        return explicitIntent;
    }
	
	class Calculate extends AsyncTask<Long, Void, Long>{
	    ProgressDialog progressDialog = null;
	    
	    @Override
	    protected void onPreExecute() {
	    	progressDialog = new ProgressDialog(MainActivity.this);
	    	progressDialog.setTitle("Please wait...");
	    	progressDialog.setMessage("Calculating...");
	    	progressDialog.setCancelable(false);                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          
	    	progressDialog.show();
	    }

	    @Override
	    protected Long doInBackground(Long... param) {
	    	Request req = new Request(Long.parseLong(mEntry.getText().toString()), mType.getCheckedRadioButtonId() == 0 ? Request.TYPE_IT : Request.TYPE_RE);
			
			try {
				Log.e(TAG, "lol");
				return mIKService.fibonacci(req).getResult();
			} catch (RemoteException e) {
				Log.e(TAG, e.getMessage());
				return (long) -1;
			}
	    }

	    @Override
	    protected void onPostExecute(Long result) {
	        progressDialog.dismiss();
	        if (result >= 0) {
	        	mResult.setText("Result = " + result);
	        }
	    }
	}
}
