package com.example.nativehelloworld;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

public class MainActivity extends Activity {

	// load the library - name matches join/Android.mk 
	static {
		System.loadLibrary("helloworld");
	}
	
	private native String invokeNativeFunction();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		int i = 0;
		String hello = invokeNativeFunction();
        
        new AlertDialog.Builder(this).setMessage(hello).show();
	}
}