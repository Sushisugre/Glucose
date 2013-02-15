package cn.edu.tongji.sse.glucosemeter.data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import android.R.integer;

public class DataHandler {

	private ArrayList<Record> recordQueue = new ArrayList<Record>();

	public List<Record> getRecordQueue() {
		return recordQueue;
	}
	public Record getLastRecord(){
		return recordQueue.get(recordQueue.size()-1);
	}
	
	public void addRecord(Record record) {
		if (recordQueue.size() < 30) {
			recordQueue.add(record);
		}else{
			recordQueue.remove(0);
			recordQueue.add(record);
		}
	}
	
	public void clearRecords(){
		recordQueue.clear();
	}
	
	public Record generateRandomTestStrip(int year,int month,int day,int hour,int minute){
		Record record = new Record();
		Random rand = new Random();
		int dl_Record = rand.nextInt(1000);
		record.setdL_Record(dl_Record);
		int L_Record_Raw = dl_Record * 10 / 18;
		int L_Record_Integer = L_Record_Raw / 10;
		int L_Record_Decimal = L_Record_Raw - L_Record_Integer * 10;
		record.setL_Record_Integer(L_Record_Integer);
		record.setL_Record_Decimal(L_Record_Decimal);
		
		record.setYear(year);
		record.setMonth(month);
		record.setDay(day);
		record.setHour(hour);
		record.setMinute(minute);
		
		this.recordQueue.add(record);
		return record;
	}
}
