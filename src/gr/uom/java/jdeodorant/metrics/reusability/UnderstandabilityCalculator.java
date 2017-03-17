package gr.uom.java.jdeodorant.metrics.reusability;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import gr.uom.java.ast.MethodObject;
import gr.uom.java.ast.ASTReader;
import gr.uom.java.ast.Access;
import gr.uom.java.ast.ClassObject;
import gr.uom.java.ast.FieldInstructionObject;
import gr.uom.java.ast.FieldObject;
import gr.uom.java.ast.SystemObject;
import gr.uom.java.ast.TypeObject;

//Implementation for QMOOD Understandability metric 
public class UnderstandabilityCalculator {
	
	public static double calculate(SystemObject system) {
		
		double abstraction   = calculateAbstraction(system);
		double encapsulation   = calculateEncapsulation(system);
		double coupling   = calculateCoupling(system);
		double cohesion   = calculateCohesion(system);
		double polymorphism  = calculatePolymorphism(system);
		double complexity  = calculateComplexity(system);
		double designsize = calculateDesignSize(system);
		
		System.out.println("Abstraction: " + abstraction);
		System.out.println("Encapsulation: " + encapsulation);
		System.out.println("Coupling: " + coupling);
		System.out.println("Cohesion: " + cohesion);
		System.out.println("Polymorphism: " + polymorphism);
		System.out.println("Complexity: " + complexity);
		System.out.println("Design Size: " + designsize);

		
		
		return  -0.33*abstraction +
			     0.33*encapsulation -
			     0.33*coupling +
			     0.33*cohesion -
			     0.33*polymorphism -
			     0.5*complexity + 
			     0.5*designsize;
	}

	// Calcualte Abstraction 
	private static double calculateAbstraction(SystemObject system) {
		
		Map<String, Integer> allFieldMap = new HashMap<String, Integer>();

		Set<ClassObject> classes = system.getClassObjects();

		for (ClassObject classObject : classes) {
			//ListIterator<TypeObject> superClassObject = classObject.getSuperclassIterator();
			//TypeObject superClassObject = classObject.getSuperclass();
			int lengthSuper = calculateDepth(classObject, 0);
			
			allFieldMap.put(classObject.getName(), lengthSuper);
		}
		double nbClasses = classes.size();
        double totalSUMofDepth = 0.0;
        for(String key : allFieldMap.keySet()) {
        	totalSUMofDepth += allFieldMap.get(key);
        }
		return totalSUMofDepth/nbClasses;
	}
	
	private static int calculateDepth(ClassObject classObject, int depth) {
		TypeObject superclass = classObject.getSuperclass();
		if(superclass == null) {
			//System.out.println("SUPERCLASS NOT FOUND");
			return depth;
		} else {
			//calculateDepth(superClassObject, depth++);
			ClassObject spObject = ASTReader.getSystemObject().getClassObject(superclass.getClassType());
			if(spObject != null) {
				//System.out.println("SUPERCLASS FOUND");
				return calculateDepth(spObject,depth+1);
			} else {
				return depth;
			}
		}
		
	}
	
	// Calcualte Encapsulation
	private static double calculateEncapsulation(SystemObject system) {
		//Map<String, Double> allFieldMap = new HashMap<String, Double>();

		Set<ClassObject> classes = system.getClassObjects();
		double totalRatioSUM = 0.0;
		for (ClassObject classObject : classes) {
			List<FieldObject> fields = classObject.getFieldList();
			int allFieldSize = fields.size();
			//System.out.println(classObject.getName() + " ---- ++all field in class: " + allFieldSize);
			int PrivateProtectedSize = countPrivateProtected(classObject);
			//System.out.println(classObject.getName() + " ---- all Private Procted in class: " + PrivateProtectedSize);
			if(allFieldSize != 0) {
				double Ratio = ((double) PrivateProtectedSize)/allFieldSize;
				//System.out.println(Ratio);
				totalRatioSUM += Ratio;
				//allFieldMap.put(classObject.getName(), Ratio);
			}
			
		}
		//System.out.println(totalRatioSUM);
        double nbClasses = classes.size();
        
        /*for(String key : allFieldMap.keySet()) {
        	System.out.println(allFieldMap.get(key));
        	totalRatioSUM += allFieldMap.get(key);
        }*/
        
		return totalRatioSUM/nbClasses;

	}
	
	// Calculate Polymorphism
	private static double calculatePolymorphism(SystemObject system) {
		Map<String, Integer> allMethodMap = new HashMap<String, Integer>();

		Set<ClassObject> classes = system.getClassObjects();

		for (ClassObject classObject : classes) {
			int allMethodCount = calculateAbstractMethod(classObject);
			allMethodMap.put(classObject.getName(), allMethodCount);
		}
        int totalAbstractMethods = 0;
        for(String key : allMethodMap.keySet()) {
        	totalAbstractMethods += allMethodMap.get(key);
        }        
		return totalAbstractMethods;
	}
	
	private static  int calculateAbstractMethod(ClassObject classObject) {
		List<MethodObject> methods = classObject.getMethodList();
		int abstractMethods = 0;
		for (int i = 0; i < methods.size() - 1; i++) {
			if(methods.get(i).isAbstract()) {
				abstractMethods++;
			}		
		}
		return abstractMethods;
	}
	
	
	
	// Calculate Complexity
	private static double calculateComplexity(SystemObject system) {
		Map<String, Integer> allMethodMap = new HashMap<String, Integer>();

		Set<ClassObject> classes = system.getClassObjects();

		for (ClassObject classObject : classes) {
			List<MethodObject> methods = classObject.getMethodList();
			int allMethodCount = methods.size();
			allMethodMap.put(classObject.getName(), allMethodCount);
		}
        double nbClasses = classes.size();
        double totalAllMethods = 0.0;
        for(String key : allMethodMap.keySet()) {
        	totalAllMethods += allMethodMap.get(key);
        	//System.out.println( key + "  " +  allMethodMap.get(key));
        }
        
		return totalAllMethods/nbClasses;
	}
	
	// Calculate Design Size
	private static double calculateDesignSize(SystemObject system) {
		return system.getClassObjects().size();
	}

	// Calculate Cohesion 
	private static double calculateCohesion(SystemObject system) {
		
		Map<String, Double> cohesionMap = new HashMap<String, Double>();
		Set<ClassObject> classes = system.getClassObjects();
		
		for (ClassObject classObject : classes) {
			double classCohesion = calculateCohesion(classObject);
			cohesionMap.put(classObject.getName(), classCohesion);

		}
		
		double sumCohesion = 0.0;
		for(String key : cohesionMap.keySet()) {
			sumCohesion += cohesionMap.get(key);
        	//System.out.println( key + "  " +  cohesionMap.get(key));
        }
		
		return sumCohesion/classes.size();
	}


	private static  int countPrivateProtected(ClassObject classObject) {
		int result = 0;
		List<FieldObject> fields = classObject.getFieldList();
		for (int i = 0; i < fields.size(); i++) {
			FieldObject field = fields.get(i);
			if (field.getAccess() == Access.PRIVATE || field.getAccess() == Access.PROTECTED) {
				result++;
			}				
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
		
		List<MethodObject> methods = classObject.getMethodList();
		Set<String> allParameters = new HashSet<String>();
		
		for (int i = 0; i < methods.size() - 1; i++) {
			List<FieldInstructionObject> methodParameters = methods.get(i).getFieldInstructions();
			for (FieldInstructionObject param : methodParameters)
				allParameters.add(param.getType().toString());
			
		}
		
		if (allParameters.size() == 0)
			return 0;
		
		
		double sumIntersection = 0.0;
		
		for (int i = 0; i < methods.size() - 1; i++) {
			List<FieldInstructionObject> methodParameters = methods.get(i).getFieldInstructions();
			for (FieldInstructionObject param : methodParameters)
				if (allParameters.contains(param.getType().toString()))
						sumIntersection++;
			
		}
		
		return sumIntersection/(methods.size() * allParameters.size());
	}
}


