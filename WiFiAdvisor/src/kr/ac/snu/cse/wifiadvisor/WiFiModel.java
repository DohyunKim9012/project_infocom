package kr.ac.snu.cse.wifiadvisor;

import android.net.wifi.ScanResult;

public class WiFiModel {
	// Field
	private String SSID;
	private String BSSID;
	private String security;
	private int frequency;
	private int level;
	private int channel;

	private double measuredEnergy; // energy in uJ
	
	public static final int[] channelFrequencies = {2412, 2417, 2422, 2427, 2432, 2437, 2442, 2447, 2452, 2457, 2462, 2467, 2472, 2484};
	public static final String[] securities = {"PSK", "WEP", "EAP"};
	public static final String adhoc = "[IBSS]";
	public static final String enterprise = "-EAP-";
	public static final String open = "Open";
	
	// Constructors
	
	// Make new data : convert ScanResult to WiFiData
	public WiFiModel(ScanResult scanResult, int time) {
		this.SSID = scanResult.SSID;
		this.BSSID = scanResult.BSSID;
		this.security = WiFiModel.getSecurity(scanResult.capabilities);
		this.frequency = scanResult.frequency;
		this.level = scanResult.level;
		this.channel = WiFiModel.getChannelfromFrequency(scanResult.frequency);
		
		this.measuredEnergy = 0.0d;
	}
	
	public static double dBm2mW (int dBm)
	{
		return Math.pow(10.0, dBm/10);
	}
	
	public static int getChannelfromFrequency (int frequency)
	{
		int i;
		for (i = channelFrequencies.length-1; i >= 0; i--)
		{
			if (channelFrequencies[i]==frequency)
			{
				break;
			}
		}
		return i;
	}
	
	public static String getSecurity (String capability)
	{
		for (int i = 0; i < securities.length; i++)
		{
			if (capability.contains(securities[i]))
			{
				return (capability.contains(adhoc) ? "(ADHOC)" : "(AP)") + securities[i];
			}
		}
		return (capability.contains(adhoc) ? "(ADHOC)" : "(AP)") + open;
	}
	
	public void update (int inteval, int level)
	{
		double dBm = WiFiModel.dBm2mW(level);
		measuredEnergy += dBm * inteval;
	}
	
	private double calculateAverageSignal (int measuredTime)
	{
		return (measuredTime == 0) ? 0.0d : measuredEnergy/measuredTime;
	}
	
	// Getter	
	public String getSSID() {
		return this.SSID;
	}	
	public String getBSSID() {
		return this.BSSID;
	}	
	public String getSecurity() {
		return this.security;
	}	
	public int getChannel() {
		return this.channel;
	}
	public int getFrequency() {
		return this.frequency;
	}	
	public int getLevel() {
		return this.level;
	}	
	public double getAverageSignal(int measuredTime) {
		return calculateAverageSignal (measuredTime);
	}

}
