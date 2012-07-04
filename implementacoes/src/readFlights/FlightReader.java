package readFlights;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FlightReader {
	private String fileName;

	public FlightReader(String fileName) {
		this.fileName = fileName;
	}

	public String getFileName() {
		return fileName;
	}
	
	public List<FlightLeg> getFlightsList() {
		List<FlightLeg> list = new ArrayList<FlightLeg>();
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");

		try {
			FileInputStream fstream = new FileInputStream(fileName);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String line;

			while ((line = br.readLine()) != null) {
				String[] fields = line.split("\t");
				int number = Integer.parseInt(fields[0]);
				String from = fields[1];
				String to = fields[2];
				Date departure = (Date) df.parse(fields[3]);
				Date arrival = (Date) df.parse(fields[4]);
				
				FlightLeg flightLeg = new FlightLeg(number, from, to, departure, arrival);
				list.add(flightLeg);
			}
			in.close();
		} 
		catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}

		return list;
	}

}
