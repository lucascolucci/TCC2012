package tcc.pairings;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import tcc.util.DateUtil;

public class Leg implements Cloneable {
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
	
	public int getFlightTime() {
		return DateUtil.difference(departure, arrival);
	}
	
	@Override
	public Leg clone() {
		return new Leg(number, from, to, departure, arrival, track);
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((from == null) ? 0 : from.hashCode());
		result = prime * result + number;
		result = prime * result + ((to == null) ? 0 : to.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != Leg.class && getClass() != DutyLeg.class)
			return false;
		Leg other = (Leg) obj;
		if (number != other.number)
			return false;
		if (from == null) {
			if (other.from != null)
				return false;
		} else if (!from.contentEquals(other.from))
			return false;
		if (to == null) {
			if (other.to != null)
				return false;
		} else if (!to.contentEquals(other.to))
			return false;
		return true;
	}

	@Override
	public String toString() {
		DateFormat daf = new SimpleDateFormat(DateUtil.DATE_FORMAT);
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
