package soundrecognition;

import javassist.bytecode.analysis.Analyzer;

public class Compare {
	
	private static int NOISE_VALUE = 10;
	

	/**
	 * 
	 * @param firstSong
	 * @param secondSong
	 * @return
	 */
	public static int[] numberOfMatches(long[] firstSong, long[] secondSong) {
		
		int[] result = new int[3];
		int matches = 0;
		int consecutiveMatches = 0, maxConsecutive = 0;
		
		int length = (firstSong.length > secondSong.length) ? firstSong.length : secondSong.length;
		
		for (int i = 0; i < length; i ++) {
			if (firstSong[i] == secondSong[i]) {
				matches++;
				consecutiveMatches++;
			}
			else {
				if (consecutiveMatches > maxConsecutive)
					maxConsecutive = consecutiveMatches;
				consecutiveMatches = 0;
			}
		}
		
		result[0] = matches;
		result[1] = length;
		result[2] = maxConsecutive;
		
		
		return result;
	}
	
	
	/**
	 * 
	 */
	public static boolean analyze(int[] result) {
		
		boolean analyze = true;
		
		if(DBConnection.songToCompare1.getName().charAt(0) != DBConnection.songToCompare2.getName().charAt(0))
			return false;
		else
			return true;
	}
	
	/**
	 * 
	 * @param firstSong
	 * @param secondSong
	 * @return
	 */
	public static int[] numberOfMatchesWithError(long[] firstSong, long[] secondSong) {
		
		int[] result = new int[3];
		int matches = 0;
		int consecutiveMatches = 0, maxConsecutive = 0;
		
		int length = (firstSong.length > secondSong.length) ? secondSong.length : firstSong.length;
		
		for (int i = 0; i < length; i ++) {
			if (checkValues(firstSong[i], secondSong[i])) {
				matches++;
				consecutiveMatches++;
			}
			else {
				if (consecutiveMatches > maxConsecutive)
					maxConsecutive = consecutiveMatches;
				consecutiveMatches = 0;
			}
		}
		
		result[0] = matches;
		result[1] = length;
		result[2] = maxConsecutive;
		
		if (!analyze(result)) {
			result[0] /= 12;
		}
		
		return result;
		
	}
	
	
	/**
	 * 
	 * @param song1
	 * @param song2
	 * @return
	 */
	public static boolean checkValues(long song1, long song2) {
		
		int modSong1, modSong2;
		
		while(song1 != 0) {
			
			modSong1 = (int) (song1 % 100);
			modSong2 = (int) (song2 % 100);
			
			if (Math.abs(modSong1 - modSong2) > NOISE_VALUE)
				return false;
			
			song1 /= 100;
			song2 /= 100;
		}
		
		return true;
	}
	
}
