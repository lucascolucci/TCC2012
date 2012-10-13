package tcc.pairings.tests;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.Test;

public class UtilTest {	
	private static Random random = new Random(0);
	
	public class TestClass {
		private int value;

		public int getValue() {
			return value;
		}

		public void setValue(int value) {
			this.value = value;
		}
		
		public TestClass(int value) {
			this.value = value;
		}
		
		@Override
		public boolean equals(Object o) {
			if (o == null || o.getClass() != TestClass.class)
				return false;
			if (this.value == ((TestClass) o).value)
				return true;
			return false;
		}
	}
	
	@Test
	public void testMathCeil() {
		double x = 2.01;
		assertEquals(3, (int) Math.ceil(x));
		double y = 2.00;
		assertEquals(2, (int) Math.ceil(y));
	}
	
	@Test
	public void testListContains() {
		TestClass t1 = new TestClass(0);
		TestClass t2 = new TestClass(0);
		List<TestClass> list = new ArrayList<TestClass>();
		list.add(t1);
		assertTrue(list.contains(t2));
	}
	
	@Test
	public void testRandom() {
		Object[] objects1 = getRandomObjects().toArray();
		Object[] objects2 = getRandomObjects().toArray();
		assertArrayEquals(objects1, objects2);
	}

	private List<Object> getRandomObjects() {
		random.setSeed(0);
		List<Object> objects = new ArrayList<Object>();
		for (int k = 0; k < 1000; k++)
			if (random.nextBoolean()) {
				for (int i = 0; i < 100; i++)
					objects.add(random.nextDouble());
				for (int i = 0; i < 100; i++)
					objects.add(random.nextBoolean());
				for (int i = 0; i < 100; i++)
					objects.add(random.nextInt(1000));
				for (int i = 0; i < 100; i++)
					objects.add(random.nextBoolean());
				for (int i = 0; i < 100; i++)
					objects.add(random.nextInt());
			}
		return objects;
	}
}
