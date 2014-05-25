package kr.ac.snu.cse.wifiadvisor;

import java.util.ArrayList;
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
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class MainActivity extends ListActivity {
	private WiFiService wifiService;
	private boolean isBound;
	private WifiManager wifiManager;
	private ArrayList<WiFiModel> wifiDataList;
	private ListBaseAdapter listBaseAdapter;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
		Log.i("MainActivity", "onCreate");

		wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        wifiDataList = makeWifiData(wifiManager.getScanResults(), 0);
        listBaseAdapter = new ListBaseAdapter(this, wifiDataList);
        setListAdapter(listBaseAdapter);
    }

    @Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.i("MainActivity", "onResume");
		
		bindService(new Intent("android.intent.action.WiFiService"),
				wifiServiceConnection,
				Context.BIND_AUTO_CREATE);
		
		IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.WIFI");
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				// TODO Auto-generated method stub
				Log.i("MainActivity", "onResume/BroadcastReceiver/onReceive");
				
				if(isBound) {
					wifiDataList = updateWifiData(wifiDataList,
							wifiService.getScanResultList(),
							wifiService.TIME);
					listBaseAdapter.setScanResultList(wifiDataList);
					listBaseAdapter.notifyDataSetChanged();
				}
				
			}
			
        };
        registerReceiver(broadcastReceiver, intentFilter);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		Log.i("MainActivity", "onPause");
		
		if(isBound) {
			unbindService(wifiServiceConnection);
			isBound = false;
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.i("MainActivity", "onDestroy");
	}
	
	// Defines callbacks for service binding, passed to bindService()
    private ServiceConnection wifiServiceConnection = new ServiceConnection() {

		/*
		 * We've bound to LocalService, 
		 * cast the IBinder and get LocalService instance
		 */
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			// TODO Auto-generated method stub
			Log.i("MainActivity", "ServiceConnection/onServiceConnected");

			WifiBinder localBinder = (WifiBinder) service;
			wifiService = localBinder.getService();
            isBound = true;
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
			Log.i("MainActivity", "ServiceConnection/onServiceDisconnected");
			
            isBound = false;
		}
		
    };
	
	// Functions for ScanResult & WiFiModel data
	
	// Update null WiFiModel List by ScanResult List
	public ArrayList<WiFiModel> makeWifiData(List<ScanResult> scanResults, int time) {
		ArrayList<WiFiModel> tempList = new ArrayList<WiFiModel>();
		
		for(ScanResult scanResult : scanResults) {
			tempList.add(new WiFiModel(scanResult, time));
		}
		
		return tempList;
	}
	
	// Update WiFiModel List by ScanResult List
	private ArrayList<WiFiModel> updateWifiData(ArrayList<WiFiModel> wifiDataList, List<ScanResult> scanResults, int time) {
		ArrayList<WiFiModel> tempList = new ArrayList<WiFiModel>();
		
		for(WiFiModel wifiData : wifiDataList) {
			int index = indexOfSame(scanResults, wifiData);
			
			// If there is no same data, just convert
			if(index == -1)
				tempList.add(updateWifi(wifiData, time));
			else
				tempList.add(updateWifi(wifiData, scanResults.get(index), time));
		}
		
		for(ScanResult scanResult : scanResults) {
			if(!isContain(wifiDataList, scanResult))
				tempList.add(new WiFiModel(scanResult, time));
		}
		
		return tempList;
	}
	
	// Search WiFiModel List with ScanResult data
	private boolean isContain(ArrayList<WiFiModel> wifiDataList, ScanResult scanResult) {
		boolean result = false;
		
		for(WiFiModel wifiData : wifiDataList) {
			if(scanResult.BSSID.equals(wifiData.getBSSID())) {
				result = true;
				break;
			}
		}
		
		return result;
	}
	
	// Compare WiFiModel data with ScanResult List and get index of same data
	private int indexOfSame(List<ScanResult> scanResults, WiFiModel wifiData) {
		int index = -1;
		
		for(int x = 0; x < scanResults.size(); x++) {
			if(scanResults.get(x).BSSID.equals(wifiData.getBSSID())) {
				index = x;
				break;
			}
		}
		
		return index;
	}
	
	// Compare WiFiModel List with ScanResult List
	private boolean isSameWifi(WiFiModel wifiData, ScanResult scanResult) {
		return wifiData.getBSSID().equals(scanResult.BSSID);
	}
	
	// Update data : update WifiData with null ScanResult data
	private WiFiModel updateWifi(WiFiModel wifiData, int time) {
		wifiData.setLevel(0);
		wifiData.setTime(wifiData.getTime() + time);
		wifiData.setAvgSig(wifiData.calculateAverage(time));
		
		return wifiData;
	}
	
	// Update data : update WifiData by ScanResult data
	private WiFiModel updateWifi(WiFiModel wifiData, ScanResult scanResult, int time) {
		if(isSameWifi(wifiData, scanResult)) {
			wifiData.setLevel(scanResult.level);
			wifiData.setTime(wifiData.getTime() + time);
			wifiData.setAvgSig(wifiData.calculateAverage(time));
		}
		
		return wifiData;
	}

}
