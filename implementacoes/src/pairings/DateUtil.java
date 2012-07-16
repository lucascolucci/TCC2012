package pairings;

import java.util.Calendar;
import java.util.Date;

public class DateUtil {
	public static final long MILLISECONDS_PER_SECOND = 1000;
	public static final long SECONDS_PER_MINUTE = 60;
	
	public static int difference(Date before, Date after) {
		long beforeInMilli = before.getTime();
		long afterInMilli = after.getTime();
		long minutes = (afterInMilli - beforeInMilli) / (MILLISECONDS_PER_SECOND * SECONDS_PER_MINUTE);
		return (int) minutes;
	}
	
	public static Date addOneDay(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DAY_OF_MONTH, 1);
		return calendar.getTime();
	}
	
	public static int getDayOfMonth(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar.get(Calendar.DAY_OF_MONTH);
	}
	
	public static boolean isSameDayOfMonth(Date date1, Date date2) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date1);
		int day1 = calendar.get(Calendar.DAY_OF_MONTH);
		calendar.setTime(date2);
		int day2 = calendar.get(Calendar.DAY_OF_MONTH);
		return day1 == day2;
	}
}
