package kr.ac.snu.cse.wifiadvisor;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ListBaseAdapter extends BaseAdapter {
	private ArrayList<WiFiModel> scanResultList;
	private int measuredTime;
	private LayoutInflater inflater;
    
    public ListBaseAdapter(Context context) {
    	this.scanResultList = new ArrayList<WiFiModel>();
    	this.measuredTime = 0;
    	
		inflater = LayoutInflater.from(context);
    }

	@Override
	public int getCount() {
		return scanResultList.size();
	}

	@Override
	public Object getItem(int position) {
		return scanResultList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		ViewHolder viewHolder = new ViewHolder();
		
		if(v == null) {
			v = inflater.inflate(R.layout.list_view, null);
			
			viewHolder.ssid = (TextView) v.findViewById(R.id.ssid);
			viewHolder.bssid = (TextView) v.findViewById(R.id.bssid);
			viewHolder.frequency = (TextView) v.findViewById(R.id.frequency);
			viewHolder.level = (TextView) v.findViewById(R.id.level);
			viewHolder.avgSignal = (TextView) v.findViewById(R.id.avgSig);
			viewHolder.security = (TextView) v.findViewById(R.id.security);
			viewHolder.channel = (TextView) v.findViewById(R.id.channel);
			viewHolder.throughput = (TextView) v.findViewById(R.id.throughput);
			
			v.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) v.getTag();
		}
		NumberFormat formatter = new DecimalFormat("#0.0");
		
		viewHolder.ssid.setText("SSID : " + ((WiFiModel) getItem(position)).getSSID());
		viewHolder.bssid.setText("BSSID : " + ((WiFiModel) getItem(position)).getBSSID());
		viewHolder.frequency.setText("주파수 : " + ((WiFiModel) getItem(position)).getFrequency() + " MHz");
		if(((WiFiModel) getItem(position)).getLevel() == 0)
			viewHolder.level.setText("세기  : ");
		else
			viewHolder.level.setText("세기 : " + ((WiFiModel) getItem(position)).getLevel() + " dbm");
		viewHolder.avgSignal.setText("신호 평균전력  " + ((WiFiModel) getItem(position)).getAverageSignal(measuredTime) + " mW");
		viewHolder.security.setText("보안방식 : " + ((WiFiModel) getItem(position)).getSecurity());
		viewHolder.channel.setText("채널 번호 : " + ((WiFiModel) getItem(position)).getChannel());
		
		double t = ((WiFiModel) getItem(position)).getThroughput();
		if (t < 0)
		{
			viewHolder.throughput.setText("계산 중");
		}
		else
		{
			viewHolder.throughput.setText(formatter.format(t)+" Mbps");
		}
		return v;
	}

	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
	}

	public void setScanResultList(ArrayList<WiFiModel> scanResultList, int measuredTime) {
		this.scanResultList = scanResultList;
		this.measuredTime = measuredTime;
	}
	
	public ArrayList<WiFiModel> getScanResultList ()
	{
		return this.scanResultList;
	}

	class ViewHolder{
		private TextView ssid = null;
		private TextView bssid = null;
		private TextView frequency = null;
		private TextView level = null;
		private TextView avgSignal = null;
		private TextView security = null;
		private TextView channel = null;
		private TextView throughput = null;
	}

}
