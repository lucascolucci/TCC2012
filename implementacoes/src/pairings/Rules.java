package pairings;

public class Rules {
	public static final String DATE_FORMAT = "dd/MM/yyyy HH:mm";
	public static final int MAX_DUTIES = 4;
	public static final int MIN_SIT_TIME = 30;
	public static final int MAX_SIT_TIME = 120;  // 02 horas
	public static final int MIN_REST_TIME = 720;  // 12 horas
	public static final int MAX_REST_TIME = 2880; // 48 horas
	public static final int MAX_FLIGHT_TIME = 570;  // 9.5 horas
	public static final int MAX_DUTY_TIME = 690;  // 11.5 horas;
	public static final int MAX_LEGS = 5;
	
	public static boolean isLegalSitTime(int sit) {
		return (sit >= Rules.MIN_SIT_TIME && sit <= Rules.MAX_SIT_TIME);
	}
	
	public static boolean isLegalRestTime(int sit) {
		return (sit >= Rules.MIN_REST_TIME && sit <= Rules.MAX_REST_TIME);
	}
}
