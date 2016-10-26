package soundrecognition;

import java.io.IOException;

import soundrecognition.Utils.SongOperation;
import wavfile.WavFileException;

public class Main {

	public static void main(String[] args) throws IOException, WavFileException {
		// TODO Auto-generated method stub
		
		boolean result = false;
		long startTime = System.currentTimeMillis();
		Song song1 = new Song("drake_clar_converted");
		Song song2 = new Song("drake_nu_converted");
		String JSONResponse = "";
    	
    	try {
    		result = DBConnection.databaseOperation(song1, song2, SongOperation.COMPARE);
		} catch (Exception e) {
			result = false;
			e.printStackTrace();
		}
    	
    	if (result) {
    		System.out.println("first: " + DBConnection.songToCompare1.toString());
    		System.out.println("second: " + DBConnection.songToCompare2.toString());
    		JSONResponse = Utils.constructCompareJSON(true, 27, "");
    	}
    	
    	System.out.println(JSONResponse);
    	
    	//---
    	
    	long[] firstSong = ReadSong.processSong(DBConnection.songToCompare1);
    	long[] secondSong = ReadSong.processSong(DBConnection.songToCompare2);
    	
    	int[] matchingResultError = Compare.numberOfMatchesWithError(firstSong, secondSong);
    	System.out.println("no of matches are " + matchingResultError[0] + " out of " 
    						+ matchingResultError[1] + " possible with " + matchingResultError[2]
    						+ " consecutive.");
    	
    	long endTime = System.currentTimeMillis();
    	long totalTime = endTime - startTime;
    	System.out.println(totalTime);

	}

}
