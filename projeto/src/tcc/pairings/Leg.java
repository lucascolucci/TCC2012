package tcc.pairings;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import tcc.util.DateUtil;

public class Leg {
	private short number;
	private String from;
	private String to;
	private Date departure;
	private Date arrival;
	private short track;
	
	public short getNumber() {
		return number;
	}

	public void setNumber(short number) {
		this.number = number;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public Date getDeparture() {
		return departure;
	}

	public void setDeparture(Date departure) {
		this.departure = departure;
	}

	public Date getArrival() {
		return arrival;
	}

	public void setArrival(Date arrival) {
		this.arrival = arrival;
	}
	
	public short getTrack() {
		return track;
	}

	public void setTrack(short track) {
		this.track = track;
	}
		
	public Leg(short number, String from, String to, Date departure, Date arrival, short track) {
		this.number = number;
		this.from = from;
		this.to = to;
		this.departure = departure;
		this.arrival = arrival;
		this.track = track;
	}

	public boolean isDuplicate(Leg leg) {
		return number == leg.getNumber() && from.contentEquals(leg.getFrom()) && to.contentEquals(leg.getTo());
	}
	
	public int getFlightTime() {
		return DateUtil.difference(departure, arrival);
	}

	@Override
	public String toString() {
		DateFormat daf = new SimpleDateFormat(Rules.DATE_FORMAT);
		DecimalFormat def1 = new DecimalFormat("0000");
		DecimalFormat def2 = new DecimalFormat("000");
		StringBuilder sb = new StringBuilder();
		sb.append(def1.format(number)).append("  ");
		sb.append(from).append("-").append(to).append("  ");
		sb.append(daf.format(departure)).append("  ");
		sb.append(daf.format(arrival)).append("  ");
		sb.append(def2.format(track));
		return sb.toString();
	}	
}
