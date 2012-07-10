package pairings.input;

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

import pairings.Leg;
import pairings.Rules;

public class TimeTableReader {
	private String fileName;

	public TimeTableReader(String fileName) {
		this.fileName = fileName;
	}

	public String getFileName() {
		return fileName;
	}
	
	public List<Leg> getLegs() {
		List<Leg> legs = new ArrayList<Leg>();
		try {
			FileInputStream fstream = new FileInputStream(fileName);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String line;
			while ((line = br.readLine()) != null) 
				legs.add(getLeg(line));
			in.close();
		} 
		catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
		return legs;
	}

	private Leg getLeg(String line) throws ParseException {
		DateFormat df = new SimpleDateFormat(Rules.DATE_FORMAT);
		String[] fields = line.split("\t");
		int number = Integer.parseInt(fields[0]);
		String from = fields[1];
		String to = fields[2];
		Date departure = (Date) df.parse(fields[3]);
		Date arrival = (Date) df.parse(fields[4]);
		return new Leg(number, from, to, departure, arrival);
	}
}
