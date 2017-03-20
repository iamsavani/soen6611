package gr.uom.java.jdeodorant.metrics.reusability;

import gr.uom.java.ast.TypeObject;
import gr.uom.java.ast.ClassObject;
import org.junit.*;
import gr.uom.java.ast.SystemObject;
import static org.junit.Assert.*;

public class UnderstandabilityCalculatorTest {
	
	@Test
	public void testCalculate_1(){
		SystemObject system = new SystemObject();
		double result = UnderstandabilityCalculator.calculate(system);
		System.out.print((result));
		assertEquals(Double.NaN, result, 0.1);
}
	@Test
	public void testCalculateAbstractMethod_1()
		throws Exception {
		ClassObject classObject = new ClassObject();
		int result = UnderstandabilityCalculator.calculateAbstractMethod(classObject);
		assertEquals(0, result);
	}

	@Test
	public void testCalculateAbstraction_1()
		throws Exception {
		SystemObject system = new SystemObject();
		double result = UnderstandabilityCalculator.calculateAbstraction(system);
		assertEquals(Double.NaN, result, 0.1);
	}

	
	@Test
	public void testCalculateCohesion_1()
		throws Exception {
		ClassObject classObject = new ClassObject();
		double result = UnderstandabilityCalculator.calculateCohesion(classObject);
		assertEquals(0.0, result, 0.1);
	}

	
	
	@Test
	public void testCalculateComplexity_1()
		throws Exception {
		SystemObject system = new SystemObject();
		double result = UnderstandabilityCalculator.calculateComplexity(system);
		assertEquals(Double.NaN, result, 0.1);
	}

	
	@Test
	public void testCalculateCoupling_1()
		throws Exception {
		SystemObject system = new SystemObject();
		double result = UnderstandabilityCalculator.calculateCoupling(system);
		assertEquals(Double.NaN, result, 0.1);
	}

	
	@Test
	public void testCalculateDepth_1()
		throws Exception {
		ClassObject classObject = new ClassObject();
		classObject.setSuperclass((TypeObject) null);
		int depth = 1;
		int result = UnderstandabilityCalculator.calculateDepth(classObject, depth);
		assertEquals(1, result);
	}

	
	@Test
	public void testCalculateDesignSize_1()
		throws Exception {
		SystemObject system = new SystemObject();
		double result = UnderstandabilityCalculator.calculateDesignSize(system);
		assertEquals(0.0, result, 0.1);
	}

	
	@Test
	public void testCalculateEncapsulation_1()
		throws Exception {
		SystemObject system = new SystemObject();
		double result = UnderstandabilityCalculator.calculateEncapsulation(system);
		assertEquals(Double.NaN, result, 0.1);
	}

	
	
	@Test
	public void testCalculatePolymorphism_1()
		throws Exception {
		SystemObject system = new SystemObject();
		double result = UnderstandabilityCalculator.calculatePolymorphism(system);
		assertEquals(0.0, result, 0.1);
	}

	
	@Test
	public void testCountPrivateProtected_1()
		throws Exception {
		ClassObject classObject = new ClassObject();
		int result = UnderstandabilityCalculator.countPrivateProtected(classObject);
		assertEquals(0, result);
	}


	public static void main(String[] args) {
		new org.junit.runner.JUnitCore().run(UnderstandabilityCalculatorTest.class);
	}
}