package tcc.pairings.tests;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
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
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + value;
			return result;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			TestClass other = (TestClass) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (value != other.value)
				return false;
			return true;
		}
		
		private UtilTest getOuterType() {
			return UtilTest.this;
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
		assertTrue(t1 != t2);
		List<TestClass> list = new ArrayList<TestClass>();
		list.add(t1);
		assertTrue(list.contains(t2));
		list.remove(t2);
		assertTrue(!list.contains(t2));
		assertTrue(!list.contains(t1));
		assertTrue(list.isEmpty());
	}
	
	@Test
	public void testHashSetContains() {
		TestClass t1 = new TestClass(0);
		TestClass t2 = new TestClass(0);
		assertTrue(t1 != t2);
		HashSet<TestClass> hash = new HashSet<TestClass>();
		hash.add(t1);
		assertTrue(hash.contains(t2));
	}
	
	@Test
	public void testHashMapContains() {
		TestClass t1 = new TestClass(0);
		TestClass t2 = new TestClass(0);
		assertTrue(t1 != t2);
		HashMap<TestClass, String> hash = new HashMap<TestClass, String>();
		hash.put(t1, "t1");
		assertTrue(hash.containsKey(t2));
		assertTrue(hash.keySet().contains(t2));
	}
	
	@Test
	public void testListEquals() {
		TestClass t1 = new TestClass(0);
		TestClass t2 = new TestClass(1);
		List<TestClass> list1 = new ArrayList<TestClass>();
		list1.add(t1); list1.add(t2);

		TestClass t3 = new TestClass(0);
		TestClass t4 = new TestClass(1);
		List<TestClass> list2 = new ArrayList<TestClass>();
		list2.add(t3); list2.add(t4);
		
		assertTrue(list1.equals(list2));
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
