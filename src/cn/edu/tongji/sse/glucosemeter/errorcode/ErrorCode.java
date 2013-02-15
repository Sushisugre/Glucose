package cn.edu.tongji.sse.glucosemeter.errorcode;

public class ErrorCode {
	private int first_Digit;
	private int second_Digit;
	private int third_Digit;

	public ErrorCode(int first_Digit, int second_Digit, int third_Digit) {
		buildAllDigits(first_Digit, second_Digit, third_Digit);
	}

	public int getFirst_Digit() {
		return first_Digit;
	}

	public void setFirst_Digit(int first_Digit) {
		this.first_Digit = first_Digit;
	}

	public int getSecond_Digit() {
		return second_Digit;
	}

	public void setSecond_Digit(int second_Digit) {
		this.second_Digit = second_Digit;
	}

	public int getThird_Digit() {
		return third_Digit;
	}

	public void setThird_Digit(int third_Digit) {
		this.third_Digit = third_Digit;
	}

	public void buildAllDigits(int first_Digit, int second_Digit,
			int third_Digit) {
		setFirst_Digit(first_Digit);
		setSecond_Digit(second_Digit);
		setThird_Digit(third_Digit);
	}
}
