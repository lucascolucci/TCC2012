package tcc.pairings;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Leg {
	private short number;
	private String from;
	private String to;
	private Date departure;
	private Date arrival;
	private String tail;
	
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
	
	public String getTail() {
		return tail;
	}

	public void setTail(String tail) {
		this.tail = tail;
	}
	
	public Leg(short number, String from, String to, Date departure, Date arrival, String tail) {
		this.number = number;
		this.from = from;
		this.to = to;
		this.departure = departure;
		this.arrival = arrival;
		this.tail = tail;
	}

	public Leg(short number, String from, String to, Date departure, Date arrival) {
		this.number = number;
		this.from = from;
		this.to = to;
		this.departure = departure;
		this.arrival = arrival;
		tail = null;
	}
	
	public boolean isTheSame(Leg leg) {
		return number == leg.getNumber() && from.contentEquals(leg.getFrom()) && to.contentEquals(leg.getTo());
	}

	@Override
	public String toString() {
		DateFormat df = new SimpleDateFormat(Rules.DATE_FORMAT);
		StringBuilder sb = new StringBuilder();
		sb.append(number).append("  ").append(from).append("->").append(to).append("  ");
		sb.append(df.format(departure)).append(" - ").append(df.format(arrival));
		if (tail != null)
			sb.append("  ").append(tail);
		return sb.toString();
	}	
}
