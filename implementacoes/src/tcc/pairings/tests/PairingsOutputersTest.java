package tcc.pairings.tests;

import static org.junit.Assert.*;

import java.io.*;

import org.junit.Before;
import org.junit.Test;

import tcc.pairings.PairingsGenerator;
import tcc.pairings.graph.networks.FlightNetwork;
import tcc.pairings.io.MpsOutputer;
import tcc.pairings.io.Outputer;
import tcc.pairings.io.TerminalOutputer;
import tcc.pairings.io.TextOutputer;
import tcc.pairings.io.TimeTableReader;

public class PairingsOutputersTest {
	private FlightNetwork net;
	
	private static final String TIME_TABLES_PATH = "./src/tcc/pairings/tests/time_tables/";
	private static final String OUTPUTS_PATH = "./src/tcc/pairings/tests/outputs/";
	
	@Before
	public void setUp() {
		TimeTableReader reader = new TimeTableReader(TIME_TABLES_PATH + "cgh_sdu_10.txt");
		net = new FlightNetwork(reader.getLegs());
		net.build();
	}
	
	@Test
	public void terminalOutputShouldBeRight() throws Exception {
		String logFile = OUTPUTS_PATH + "terminal.log";
		
		FileOutputStream fos = new FileOutputStream(logFile);
		PrintStream logStream = new PrintStream(fos);
		PrintStream out = System.out;
		System.setOut(logStream);
		
		TerminalOutputer terminal = new TerminalOutputer();
		Outputer[] outputers = new Outputer[] { terminal };
		PairingsGenerator generator = new PairingsGenerator(net, outputers);
		generator.generate("CGH");
		System.setOut(out);
		
		String expected = getContent(OUTPUTS_PATH + "cgh_sdu_10.pairings");
		String actual = getContent(logFile);
		assertEquals(expected, actual);
	}
	
	@Test
	public void textOutputShouldBeRight() throws Exception {
		String pairingsFile = OUTPUTS_PATH + "pairings.txt";
		
		TextOutputer text = new TextOutputer(pairingsFile);
		Outputer[] outputers = new Outputer[] { text };
		PairingsGenerator generator = new PairingsGenerator(net, outputers);
		generator.generate("CGH");
		text.close();
		
		String expected = getContent(OUTPUTS_PATH + "cgh_sdu_10.pairings");
		String actual = getContent(pairingsFile);
		assertEquals(expected, actual);
	}
	
	@Test
	public void mpsOutputShouldBeRight() throws Exception {
		String mpsFile = OUTPUTS_PATH + "in.mps";
		
		MpsOutputer mps = new MpsOutputer(net.getLegs(), mpsFile);
		Outputer[] outputers = new Outputer[] { mps };
		PairingsGenerator generator = new PairingsGenerator(net, outputers);
		mps.writeUntilColumns();
		generator.generate("CGH");
		mps.writeRhsAndBounds(generator.getNumberOfPairings());
		mps.close();
		
		String expected = getContent(OUTPUTS_PATH + "cgh_sdu_10.mps");
		String actual = getContent(mpsFile);
		assertEquals(expected, actual);
	}
	
	private String getContent(String fileName) throws Exception {
		DataInputStream in = new DataInputStream(new FileInputStream(fileName));
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		StringBuilder sb = new StringBuilder();
		
		String line;
		while ((line = br.readLine()) != null) 
			sb.append(line).append('\n');
		in.close();		
		
		return sb.toString();
	}
}
