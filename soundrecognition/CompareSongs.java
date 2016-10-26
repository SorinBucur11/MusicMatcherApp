package soundrecognition;

import java.io.IOException;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import soundrecognition.Song;
import soundrecognition.Utils.SongOperation;
import wavfile.WavFileException;

@Path("/compare")
public class CompareSongs {
	
	@GET
    @Produces(MediaType.APPLICATION_JSON)
    public String compareSongs(@QueryParam("firstSong") String songName1, @QueryParam("secondSong") String songName2) throws IOException, WavFileException {
    	
    	Song song1 = new Song(songName1);
    	Song song2 = new Song(songName2);
    	String JSONResponse = "";
    	boolean result = compare(song1, song2);
    	
    	if (result == true) {
    		//doCompare
    		long[] firstSong = ReadSong.processSong(DBConnection.songToCompare1);
        	long[] secondSong = ReadSong.processSong(DBConnection.songToCompare2);
        	
        	int[] matchingResultError = Compare.numberOfMatchesWithError(firstSong, secondSong);
        	double similarity = matchingResultError[0] * 100 / matchingResultError[1] + matchingResultError[2] / 2;
    		JSONResponse = Utils.constructCompareJSON(true, similarity, "");
    	}
    	else { 
    		JSONResponse = Utils.constructCompareJSON(false, 0, "Something happened");
    	}
    	System.out.println(JSONResponse);
    	System.out.println(song1.getPath());
    	
    	return JSONResponse;
    }
	
	/**
	 * 
	 * @param song1
	 * @param song2
	 * @return
	 */
	private boolean compare(Song song1, Song song2) {
		
		boolean result = false;
    	
    	try {
    		result = DBConnection.databaseOperation(song1, song2, SongOperation.COMPARE);
		} catch (Exception e) {
			result = false;
			e.printStackTrace();
		}
    	
    	return result;
	}
}
