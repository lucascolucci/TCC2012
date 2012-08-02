package tcc.pairings.io;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import tcc.pairings.Leg;
import tcc.pairings.Rules;

public class TimeTableReader {
	private String fileName;

	public String getFileName() {
		return fileName;
	}
	
	public TimeTableReader(String fileName) {
		this.fileName = fileName;
	}
	
	public List<Leg> getLegs() {
		try {
			return tryToGetLegs();
		} 
		catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
			return null;
		}
	}

	private List<Leg> tryToGetLegs() throws Exception {
		List<Leg> legs = new ArrayList<Leg>();
		readLinesAndAdd(legs);
		return legs;
	}

	private void readLinesAndAdd(List<Leg> legs) throws Exception {
		DataInputStream in = new DataInputStream(new FileInputStream(fileName));
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String line;
		while ((line = br.readLine()) != null) 
			legs.add(getLeg(line));
		in.close();
	}

	private Leg getLeg(String line) throws ParseException {
		DateFormat df = new SimpleDateFormat(Rules.DATE_FORMAT);
		String[] fields = line.split("\t");
		int number = Integer.parseInt(fields[0]);
		String from = fields[1];
		String to = fields[2];
		Date departure = (Date) df.parse(fields[3]);
		Date arrival = (Date) df.parse(fields[4]);
		Leg leg = new Leg(number, from, to, departure, arrival);
		if (fields.length == 6)
			leg.setTail(fields[5]);
		return leg;
	}
}
