package pairings;

import java.util.Calendar;
import java.util.Date;

public class DateUtil {
	public static final long MILLISECONDS_PER_SECOND = 1000;
	public static final long SECONDS_PER_MINUTE = 60;
	
	public static int differenceInMinutes(Date before, Date after) {
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
}
