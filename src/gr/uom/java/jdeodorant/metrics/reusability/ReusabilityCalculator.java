package gr.uom.java.jdeodorant.metrics.reusability;

public class ReusabilityCalculator {

	public static void main(String args[]) {
		
		// sample metrics data recorded during 4 days of development (N=4)
		int[] MAX_MCC = {10, 5, 3, 15};
		int[] MAX_LOC = {400, 1000, 300, 600};
		int[] CBO = {1, 2, 3, 4};
		int[] RFC = {40, 35, 20, 12};
		int[] WMC = {3, 10, 13, 1};
		int[] DIT = {1, 2, 1, 3};
		int[] NOC = {0, 1, 0, 0};
		int[] LCOM = {2, 1, 1, 1};
		
		int N = 4;
		int R = 1; // After 3 days of development, last is dedicated for refactoring (R=1)
		
		System.out.println( calculateReusability(MAX_MCC, MAX_LOC, CBO, RFC, WMC, DIT, NOC, LCOM, N, R) );
	}
	
	public static int calculateReusability(int[] MAX_MCC, int[] MAX_LOC, int[] CBO,
			int[] RFC, int[] WMC, int[] DIT, int[] NOC, int[] LCOM, int N, int R) {
		
		int[][] metric = {MAX_MCC, MAX_LOC, CBO, RFC, WMC, DIT, NOC, LCOM};
		int[] DeltaM = new int[metric.length]; // average number for each metric during the days of development
		int[] DeltaR = new int[metric.length]; // average number for each metric during the days of refactoring
		
		int reusability = 0; // number of times DeltaR is negative and is significantly smaller than DeltaM
							 // => refactoring improves quality of the classes written in the development phase
								
		                     // (DeltaR should be negative because we want the opposite of these metrics related
		                     //  to coupling)
		
							 // if we improved these metrics that are related to reusability => increased reusability
		
		for (int i = 0; i < metric.length; i++) {
			for (int k = 1; k < N; k++) {
				if (k < N - R)					
					DeltaM[i] += metric[i][k] - metric[i][k-1]; 
								
				else
					DeltaR[i] += metric[i][k] - metric[i][k-1];
				
			}
			
			DeltaM[i] /= N - R;
			DeltaR[i] /= R;
			
			if (DeltaR[i] < 0 && DeltaR[i] < DeltaM[i])
				reusability++;
			
		}
				
		return reusability;
	}
}
