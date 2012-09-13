package tcc.pairings.tests;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;

import org.junit.Before;
import org.junit.Test;

import tcc.pairings.Base;
import tcc.pairings.generators.PairingsGenerator;
import tcc.pairings.graph.networks.FlightNetwork;
import tcc.pairings.io.CplexOutputer;
import tcc.pairings.io.MpsOutputer;
import tcc.pairings.io.Outputer;
import tcc.pairings.io.TerminalOutputer;
import tcc.pairings.io.TextOutputer;
import tcc.pairings.io.TimeTableReader;
import tcc.pairings.rules.Rules;

public class PairingsOutputersTest {
	private FlightNetwork net;
	
	@Before
	public void setUp() {
		Rules.MAX_DUTIES = 4;
		Rules.MIN_SIT_TIME = 25;
		Rules.MAX_LEGS = 5;
		Rules.MAX_TRACKS = 2;
		
		TimeTableReader reader = new TimeTableReader(FilePaths.TIME_TABLES + "cgh_sdu_10.txt");
		net = new FlightNetwork(reader.getLegs());
		net.build();	
	}
	
	@Test
	public void terminalOutputShouldBeRight() throws Exception {
		String logFile = FilePaths.OUTPUTS + "terminal.log";
		
		FileOutputStream fos = new FileOutputStream(logFile);
		PrintStream logStream = new PrintStream(fos);
		PrintStream out = System.out;
		System.setOut(logStream);
		
		TerminalOutputer terminal = new TerminalOutputer();
		Outputer[] outputers = new Outputer[] { terminal };
		PairingsGenerator generator = new PairingsGenerator(net, outputers);
		generator.generate(new Base("CGH"));
		System.setOut(out);
		
		String expected = getContent(FilePaths.OUTPUTS + "cgh_sdu_10.pairings");
		String actual = getContent(logFile);
		assertEquals(expected, actual);
	}
	
	@Test
	public void textOutputShouldBeRight() throws Exception {
		String pairingsFile = FilePaths.OUTPUTS + "pairings.txt";
		
		TextOutputer text = new TextOutputer(pairingsFile);
		Outputer[] outputers = new Outputer[] { text };
		PairingsGenerator generator = new PairingsGenerator(net, outputers);
		generator.generate(new Base("CGH"));
		text.close();
		
		String expected = getContent(FilePaths.OUTPUTS + "cgh_sdu_10.pairings");
		String actual = getContent(pairingsFile);
		assertEquals(expected, actual);
	}
	
	@Test
	public void mpsOutputShouldBeRight() throws Exception {
		String mpsFile = FilePaths.OUTPUTS + "in.mps";
		
		MpsOutputer mps = new MpsOutputer(net.getLegs(), mpsFile);
		Outputer[] outputers = new Outputer[] { mps };
		PairingsGenerator generator = new PairingsGenerator(net, outputers);
		mps.writeUntilColumns();
		generator.generate(new Base("CGH"));
		mps.writeRhsAndBounds(generator.getNumberOfPairings());
		mps.close();
		
		String expected = getContent(FilePaths.OUTPUTS + "cgh_sdu_10.mps");
		String actual = getContent(mpsFile);
		assertEquals(expected, actual);
	}
	
	@Test
	public void cplexOutputShouldBeRight() throws Exception {
		String cplexFile = FilePaths.OUTPUTS + "in.lp";

		CplexOutputer cplex = new CplexOutputer(net.getLegs());
		Outputer[] outputers = new Outputer[] { cplex };
		PairingsGenerator generator = new PairingsGenerator(net, outputers);
		cplex.addRows();
		generator.generate(new Base("CGH"));
		cplex.exportModel(cplexFile);

		String expected = getContent(FilePaths.OUTPUTS + "cgh_sdu_10.lp");
		String actual = getContent(cplexFile);
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
