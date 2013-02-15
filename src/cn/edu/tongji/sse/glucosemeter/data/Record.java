package cn.edu.tongji.sse.glucosemeter.data;

public class Record {
	
	private int dL_Record;
	
	private int L_Record_Integer;
	private int L_Record_Decimal;
	
	private int year;
	private int month;
	private int day;
	private int hour;
	private int minute;
	
	public int getdL_Record() {
		return dL_Record;
	}
	public void setdL_Record(int dl_Record) {
		dL_Record = dl_Record;
	}
	public int getL_Record_Integer() {
		return L_Record_Integer;
	}
	public void setL_Record_Integer(int L_Record_Integer) {
		this.L_Record_Integer = L_Record_Integer;
	}
	public int getL_Record_Decimal() {
		return L_Record_Decimal;
	}
	public void setL_Record_Decimal(int L_Record_Decimal) {
		this.L_Record_Decimal = L_Record_Decimal;
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public int getMonth() {
		return month;
	}
	public void setMonth(int month) {
		this.month = month;
	}
	public int getDay() {
		return day;
	}
	public void setDay(int day) {
		this.day = day;
	}
	public int getHour() {
		return hour;
	}
	public void setHour(int hour) {
		this.hour = hour;
	}
	public int getMinute() {
		return minute;
	}
	public void setMinute(int minute) {
		this.minute = minute;
	}

}
