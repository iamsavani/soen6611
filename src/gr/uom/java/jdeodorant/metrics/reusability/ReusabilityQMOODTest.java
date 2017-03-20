package gr.uom.java.jdeodorant.metrics.reusability;

import gr.uom.java.ast.ClassObject;
import org.junit.*;
import gr.uom.java.ast.SystemObject;
import static org.junit.Assert.*;

public class ReusabilityQMOODTest {

	@Test
	public void testCalculate_1()
		throws Exception {
		SystemObject system = new SystemObject();
		double result = ReusabilityQMOOD.calculate(system);
		assertEquals(Double.NaN, result, 0.1);
	}

	
	@Test
	public void testCalculateCohesion_1()
		throws Exception {
		ClassObject classObject = new ClassObject();
		double result = ReusabilityQMOOD.calculateCohesion(classObject);
		assertEquals(0.0, result, 0.1);
	}

	
	@Test
	public void testCalculateCoupling_1()
		throws Exception {
		SystemObject system = new SystemObject();

		double result = ReusabilityQMOOD.calculateCoupling(system);

		assertEquals(Double.NaN, result, 0.1);
	}

	
	@Test
	public void testCalculateDesignSize_1()
		throws Exception {
		SystemObject system = new SystemObject();
		double result = ReusabilityQMOOD.calculateDesignSize(system);
		assertEquals(0.0, result, 0.1);
	}

	@Test
	public void testCalculateMessaging_1()
		throws Exception {
		SystemObject system = new SystemObject();
		double result = ReusabilityQMOOD.calculateMessaging(system);
		assertEquals(Double.NaN, result, 0.1);
	}

	@Test
	public void testCalculateMessaging_2()
		throws Exception {
		SystemObject system = new SystemObject();
		double result = ReusabilityQMOOD.calculateMessaging(system);
		assertEquals(Double.NaN, result, 0.1);
	}

	@Test
	public void testCountPublicMessages_1()
		throws Exception {
		ClassObject classObject = new ClassObject();
		int result = ReusabilityQMOOD.countPublicMessages(classObject);
		assertEquals(0, result);
	}

	public static void main(String[] args) {
		new org.junit.runner.JUnitCore().run(ReusabilityQMOODTest.class);
	}
}