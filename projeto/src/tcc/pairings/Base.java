package tcc.pairings;

import java.util.ArrayList;
import java.util.List;

public class Base {
	private List<String> airports;
	
	public List<String> getAirports() {
		return airports;
	}
	
	public Base(String airport) {
		this();
		airports.add(airport);
	}
	
	public Base(String[] airports) {
		this();
		for (String airport: airports) 
			this.airports.add(airport);
	}
	
	public Base() {
		airports = new ArrayList<String>();
	}
	
	public void addAirport(String airport) {
		airports.add(airport);
	}
}
