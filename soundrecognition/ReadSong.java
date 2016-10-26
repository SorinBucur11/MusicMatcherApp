package soundrecognition;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;

import org.jtransforms.fft.DoubleFFT_1D;

import wavfile.WavFile;
import wavfile.WavFileException;

public class ReadSong {
	
	private static int SAMPLED_SIZE = 2048;
	//private final static int[] FREQUENCIES = new int[] {40, 80, 120, 180, 300};
	private final static int[] FREQUENCIES = new int[] {40, 70, 100};
	//private final static int[] FREQUENCIES = new int[] {120, 150, 200, 300};
	private static int NOISE_VALUE = 1;

	
	/**
	 * 
	 * @param song
	 * @return
	 */
	public static WavFile openSong(Song song) {
		
		WavFile wavFile = null;
		
		try {
			wavFile = WavFile.openWavFile(new File(song.getPath()));
		} catch (IOException | WavFileException e) {
			e.printStackTrace();
		}
		
		return wavFile;
	}
	
	
	/**
	 * 
	 * @param song
	 * @throws WavFileException 
	 * @throws IOException 
	 */
	public static long[] processSong(Song song) throws IOException, WavFileException {
		
		WavFile wavFile = openSong(song);
	
		int numberOfChannels = wavFile.getNumChannels();
		double numberOfBytes = wavFile.getNumFrames();
			
		double[] songBuffer = new double[(int) (numberOfBytes * numberOfChannels)];
		
		int framesRead = wavFile.readFrames(songBuffer);
		int numberOfSamples = framesRead / SAMPLED_SIZE;
		
		double[][] magnitudes = new double[numberOfSamples][];
		
		double[] sampledLine = new double[SAMPLED_SIZE];
		for (int sample = 0; sample < numberOfSamples; sample++) {
			
			for (int i = 0; i < SAMPLED_SIZE; i++) {
				
				sampledLine[i] = songBuffer [(sample * SAMPLED_SIZE) + i];		
			}
			
			sampledLine = hanningWindow(sampledLine);
			
			DoubleFFT_1D jt = new DoubleFFT_1D(SAMPLED_SIZE);
			jt.realForward(sampledLine);
			
			double[] sampledMagnitudes = calculateMagnitudes(sampledLine);
			magnitudes[sample] = sampledMagnitudes;
			
		}
		
		int[][] hashes = hashResult(magnitudes);
		//printHashArray(hashes, song);
		
		long[] hash = createHashLong(hashes, song);
		//printHashLong(hash, song);
		
		return hash;
		
	}
	
	
	/**
	 * 
	 * @param recordedData
	 * @return
	 */
	public static double[] hanningWindow(double[] recordedData) {

	    // iterate until the last line of the data buffer
	    for (int n = 1; n < recordedData.length / 2; n++) {
	        // reduce unnecessarily performed frequency part of each and every frequency
	        recordedData[n] *= 0.5 * (1 - Math.cos((2 * Math.PI * n)
	                / (recordedData.length / 2 - 1)));
	    }
	    // return modified buffer to the FFT function
	    return recordedData;
	}
	
	
	/**
	 * 
	 * @param sampledLine
	 * @return
	 */
	public static double[] calculateMagnitudes(double[] sampledLine) {

		double[] magnitudes = new double[sampledLine.length / 2];
		
		for (int i = 0; i < sampledLine.length / 2; i++) {
			magnitudes[i] = Math.sqrt(Math.pow(sampledLine[2 * i], 2) + Math.pow(sampledLine[2 * i + 1], 2));
		}
	    
		return magnitudes;
	}
	
	
	/**
	 * 
	 * @param magnitudes
	 * @return
	 */
	public static int[][] hashResult(double[][] magnitudes) {

		int[][] maxMagnitudes = new int[magnitudes.length][FREQUENCIES.length];
		double[][] maxFrequencies = new double[magnitudes.length][FREQUENCIES.length];
		//int length = FREQUENCIES.length - 1;
		
		for (int magnitude = 0; magnitude < magnitudes.length; magnitude++) {
			
		    //for (int frequency = 30; frequency < 300 ; frequency++) {
			for (int frequency = 30; frequency < 100 ; frequency++) {
				
		        int index = getIndex(frequency);

		        if (magnitudes[magnitude][frequency] > maxFrequencies[magnitude][index]) {
		        	maxMagnitudes[magnitude][index] = frequency;
		        	maxFrequencies[magnitude][index] = magnitudes[magnitude][frequency];
		        }
		    }
		    
		    //TODO
		    // form hash tag
		}
		
		return maxMagnitudes;
	}
	
	
	public static void printHashArray(int[][] magnitudes, Song song) {
		
		try {
			PrintWriter writer = new PrintWriter("array/" + song.getName() + ".txt");
			
			for(int i = 0; i < magnitudes.length; i++) {
				for (int j = 0; j < magnitudes[0].length; j++)
					writer.print(magnitudes[i][j] + " ");
				writer.println();
			}
			
			writer.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
	}
	
	
	public static void printHash(ArrayList<String> hash, Song song) {
		
		try {
			PrintWriter writer = new PrintWriter("hash/" + song.getName() + "_hash.txt");
			
			for(int i = 0; i < hash.size(); i++) {
				writer.println(hash.get(i));
			}
			
			writer.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
	}
	
	
	public static void printHashLong(long[] hash, Song song) {
		
		try {
			PrintWriter writer = new PrintWriter("hash/" + song.getName() + "_hash.txt");
			
			for(int i = 0; i < hash.length; i++) {
				writer.println(hash[i]);
			}
			
			writer.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
	}
	
	
	/**
	 * 
	 * @param freqcuencies
	 * @return
	 */
	public static int getIndex(int freqcuencies) {
		
	    int i = 0;
	    while (FREQUENCIES[i] <= freqcuencies)
	        i++;
	    
	    return i;
	}
	
	
	/**
	 * 
	 * @param magnitudes
	 * @param song
	 * @return
	 */
	public static ArrayList<String> createHash(int[][] hashes, Song song) {
		
		ArrayList<String> hash = new ArrayList<String>();
		
		for (int i = 0; i < hashes.length; i++) {
			hash.add(Arrays.toString(hashes[i]));
		}
		
		return hash;
	}
	
	
	/**
	 * 
	 * @param hashes
	 * @param song
	 * @param number
	 * @return
	 */
	public static long[] createHashLong(int[][] hashes, Song song) {
		
		long[] hash =  new long[hashes.length];
		
		for (int i = 0; i < hashes.length; i++) {
			for (int j = 0; j < 2 * hashes[0].length; j += 2) {
				int value = hashes[i][j / 2];
				hash[i] += (Math.pow(10, j) * (value - (value % NOISE_VALUE)));
			}
		}
		
		return hash;
	}
	
}
