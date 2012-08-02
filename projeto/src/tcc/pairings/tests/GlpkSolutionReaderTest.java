package tcc.pairings.tests;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import tcc.pairings.io.GlpkSolutionReader;

public class GlpkSolutionReaderTest {
	@Test
	public void itShouldReadTheRightOneVariables() {
		GlpkSolutionReader reader = new GlpkSolutionReader(FilePaths.OUTPUTS + "cgh_sdu_10.solution");
		Object[] expecteds = new Object[] { 22, 48, 69, 77, 92 };
		assertArrayEquals(expecteds, reader.getOneVariables().toArray());
	}
	
	@Test
	public void itShouldReadTheRightSolutionCost() {
		GlpkSolutionReader reader = new GlpkSolutionReader(FilePaths.OUTPUTS + "cgh_sdu_10.solution");
		assertEquals(1810, reader.getCost());
	}
}
