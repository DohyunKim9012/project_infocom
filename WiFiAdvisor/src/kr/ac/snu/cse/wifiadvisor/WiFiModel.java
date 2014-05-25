package kr.ac.snu.cse.wifiadvisor;

import android.net.wifi.ScanResult;

public class WiFiModel {
	// field
	
	private String SSID;
	private String BSSID;
	private String capabilities;
	private int frequency;
	private int level;
	private int time;
	private double avgSignal;
	
	// constructor

	// Make new data : from null data
	public WiFiModel() {
		super();
		
		SSID = null;
		BSSID = null;
		capabilities = null;
		frequency = 0;
		level = 0;
		time = 0;
		avgSignal = 0.0d;
	}
	
	// Make new data : convert ScanResult to WiFiData
	public WiFiModel(ScanResult scanResult, int time) {
		super();
		
		this.SSID = scanResult.SSID;
		this.BSSID = scanResult.BSSID;
		this.capabilities = scanResult.capabilities;
		this.frequency = scanResult.frequency;
		this.level = scanResult.level;
		this.time = time;
		this.avgSignal = scanResult.level;
	}
	
	// Calculate average signal
	public double calculateAverage(int interval) {
		double totalSig = (double)(time - interval) * avgSignal;
		totalSig += Math.pow(10.0, level) * (double)interval;
		double avgSignal = totalSig / (double)time;
		return avgSignal;
	}
	
	// getter
	
	public String getSSID() {
		return SSID;
	}
	
	public String getBSSID() {
		return BSSID;
	}
	
	public String getCapabilities() {
		return capabilities;
	}
	
	public int getFrequency() {
		return frequency;
	}
	
	public int getLevel() {
		return level;
	}
	
	public int getTime() {
		return time;
	}
	
	public double getAvgSignal() {
		return avgSignal;
	}
	
	// setter
	
	public void setSSID(String sSID) {
		SSID = sSID;
	}
	
	public void setBSSID(String bSSID) {
		BSSID = bSSID;
	}
	
	public void setCapabilities(String capabilities) {
		this.capabilities = capabilities;
	}
	
	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}
	
	public void setLevel(int level) {
		this.level = level;
	}
	
	public void setTime(int time) {
		this.time = time;
	}
	
	public void setAvgSig(double avgSignal) {
		this.avgSignal = avgSignal;
	}

}
