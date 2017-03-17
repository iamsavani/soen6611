package gr.uom.java.jdeodorant.metrics.reusability;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import gr.uom.java.ast.MethodObject;
import gr.uom.java.ast.Access;
import gr.uom.java.ast.ClassObject;
import gr.uom.java.ast.FieldInstructionObject;
import gr.uom.java.ast.FieldObject;
import gr.uom.java.ast.SystemObject;

// Implementation for QMOOD reusability metric 
public class ReusabilityQMOOD {

	public static double calculate(SystemObject system) {
		double coupling   = calculateCoupling(system);
		double cohesion   = calculateCohesion(system);
		double messaging  = calculateMessaging(system);
		double designSize = calculateDesignSize(system);
		
		System.out.println("coupling: " + coupling);
		System.out.println("cohesion: " + cohesion);
		System.out.println("messaging: " + messaging);
		System.out.println("designSize: " + designSize);
		
		return  -0.25*coupling +
			     0.25*cohesion +
			     0.5*messaging + 
			     0.5*designSize;
	}

	private static double calculateDesignSize(SystemObject system) {
		return system.getClassObjects().size();
	}

	private static double calculateCohesion(SystemObject system) {
		
		System.out.println("Clculate Cohesion start");
		Map<String, Double> cohesionMap = new HashMap<String, Double>();
		Set<ClassObject> classes = system.getClassObjects();
		
		for (ClassObject classObject : classes) {
			double classCohesion = calculateCohesion(classObject);
			cohesionMap.put(classObject.getName(), classCohesion);

		}
		
		double sumCohesion = 0.0;
		for(String key : cohesionMap.keySet()) {
			sumCohesion += cohesionMap.get(key);
        	System.out.println( key + "  " +  cohesionMap.get(key));
        }
		
		System.out.println("Clculate Cohesion end");
		return sumCohesion/classes.size();
	}

	private static double calculateMessaging(SystemObject system) {

		Map<String, Integer> publicMethodsMap = new HashMap<String, Integer>();

		Set<ClassObject> classes = system.getClassObjects();

		for (ClassObject classObject : classes) {
			int publicMethodsCount = countPublicMessages(classObject);
			publicMethodsMap.put(classObject.getName(), publicMethodsCount);

		}
        double nbClasses = classes.size();
        double totalPublicMethods = 0.0;
        for(String key : publicMethodsMap.keySet()) {
        	totalPublicMethods += publicMethodsMap.get(key);
        	System.out.println( key + "  " +  publicMethodsMap.get(key));
        }
        
		return totalPublicMethods/nbClasses;
	}

	private static  int countPublicMessages(ClassObject classObject) {
		int result = 0;
		List<MethodObject> methods = classObject.getMethodList();
		for (int i = 0; i < methods.size() - 1; i++) {
			MethodObject method = methods.get(i);
			if (method.getAccess() == Access.PUBLIC)
				result++;
		}
		return result;
	}

	private static double calculateCoupling(SystemObject system) {
		Map<String, Integer> couplingMap = new HashMap<String, Integer>();
		
		Set<ClassObject> classes = system.getClassObjects();
		List<String> classTypes  = system.getClassNames();
		
		for (ClassObject classObject : classes) {
			List<String> cls = new ArrayList<String>(classTypes);
			
			cls.remove(classObject.getName());
			int directCoupling = calculateCoupling(classObject, cls);
			couplingMap.put(classObject.getName(), directCoupling);

		}
		
		
		double sumCoupling = 0.0;
		for(String key : couplingMap.keySet()) {
			sumCoupling += couplingMap.get(key);
        	System.out.println( key + "  " +  couplingMap.get(key));
        }
		return sumCoupling/classes.size();
		
	}
	
	
	
	private static int calculateCoupling(ClassObject classObject, List<String> otherClasses){
		Set<String> directClasses = new HashSet<String>();
		ListIterator<FieldObject> fieldIterator = classObject.getFieldIterator();
		while(fieldIterator.hasNext()) {
			FieldObject field = fieldIterator.next();
			if (otherClasses.contains(field.getClassName()))
				directClasses.add(field.getClassName());
		}
			
		List<MethodObject> methods = classObject.getMethodList();
		for (int i = 0; i < methods.size() - 1; i++) {
			List<FieldInstructionObject> methodParameters = methods.get(i).getFieldInstructions();
			for (FieldInstructionObject param : methodParameters)
				if(otherClasses.contains(param.getOwnerClass()))
					directClasses.add(param.getOwnerClass());
		}
		return directClasses.size();
	}
	
	private static double calculateCohesion(ClassObject classObject){
		System.out.println("--------  Calculate Cohesion for " + classObject);
		
		List<MethodObject> methods = classObject.getMethodList();
		Set<String> allParameters = new HashSet<String>();
		
		for (int i = 0; i < methods.size() - 1; i++) {
			List<FieldInstructionObject> methodParameters = methods.get(i).getFieldInstructions();
			for (FieldInstructionObject param : methodParameters)
				allParameters.add(param.getType().toString());
			
		}
		
		if (allParameters.size() == 0)
			return 0;
		
		for (String s : allParameters)
		   System.out.println("----------- param: " + s);
		
		double sumIntersection = 0.0;
		
		for (int i = 0; i < methods.size() - 1; i++) {
			List<FieldInstructionObject> methodParameters = methods.get(i).getFieldInstructions();
			for (FieldInstructionObject param : methodParameters)
				if (allParameters.contains(param.getType().toString()))
						sumIntersection++;
			
		}
		System.out.println("---- sumIntersection: " + sumIntersection);
		System.out.println("---- methods.size: " + methods.size());
		
		System.out.println("---- allParameters " + allParameters.size());
		
		return sumIntersection/(methods.size() * allParameters.size());
	}

}
