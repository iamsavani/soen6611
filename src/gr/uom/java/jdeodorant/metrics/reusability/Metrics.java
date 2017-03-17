package gr.uom.java.jdeodorant.metrics.reusability;

import java.io.File;
import java.util.HashMap;
import java.io.FileNotFoundException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.LinkedList;
import java.util.Stack;
import java.util.regex.*; 

public class Metrics {
	
	public static int DIT(File[] library, File objectClass) throws FileNotFoundException, IOException {
		HashMap<String, Integer> DIT = new HashMap<String, Integer>();
		HashMap<String, String> classExtension = new HashMap<String, String>();
		String line;
		
		for (int i = 0; i < library.length; i++) {
			
			BufferedReader reader = new BufferedReader(new FileReader(library[i]));
			
			boolean commentBlockOn = false;
			
			while ((line = reader.readLine()) != null) {
				
				boolean outsideComments = true;
				
				int commentLineIndex = line.indexOf("//");
				
				int firstCommentBlockIndex = line.indexOf("/*");
				
				if (firstCommentBlockIndex != -1)
					commentBlockOn = true;
				
				int lastCommentBlockIndex = line.indexOf("*/");
				
				if (lastCommentBlockIndex != -1)
					commentBlockOn = false;
				
				int firstQuotationMarkIndex = line.indexOf('"');
				int lastQuotationMarkIndex = line.indexOf('"', firstQuotationMarkIndex + 1);
				
				int extendsIndex = line.indexOf("extends");
				int openBraceIndex = line.indexOf('{');
				
				// check whether "extends" keyword and '{' are outside comments region
				
				if (openBraceIndex != -1 && extendsIndex != -1 && commentLineIndex != -1)
					if (openBraceIndex > commentLineIndex && extendsIndex > commentLineIndex)
						outsideComments = false;
				
				if (openBraceIndex != -1 && extendsIndex != -1 && firstCommentBlockIndex != -1 )
					if (openBraceIndex > firstCommentBlockIndex && extendsIndex > firstCommentBlockIndex)
						outsideComments = false;

				if (openBraceIndex != -1 && extendsIndex != -1 && lastCommentBlockIndex != -1)
					if (openBraceIndex < lastCommentBlockIndex && extendsIndex < lastCommentBlockIndex) 
						outsideComments = false;
					
				if (openBraceIndex != -1 && extendsIndex != -1 && firstQuotationMarkIndex != -1 && lastQuotationMarkIndex != -1)
					if (openBraceIndex > firstQuotationMarkIndex && openBraceIndex < lastQuotationMarkIndex &&
							extendsIndex > firstQuotationMarkIndex && extendsIndex < lastQuotationMarkIndex)
						outsideComments = false;
					
				if (openBraceIndex != -1 && extendsIndex != -1 && outsideComments && !commentBlockOn) {
					String className = library[i].getClass().getName();
					
					// increment the DIT of the current file from the input library
					
					if ( DIT.get(className) != null)
						DIT.put(className, DIT.get(className) + 1);
					else
						DIT.put(className, 1);
					
					String superClass = line.substring(extendsIndex + 7, openBraceIndex);
					superClass.trim();
					
					// map the super class with its subclass
					
					classExtension.put(superClass, className);
					String subclass = (String) classExtension.get(className);
					
					// increment the DIT of the related subclasses if needed
					
					while (subclass != null) {
						if (DIT.get(subclass) != null)
							DIT.put(subclass, DIT.get(subclass) + 1);
						else
							DIT.put(subclass, 1);
						
						subclass = (String) classExtension.get(subclass);
					}
				}
			}
		}
		
		return DIT.get(objectClass.getClass().getName());
	}
	
	public static int MAX_MCC(File code) throws FileNotFoundException, IOException {
		int MAX_MCC = 0;
		
		BufferedReader reader = new BufferedReader(new FileReader(code));
		
		String line;
		Stack<Character> braces = new Stack<Character>();
		
		int nodes = 2, edges = 1;
		
		int openBraceIndex = -1;
		int closeBraceIndex = -1;
				
		boolean commentBlockOn = false;
		
		while ((line = reader.readLine()) != null) {
			
			int commentLineIndex = line.indexOf("//");
			
			int firstCommentBlockIndex = line.indexOf("/*");
			
			if (firstCommentBlockIndex != -1)
				commentBlockOn = true;
			
			int lastCommentBlockIndex = line.indexOf("*/");
			
			if (lastCommentBlockIndex != -1)
				commentBlockOn = false;
			
			int firstQuotationMarkIndex = line.indexOf('"');
			int lastQuotationMarkIndex = line.indexOf('"', firstQuotationMarkIndex + 1);
			
			boolean outsideComments = true;
			
			openBraceIndex = line.indexOf('{');
			
			// check whether '{' are outside comments region
						
			if (openBraceIndex != -1 && commentLineIndex != -1)
				if (openBraceIndex > commentLineIndex)
					outsideComments = false;
			
			if (openBraceIndex != -1 && firstCommentBlockIndex != -1)
				if(openBraceIndex > firstCommentBlockIndex)
					outsideComments = false;
				
				
			if (openBraceIndex != -1 && lastCommentBlockIndex != -1)
				if (openBraceIndex < lastCommentBlockIndex) 
					outsideComments = false;
				
			if (openBraceIndex != -1 && firstQuotationMarkIndex != -1 && lastQuotationMarkIndex != -1)
				if (openBraceIndex > firstQuotationMarkIndex && openBraceIndex < lastQuotationMarkIndex )
					outsideComments = false;
			
			if (openBraceIndex != -1 && outsideComments && !commentBlockOn)
				braces.push('{');
		
			closeBraceIndex = line.indexOf('}');
			
			// check whether '}' are outside comments region
			
			if (closeBraceIndex != -1 && commentLineIndex != -1)
				if (closeBraceIndex > commentLineIndex)
					outsideComments = false;
			
			if (closeBraceIndex != -1 && firstCommentBlockIndex != -1)
				if (closeBraceIndex > firstCommentBlockIndex)
					outsideComments = false;
				
			if (closeBraceIndex != -1 && lastCommentBlockIndex != -1)
				if (closeBraceIndex < lastCommentBlockIndex) 
					outsideComments = false;
				
			if (closeBraceIndex != -1 && firstQuotationMarkIndex != -1 && lastQuotationMarkIndex != -1)
				if (closeBraceIndex > firstQuotationMarkIndex && closeBraceIndex < lastQuotationMarkIndex)
					outsideComments = false;
			
			// if size of the braces stack is 2, at the method level, compute the McCabe Cyclomatic 
			// Complexity and check whether it is a maximum value among the other methods, then reinitialize the number of edges and nodes
				
			if (closeBraceIndex != -1 && outsideComments && !commentBlockOn) {
				if (braces.size() == 2) {
					int C = edges - nodes + 2;
					
					if (C > MAX_MCC)
						MAX_MCC = C;
					
					nodes = 2;
					edges = 1;
				}
					
				braces.pop();
			}
			
			if (braces.size() >= 2) {
				
				// check whether ';' is outside comments region
				
				int semiColenIndex = line.indexOf(';');
				
				if (semiColenIndex != -1 && commentLineIndex != -1)
					if (semiColenIndex > commentLineIndex)
						outsideComments = false;
				
				if (semiColenIndex != -1 && firstCommentBlockIndex != -1 )
					if (semiColenIndex > firstCommentBlockIndex)
						outsideComments = false;

				if (semiColenIndex != -1 && lastCommentBlockIndex != -1)
					if (semiColenIndex < lastCommentBlockIndex) 
						outsideComments = false;
					
				if (semiColenIndex != -1 && firstQuotationMarkIndex != -1 && lastQuotationMarkIndex != -1)
					if (semiColenIndex > firstQuotationMarkIndex && semiColenIndex < lastQuotationMarkIndex)
						outsideComments = false;
				
				// for each statement delimited by ';', increase the number of nodes and edges by 1
					
				if (semiColenIndex != -1 && outsideComments && !commentBlockOn) {
					nodes++;
					edges++;
				}
					
				int ifStatementIndex = line.indexOf("if");
				
				// check whether "if" is outside comments region
				
				if (ifStatementIndex != -1 && commentLineIndex != -1)
					if (ifStatementIndex > commentLineIndex)
						outsideComments = false;

				if (ifStatementIndex != -1 && firstCommentBlockIndex != -1 )
					if (ifStatementIndex > firstCommentBlockIndex)
						outsideComments = false;

				if (ifStatementIndex != -1 && lastCommentBlockIndex != -1)
					if (ifStatementIndex < lastCommentBlockIndex) 
						outsideComments = false;
				
				if (ifStatementIndex != -1 && firstQuotationMarkIndex != -1 && lastQuotationMarkIndex != -1)
					if (ifStatementIndex > firstQuotationMarkIndex && ifStatementIndex < lastQuotationMarkIndex)
						outsideComments = false;
				
				// for each "if" statement, increase the number of nodes by 1 and edges by 2
				
				if (ifStatementIndex != -1 && outsideComments && !commentBlockOn) {
					nodes++;
					edges += 2;
				}
					
				int whileStatementIndex = line.indexOf("while");
				
				// check whether "while" is outside comments region
				
				if ( whileStatementIndex != -1 && commentLineIndex != -1)
					if (whileStatementIndex > commentLineIndex)
						outsideComments = false;
				
				if (whileStatementIndex != -1 && firstCommentBlockIndex != -1 )
					if (whileStatementIndex > firstCommentBlockIndex)
						outsideComments = false;

				if (whileStatementIndex != -1 && lastCommentBlockIndex != -1)
					if (whileStatementIndex < lastCommentBlockIndex) 
						outsideComments = false;
				
				if (whileStatementIndex != -1 && firstQuotationMarkIndex != -1 && lastQuotationMarkIndex != -1)
					if (whileStatementIndex > firstQuotationMarkIndex && whileStatementIndex < lastQuotationMarkIndex)
						outsideComments = false;
				
				// for each "while" statement, increase the number of nodes by 1 and edges by 3
				
				if (whileStatementIndex != -1 && outsideComments && !commentBlockOn) {
					edges += 3;
					nodes++;
				}
			}
		}
		
		reader.close();
					
		return MAX_MCC;
	}
	
	public static int MAX_LOC(File code) throws FileNotFoundException, IOException {
		int MAX_LOC = 0;
		
		BufferedReader reader = new BufferedReader(new FileReader(code));
		
		String line;
		Stack<Character> braces = new Stack<Character>();
		
		int LOC = 1;
		
		int openBraceIndex = -1;
		int closeBraceIndex = -1;
		
		boolean commentBlockOn = false;
		
		while ((line = reader.readLine()) != null) {
			
			boolean outsideComments = true;
			
			int commentLineIndex = line.indexOf("//");
			
			int firstCommentBlockIndex = line.indexOf("/*");
			
			if (firstCommentBlockIndex != -1)
				commentBlockOn = true;
			
			int lastCommentBlockIndex = line.indexOf("*/");
			
			if (lastCommentBlockIndex != -1)
				commentBlockOn = false;
			
			int firstQuotationMarkIndex = line.indexOf('"');
			int lastQuotationMarkIndex = line.indexOf('"', firstQuotationMarkIndex + 1);
						
			openBraceIndex = line.indexOf('{');
			
			// check whether '{' are outside comments region
			
			if (openBraceIndex != -1 && commentLineIndex != -1)
				if (openBraceIndex > commentLineIndex)
					outsideComments = false;
			
			if (openBraceIndex != -1 && firstCommentBlockIndex != -1)
				if(openBraceIndex > firstCommentBlockIndex)
					outsideComments = false;
				
				
			if (openBraceIndex != -1 && lastCommentBlockIndex != -1)
				if (openBraceIndex < lastCommentBlockIndex) 
					outsideComments = false;
				
			if (openBraceIndex != -1 && firstQuotationMarkIndex != -1 && lastQuotationMarkIndex != -1)
				if (openBraceIndex > firstQuotationMarkIndex && openBraceIndex < lastQuotationMarkIndex )
					outsideComments = false;
			
			if (openBraceIndex != -1 && outsideComments && !commentBlockOn) 
				braces.push('{');
		
			closeBraceIndex = line.indexOf('}');
			
			// check whether '}' are outside comments region
			
			if (closeBraceIndex != -1 && commentLineIndex != -1)
				if (closeBraceIndex > commentLineIndex)
					outsideComments = false;
			
			if (closeBraceIndex != -1 && firstCommentBlockIndex != -1)
				if (closeBraceIndex > firstCommentBlockIndex)
					outsideComments = false;
				
			if (closeBraceIndex != -1 && lastCommentBlockIndex != -1)
				if (closeBraceIndex < lastCommentBlockIndex) 
					outsideComments = false;
				
			if (closeBraceIndex != -1 && firstQuotationMarkIndex != -1 && lastQuotationMarkIndex != -1)
				if (closeBraceIndex > firstQuotationMarkIndex && closeBraceIndex < lastQuotationMarkIndex)
					outsideComments = false;
			
			// if size of the braces stack is 2, at the method level, compute the number of lines of statements 
			// and check whether this number is a maximum value among the other methods, then reinitialize it
				
			if (closeBraceIndex != -1 && outsideComments && !commentBlockOn) {
				if (braces.size() == 2) {
					
					if (LOC > MAX_LOC)
						MAX_LOC = LOC;
					
					LOC = 1;
				}
					
				braces.pop();
			}
			
			if (braces.size() >= 2) {
				
				int semiColenIndex = line.indexOf(';');
				
				// check whether ';' is outside comments region
				
				if (semiColenIndex != -1 && commentLineIndex != -1)
					if (semiColenIndex > commentLineIndex)
						outsideComments = false;
				
				if (semiColenIndex != -1 && firstCommentBlockIndex != -1 )
					if (semiColenIndex > firstCommentBlockIndex)
						outsideComments = false;

				if (semiColenIndex != -1 && lastCommentBlockIndex != -1)
					if (semiColenIndex < lastCommentBlockIndex) 
						outsideComments = false;
					
				if (semiColenIndex != -1 && firstQuotationMarkIndex != -1 && lastQuotationMarkIndex != -1)
					if (semiColenIndex > firstQuotationMarkIndex && semiColenIndex < lastQuotationMarkIndex)
						outsideComments = false;
				
				// for each statement delimited by ';', increase the number LOC
					
				if (semiColenIndex != -1 && outsideComments && !commentBlockOn)
					LOC++;
									
				int ifStatementIndex = line.indexOf("if");
				
				// check whether "if" is outside comments region
				
				if (ifStatementIndex != -1 && commentLineIndex != -1)
					if (ifStatementIndex > commentLineIndex)
						outsideComments = false;

				if (ifStatementIndex != -1 && firstCommentBlockIndex != -1 )
					if (ifStatementIndex > firstCommentBlockIndex)
						outsideComments = false;

				if (ifStatementIndex != -1 && lastCommentBlockIndex != -1)
					if (ifStatementIndex < lastCommentBlockIndex) 
						outsideComments = false;
				
				if (ifStatementIndex != -1 && firstQuotationMarkIndex != -1 && lastQuotationMarkIndex != -1)
					if (ifStatementIndex > firstQuotationMarkIndex && ifStatementIndex < lastQuotationMarkIndex)
						outsideComments = false;
				
				// for each "if" statement, increase the LOC by 1
				
				if (ifStatementIndex != -1 && outsideComments && !commentBlockOn)
					LOC++;
					
				int whileStatementIndex = line.indexOf("while");
				
				// check whether "while" is outside comments region
				
				if ( whileStatementIndex != -1 && commentLineIndex != -1)
					if (whileStatementIndex > commentLineIndex)
						outsideComments = false;
				
				if (whileStatementIndex != -1 && firstCommentBlockIndex != -1 )
					if (whileStatementIndex > firstCommentBlockIndex)
						outsideComments = false;

				if (whileStatementIndex != -1 && lastCommentBlockIndex != -1)
					if (whileStatementIndex < lastCommentBlockIndex) 
						outsideComments = false;
				
				if (whileStatementIndex != -1 && firstQuotationMarkIndex != -1 && lastQuotationMarkIndex != -1)
					if (whileStatementIndex > firstQuotationMarkIndex && whileStatementIndex < lastQuotationMarkIndex)
						outsideComments = false;
				
				// for each "while" statement, increase the LOC by 1
					
				if (whileStatementIndex != -1 && outsideComments && !commentBlockOn)
					LOC++;
			}
		}
		
		reader.close();
					
		return MAX_LOC;
	}
}
