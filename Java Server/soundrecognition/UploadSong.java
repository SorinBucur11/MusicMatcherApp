package soundrecognition;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import soundrecognition.Song;
import soundrecognition.Utils.SongOperation;

import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

@Path("/upload")
public class UploadSong {
	
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
    public String uploadSongs(@FormDataParam("file") InputStream file,
            @FormDataParam("file") FormDataContentDisposition fileDetail) throws IOException {

    	String JSONResponse = "";
    	String songName = fileDetail.getFileName();
    	songName = songName.substring(0, songName.length() - 4);
    	Song song = new Song(songName);
    	boolean result = upload(song);
    	
    	byte[] bytes = IOUtils.toByteArray(file);
    	FileOutputStream uploadedFile = new FileOutputStream(
    			new File("E:/workspace/MusicMatcherApp/songs/" + songName + ".wav"));
    	uploadedFile.write(bytes);
    	uploadedFile.flush();
    	uploadedFile.close();

		//check if uploaded
    	if (result == true) {
    		JSONResponse = Utils.constructUploadJSON(true, "");
    	}
    	else { 
    		JSONResponse = Utils.constructUploadJSON(false, "Something happened");
    	}
    	
    	return JSONResponse;
    }
	
	/**
	 * 
	 * @param song1
	 * @param song2
	 * @return
	 */
	private boolean upload(Song song1) {
		
		boolean result = false;
    	Song song2 = new Song();
    	
    	try {
    		result = DBConnection.databaseOperation(song1, song2, SongOperation.UPLOAD);
		} catch (Exception e) {
			result = false;
			e.printStackTrace();
		}
    	
    	return result;
	}
}
