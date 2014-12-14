package com.example.kservice;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.example.kservicelib.IKService;
import com.example.kservicelib.Request;
import com.example.kservicelib.Response;

public class KService extends Service {

	private static final String 	TAG = "KService";
	private IKService.Stub 			mBinder = new IKService.Stub() {

		@Override
		public Response fibonacci(Request request) {
	        switch (request.getType()) {
	            case Request.TYPE_IT:
	            	return new Response(fibonacciIterrative(request.getValue()));
	            case Request.TYPE_RE:
	            	return new Response(fibonacciRecursive(request.getValue()));
	            default:
	                return null;
	        	}
	      	}
    };
    
	@Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "create");
    }

    @Override
    public IBinder onBind(Intent intent) {
    	Log.d(TAG, "bind");
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "unbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "destroy");
        this.mBinder = null;
        super.onDestroy();
    }
    
    public static long fibonacciRecursive(long param) {
        return param <= 0 ? 0 : param == 1 ? 1 : fibonacciRecursive(param - 1) + fibonacciRecursive(param - 2);
    }
    
    public static long fibonacciIterrative(long param) {
        long prev = -1;
        long curr = 1;
        
        for (long i = 0; i <= param; i++) {
            long sum = prev + curr;
            
            prev = curr;
            curr = sum;
        }
        return curr;
    }
}
