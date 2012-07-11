package pairings;

public class Rules {
	public static final String DATE_FORMAT          = "dd/MM/yyyy HH:mm";
	public static final int    MAX_DAYS_PER_PAIRING = 4;
	public static final int    MIN_SIT_TIME         = 30;
	public static final int    MAX_SIT_TIME         = 120;  // 02 horas
	public static final int    MIN_REST_TIME        = 720;  // 12 horas
	public static final int    MAX_REST_TIME        = 2880; // 48 horas
	
	public static boolean isLegalSitTime(int delta) {
		return (delta >= Rules.MIN_SIT_TIME && delta <= Rules.MAX_SIT_TIME);
	}
	
	public static boolean isLegalRestTime(int delta) {
		return (delta >= Rules.MIN_REST_TIME && delta <= Rules.MAX_REST_TIME);
	}
}
