package kr.ac.snu.cse.wifiadvisor;

import java.util.List;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class WiFiService extends Service {
	public final int TIME = 1000;
	
	// Binder given to clients
    private final IBinder wifiBinder = new WifiBinder();
    
    // WiFi
    private WifiManager wifiManager;
    private List<ScanResult> scanResultList;
    
    // Thread
    private boolean endThread;
    private BackgroundTask backgroundTask;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		//Log.i("WiFiService", "onBind");
		wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
		scanResultList = null;
		endThread = false;
	    backgroundTask = new BackgroundTask();
		backgroundTask.execute();
		
		return wifiBinder;
	}
    
    @Override
	public boolean onUnbind(Intent intent) {
		// TODO Auto-generated method stub
		//Log.i("WiFiService", "onUnbind");
    	endThread = true;
		
		return super.onUnbind(intent);
	}
    
    public List<ScanResult> getScanResultList() {
    	return scanResultList;
    }

	/*
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class WifiBinder extends Binder {
    	WiFiService getService() {
            // Return this instance of LocalService so clients can call public methods
            return WiFiService.this;
        }
    }
	
	private class BackgroundTask extends AsyncTask<String, String, Void> {
		private final String SIG_UPDATE = "update";

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			Log.i("WiFiService", "BackgroundTask/onPreExecute");
		}

		@Override
		protected Void doInBackground(String... params) {
			Log.i("WiFiService", "BackgroundTask/doInBackground");
			
			while(true) {
				wifiManager.startScan();
				scanResultList = wifiManager.getScanResults();
				publishProgress(SIG_UPDATE);
				
				try {
					Thread.sleep(TIME);
				} catch(Exception ex) {
					Log.e("WiFiService", "BackgroundTask/doInBackground", ex);
				}
				
				if(endThread) return null;
			}
		}

		@Override
		protected void onProgressUpdate(String... values) {
			super.onProgressUpdate(values);
			Log.i("WiFiService", "BackgroundTask/onProgressUpdate");
			
			if(values[0].equals(SIG_UPDATE)) {
				sendBroadcast(new Intent("android.intent.action.WIFI"));
			}

		}
		
	}

}
