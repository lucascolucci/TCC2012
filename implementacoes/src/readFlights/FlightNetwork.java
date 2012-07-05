package readFlights;

import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import readFlights.graph.Graph;

public class FlightNetwork extends Graph {
	private List<FlightLeg> legsList;
	
	public FlightNetwork(List<FlightLeg> legsList) {
		super();
		this.legsList = legsList;
	}
	
	public void build() {
		Calendar calendar = Calendar.getInstance();
		Iterator<FlightLeg> it = legsList.iterator();
		// Nao funciona, so para lembrar
		while (it.hasNext()) {
			FlightLeg leg = it.next();
			Date departure = leg.getDeparture();
			calendar.setTime(departure);
			calendar.add(Calendar.DATE, 1);
			departure = calendar.getTime();
		}
	}
}
