package kr.ac.snu.cse.wifiadvisor;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

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
	private long lastScanTime = 0;
	boolean scanning = false;
	
	// Binder given to clients
    private final IBinder wifiBinder = new WifiBinder();
    
    // WiFi
    private WifiManager wifiManager;
    private List<ScanResult> scanResultList;
    
    // Thread
    private BackgroundTask backgroundTask;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		Log.i("WiFiService", "onBind");
		wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
		scanResultList = new ArrayList<ScanResult>();
		
		return wifiBinder;
	}
    
    @Override
	public boolean onUnbind(Intent intent) {
    	if (backgroundTask != null)
    	{
    		backgroundTask.cancel(false);		
    	}
		return super.onUnbind(intent);
	}
    
    public List<ScanResult> getScanResultList() {
    	return scanResultList;
    }
    
    public long getLastScanTime () {
    	return this.lastScanTime;
    }
    
    public void startScan ()
    {
    	backgroundTask = new BackgroundTask ();
    	backgroundTask.execute ();
    }
    
    public void stopScan ()
    {
    	if (backgroundTask != null)
    	{
    		backgroundTask.cancel(false);
    	}
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
		Timer scanTimer;
		TimerTask scanTask;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			Log.i("WiFiService", "BackgroundTask/onPreExecute");
			
			initializeTimer ();
		}

		@Override
		protected Void doInBackground(String... params) {
			Log.i("WiFiService", "BackgroundTask/doInBackground");
			
			while (!isCancelled())
			{}
			
			return null;
		}

		@Override
		protected void onProgressUpdate(String... values) {
			super.onProgressUpdate(values);
			Log.i("WiFiService", "BackgroundTask/onProgressUpdate");
			
			if(values[0].equals(SIG_UPDATE)) {
				sendBroadcast(new Intent("android.intent.action.WIFI"));
			}

		}
		
		@Override
		protected void onCancelled (Void result)
		{
			cancelTimer ();
		}
		
		@Override
		protected void onPostExecute (Void result)
		{
			cancelTimer ();
		}
		
		private void initializeTimer ()
		{
			scanTask = new TimerTask ()
			{
				@Override
				public void run () {
					Log.i("WiFiService/TimerTask", "getScan");
					wifiManager.startScan();
					lastScanTime = System.currentTimeMillis() / TIME;
					scanResultList = wifiManager.getScanResults();
					publishProgress(SIG_UPDATE);
					
					if (isCancelled())
					{
						this.cancel();
					}
				}
			};
			
			scanTimer = new Timer ();
			
			scanTimer.schedule(scanTask, TIME, TIME);
		}
		
		private void cancelTimer ()
		{
			scanTimer.cancel();
		}		
	}

}
