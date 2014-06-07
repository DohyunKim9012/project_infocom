package kr.ac.snu.cse.wifiadvisor;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import kr.ac.snu.cse.wifiadvisor.WiFiService.WifiBinder;
import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends ListActivity {
	// WiFiService Variables
	private boolean isBound;
	private WiFiService wifiService;
	private ServiceConnection wifiServiceConnection;
	
	// WiFiData Management
	private Hashtable<String,WiFiModel> wifiDictionary;
	private ListBaseAdapter listBaseAdapter;
	private int measuredTime;
	private int intervalTime;
	private long lastScanTime;
	
	// User Interfaces
	private Button stopBtn;
	private Button startBtn;
	private EditText timeText;

	// Process Life Cycle
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
		Log.i("MainActivity", "onCreate");
		
		initialize ();
    }
    @Override
	protected void onResume() {
		super.onResume();
		Log.i("MainActivity", "onResume");
		
		if (measuredTime >= intervalTime)
		{
			startWiFiService ();
		}
	}
	@Override
	protected void onPause() {
		super.onPause();
		Log.i("MainActivity", "onPause");
		
		stopWiFiService ();
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		this.stopWiFiService();
		Log.i("MainActivity", "onDestroy");
	}
    
	// Private Functions
	private void initialize () {        
        listBaseAdapter = new ListBaseAdapter(this);
        setListAdapter(listBaseAdapter);
        
        measuredTime = 0;
        intervalTime = 0;
        lastScanTime = 0;
               
        timeText = (EditText)findViewById(R.id.timeintervalBox);
        
        stopBtn = (Button)findViewById(R.id.stopBtn);
        stopBtn.setOnClickListener(new OnClickListener(){
        	@Override
        	public void onClick (View v) {
       			wifiService.stopScan();
        	}
        });
        
        startBtn = (Button)findViewById(R.id.startBtn);
        startBtn.setOnClickListener(new OnClickListener(){
        	@Override
        	public void onClick (View v) {
        		measuredTime = 0;
        		
        		if (timeText.getText() != null)
        		{
        			intervalTime = Integer.parseInt(timeText.getText().toString()) * 1000;
        		}
        		else
        		{
        			intervalTime = 10000;
        		}
        		
        		if (intervalTime > 0)
        		{
           			wifiService.startScan();
        		}
        	}
        });

        
    	// Defines callback for service binding, passed to bindService()
        wifiServiceConnection = new ServiceConnection() {

    		/*
    		 * We've bound to LocalService, 
    		 * cast the IBinder and get LocalService instance
    		 */
    		@Override
    		public void onServiceConnected(ComponentName name, IBinder service) {
    			Log.i("MainActivity", "ServiceConnection/onServiceConnected");

    			WifiBinder localBinder = (WifiBinder) service;
    			wifiService = localBinder.getService();
                isBound = true;
    		}

    		@Override
    		public void onServiceDisconnected(ComponentName name) {
    			Log.i("MainActivity", "ServiceConnection/onServiceDisconnected");
    			
                isBound = false;
    		}
        };
        
        wifiDictionary = new Hashtable<String, WiFiModel>();
	}
    
    // WiFiService Functions
    public void startWiFiService ()
    {
		bindService(new Intent("android.intent.action.WiFiService"),
				wifiServiceConnection,
				Context.BIND_AUTO_CREATE);
		
		IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.WIFI");
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				Log.i("MainActivity", "onResume/BroadcastReceiver/onReceive");
				
				if(isBound && wifiService.getLastScanTime() > lastScanTime) {
					ArrayList<WiFiModel> wifiDataList = updateWifiData(
							wifiService.getScanResultList(),
							wifiService.TIME);
					lastScanTime = wifiService.getLastScanTime();
					measuredTime += wifiService.TIME;
					listBaseAdapter.setScanResultList(wifiDataList, measuredTime);
					listBaseAdapter.notifyDataSetChanged();
					
					timeText.setText(""+(intervalTime-measuredTime)/1000);
					
					if (measuredTime >= intervalTime)
					{
						wifiService.stopScan();
					}
				}
				
			}
			
        };
        registerReceiver(broadcastReceiver, intentFilter);
    }
    public void stopWiFiService ()
    {
		if(isBound) {
			unbindService(wifiServiceConnection);
			isBound = false;
		}
    }
	
	// Functions for ScanResult & WiFiModel data    
	
	// Update WiFiModel List by ScanResult List
	private ArrayList<WiFiModel> updateWifiData(List<ScanResult> scanResults, int time) {
		Log.i("MainActivity", "onReceive/updateWiFiData");

		for (ScanResult scanResult : scanResults)
		{
			String BSSID = scanResult.BSSID;
			
			WiFiModel model = wifiDictionary.get(BSSID);
			if (model != null)
			{
				model.update(time, scanResult.level);
			}
			else
			{
				wifiDictionary.put(BSSID, new WiFiModel(scanResult, time));
			}
		}
		
		return new ArrayList<WiFiModel>(wifiDictionary.values());
	}	
}
